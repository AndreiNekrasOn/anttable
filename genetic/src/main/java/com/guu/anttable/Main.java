package com.guu.anttable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.json.*;

import com.guu.anttable.alg.GAJenetics;
import com.guu.anttable.utils.*;

public class Main {

    final static List<Activity> ACTIVITIES = new ArrayList<>();
    final static List<Timeslot> TIMESLOTS = new ArrayList<>();

    public static List<Activity> transformGroupsToActivities(List<Group> groups) {
        List<Activity> result = new ArrayList<>();
        for (Group g : groups) {
            for (SubjectTeacherPair stp : g.getRequiredClasses()) {
                result.add(new Activity(g, stp.getTeacher(), stp.getSubject()));
            }
        }
        return result;
    }

    public static List<Timeslot> generateTimeslots(int maxClasses, int maxDays) {
        List<Timeslot> result = new ArrayList<>();
        for (int weekday = 0; weekday < maxDays; weekday++) {
            for (int classNumber = 0; classNumber < maxClasses; classNumber++) {
                result.add(new Timeslot(weekday, classNumber));
            }
        }

        return result;
    }

    public static List<Group> parseGroupData(String path)
            throws IOException, JSONException {
        List<Group> allGroups = new ArrayList<>();
        InputStream is = Main.class.getResourceAsStream(path);
        String jsonString = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        JSONObject obj = new JSONObject(jsonString);
        Iterator<String> insts = obj.keys();
        while (insts.hasNext()) {
            String instName = insts.next();
            JSONObject inst = obj.getJSONObject(instName);
            Iterator<String> groups = inst.keys();
            while (groups.hasNext()) {
                String groupName = groups.next();
                Group currentGroup = new Group(groupName, new ArrayList<>());
                JSONObject jGroup = inst.getJSONObject(groupName);
                JSONArray classes = jGroup.getJSONArray("Предметы");
                for (int i = 0; i < classes.length(); i++) {
                    String className = classes.getJSONObject(i)
                            .getString("Название");
                    String teacherName = classes.getJSONObject(i)
                            .getString("Преподаватель");
                    int n = classes.getJSONObject(i).getInt("Количество");
                    for (int j = 0; j < n; j++) {
                        currentGroup.addClass(
                                new SubjectTeacherPair(
                                        new Subject(className),
                                        new Teacher(teacherName)));
                    }
                }
                allGroups.add(currentGroup);
             }
        }
        return allGroups;
    }

    public static void main(String[] args) {
        List<Group> plan;
        try {
            plan = parseGroupData(args[0]);
        } catch (IOException e) {
            System.err.println("Error in parseGroupsData: no such file");
            System.err.println(e);
            return;
        } catch (JSONException e) {
            System.err.println("Error in parseGroupsData: Invalid json");
            System.err.println(e);
            return;
        }
        GAJenetics engine = new GAJenetics(generateTimeslots(5, 12), transformGroupsToActivities(plan));
        Timetable bestTimetable = engine.run();
        // System.out.println(bestTimetable);
        System.out.println(bestTimetable.getFitnessScore());
    }
}
