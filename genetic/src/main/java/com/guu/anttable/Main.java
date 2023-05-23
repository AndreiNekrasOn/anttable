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
    public final static int MAX_CLASSES = 5;
    public final static int DAYS_IN_WEEK = 12;

    public static List<Activity> transformGroupsToActivities(Map<String, List<SubjectTeacherPair>> studyPlan) {
        List<Activity> result = new ArrayList<>();
        for (var g : studyPlan.entrySet()) {
            for (var subjectTeacherPair : g.getValue()) {
                result.add(new Activity(g.getKey(), subjectTeacherPair.teacher(), subjectTeacherPair.subject()));
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

    public static Map<String, List<SubjectTeacherPair>> parseGroupData(String path)
            throws IOException, JSONException {
        Map<String, List<SubjectTeacherPair>> studyPlan = new HashMap<>();
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
                studyPlan.putIfAbsent(groupName, new ArrayList<>());
                JSONObject jGroup = inst.getJSONObject(groupName);
                JSONArray classes = jGroup.getJSONArray("Предметы");
                for (int i = 0; i < classes.length(); i++) {
                    String className = classes.getJSONObject(i)
                            .getString("Название");
                    String teacherName = classes.getJSONObject(i)
                            .getString("Преподаватель");
                    int n = classes.getJSONObject(i).getInt("Количество");
                    for (int j = 0; j < n; j++) {
                        studyPlan.get(groupName).add(new SubjectTeacherPair(className, teacherName));
                    }
                }
             }
        }
        return studyPlan;
    }

    public static void toCsv(Timetable t) {
        Map<String, List<ActivityTimeslot>> classesByGroup = t.classes().stream()
                .collect(Collectors.groupingBy(c->c.activity().group()));


        Map<String, String[][]> groupTimetableMatrix = new HashMap<>();

        for (var classes : classesByGroup.entrySet()) {
            String[][] timetableMatrix = new String[MAX_CLASSES][DAYS_IN_WEEK];
            for (int i = 0; i < MAX_CLASSES; i++) {
                for (int j = 0; j < DAYS_IN_WEEK; j++) {
                    timetableMatrix[i][j] = "";
                }
            }
            for (var activity : classes.getValue()) {
                if ("".equals(timetableMatrix[activity.timeslot().classNumber()][activity.timeslot().weekday()])) {
                    timetableMatrix[activity.timeslot().classNumber()][activity.timeslot().weekday()] = 
                            String.format("%s (%s)", activity.activity().subject(), activity.activity().teacher());
                } else {
                    timetableMatrix[activity.timeslot().classNumber()][activity.timeslot().weekday()] += String.format("/%s", activity.activity().subject());
                    System.out.println("FUCK " + classes.getKey());
                }
            }
            groupTimetableMatrix.put(classes.getKey(), timetableMatrix);
        }

        for (var groupTT : groupTimetableMatrix.entrySet()) {
            System.out.println("\t\t" + groupTT.getKey());
            String[][] tt = groupTT.getValue();
            for (var row : tt) { 
                for (var item : row) {
                    System.out.format("%60s| ", item);
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        Map<String, List<SubjectTeacherPair>> plan;
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
        GAJenetics engine = new GAJenetics(generateTimeslots(MAX_CLASSES, DAYS_IN_WEEK), transformGroupsToActivities(plan));
        Timetable bestTimetable = engine.run();
        toCsv(bestTimetable);

    }
}
