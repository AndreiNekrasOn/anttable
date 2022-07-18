package com.guu;

import com.guu.alg.GeneticSearch;
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

    public static List<Timeslot> generaTimeslots(int maxClasses, int maxDays) {
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
        Set<Teacher> teachers = new HashSet<>();
        Set<Subject> subjects = new HashSet<>();
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
        List<Timeslot> timeslots = generaTimeslots(5, 6);
        Timeslot[] timeslotsArr = new Timeslot[timeslots.size()];
        timeslots.toArray(timeslotsArr);    
        GeneticSearch engine = new GeneticSearch(20, 
                firstShift, new ArrayList<>(List.of(
                    new TeacherIntersections(true),
                    new GroupsIntersections(true))));
        engine.genesis(activities, timeslotsArr);

        Timetable bgt = engine.search();
        System.out.println(bgt.getFitnessScore());
        System.out.println(bgt);


    }
}
