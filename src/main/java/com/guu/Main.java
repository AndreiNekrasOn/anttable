package com.guu;

import com.guu.alg.GeneticSearch;
import com.guu.constraints.Constraint;
import com.guu.constraints.GroupsIntersections;
import com.guu.constraints.TeacherIntersections;
import com.guu.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.json.*;

public class Main {

    public static List<Activity> transformGroupsToActivities(List<Group> groups) {
        List<Activity> result  = new ArrayList<>();
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
        String jsonString = Files.readString(Path.of(path));
        JSONObject obj = new JSONObject(jsonString);
        JSONObject inst = obj.getJSONObject("ИИС - 3"); 
        for (String gr: groups) {
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

    public static void gridSearch(Timeslot[] timeslotsArr, 
            List<Constraint> constraints, DayFormat format, 
            List<Activity> activities) {
        int randomSeed = 0;
        Random seedGenerator = new Random(randomSeed);
        for (int population = 10; population < 200; population += 10) {
            for (int maxIterations = 100; maxIterations < 10000; 
                    maxIterations += 100) {
                GeneticSearch engine = new GeneticSearch.Builder(
                        timeslotsArr, constraints, format)
                        .maxPopulationSize(population)
                        .maxIterations(maxIterations)
                        .randomSeed(seedGenerator.nextInt())
                        .build();
                engine.genesis(activities, timeslotsArr);

                Timetable bgt = engine.search();
                String result = bgt.getFitnessScore() + " "
                        + population + " "
                        + maxIterations + " ";
                for (int i = 2 - (int) bgt.getFitnessScore(); i >= 0; i--) {
                    result += "!";
                }
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) {
        DayFormat firstShift = new DayFormat(new ArrayList<>(
                List.of("8:15 - 8:55", "9:05 - 9:50", "10:00 - 11:45", 
                "12:30 - 13:15", "14:00 - 14:45")));
        List<Group> groups;
        try {
            String path = "src/class_requirements.json";
            List<String> groupsNames = List.of(
                    "ПМИ", "Бизнес-информатика-1", "Бизнес-информатика-2");
            groups = parseGroupData(path, groupsNames);
        } catch (IOException e) {
            System.err.println("Error in parseGroupsData: no such file");
            System.err.println(e);
            return;
        } catch (JSONException e) {
            System.err.println("Error in parseGroupsData: Invalid json");
            System.err.println(e);
            return;
        }
        List<Activity> activities = transformGroupsToActivities(groups);
        List<Timeslot> timeslots = generateTimeslots(5, 6);
        Timeslot[] timeslotsArr = new Timeslot[timeslots.size()];
        timeslots.toArray(timeslotsArr);    
        List<Constraint> constraints = List.of(
            new TeacherIntersections(true),
            new GroupsIntersections(true)
        );
        gridSearch(timeslotsArr, constraints, firstShift, activities);
        
        // GeneticSearch engine = 
        //         new GeneticSearch.Builder(timeslotsArr, constraints, firstShift)
        //             .maxPopulationSize(50)
        //             .build();
        // engine.genesis(activities, timeslotsArr);

        // Timetable bgt = engine.search();
        // System.out.println(bgt.getFitnessScore());
        // System.out.println(bgt);
    }
}
