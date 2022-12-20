package com.guu.anttable;

import com.guu.anttable.alg.GAJenetics;
import com.guu.anttable.constraints.*;
import com.guu.anttable.utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.json.*;

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

    public static List<Group> parseGroupData(String path, List<String> groups)
            throws IOException, JSONException {
        List<Group> allGroups = new ArrayList<>();
        InputStream is = Main.class.getResourceAsStream("/class_requirements.json");
        String jsonString = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        JSONObject obj = new JSONObject(jsonString);
        JSONObject inst = obj.getJSONObject("ИИС - 3");
        for (String gr : groups) {
            Group currentGroup = new Group(gr, new ArrayList<>());
            JSONArray classes = inst.getJSONObject(gr)
                    .getJSONArray("Предметы");
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
        return allGroups;
    }

    public static void main(String[] args) {
        DayFormat firstShift = new DayFormat(new ArrayList<>(
                List.of("8:15 - 8:55", "9:05 - 9:50", "10:00 - 11:45",
                        "12:30 - 13:15", "14:00 - 14:45")));
        List<Group> groups;
        try {
            List<String> groupsNames = List.of(
                    "ПМИ", "Бизнес-информатика-1", "Бизнес-информатика-2");
            groups = parseGroupData("", groupsNames);
        } catch (IOException e) {
            System.err.println("Error in parseGroupsData: no such file");
            System.err.println(e);
            return;
        } catch (JSONException e) {
            System.err.println("Error in parseGroupsData: Invalid json");
            System.err.println(e);
            return;
        }
        GAJenetics.initialize(
                generateTimeslots(5, 6),
                transformGroupsToActivities(groups), firstShift);
        Timetable bestTimetable = GAJenetics.run();
        System.out.println(bestTimetable);

        // check if result was correct
        List<Constraint> constraints = List.of(
                new TeacherIntersections(true),
                new GroupsIntersections(true));
        System.out.println(bestTimetable.checkConstraints(constraints));
    }
}
