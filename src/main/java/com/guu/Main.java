package com.guu;

import com.guu.alg.GeneticSearch;
import com.guu.constraints.TeacherIntersections;
import com.guu.utils.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import org.json.*;

public class Main {


    public static Timetable queueFillGroupsTimetable(DayFormat fmt, List<Group> groups, List<Timetable> otherTimetables) {
        if (otherTimetables != null) {
            System.out.println("Not implemented");
            return new Timetable(6, new ArrayList<>());
        }

        ArrayList<ActivityTimeslot> tcs = new ArrayList<>();
        for (Group group: groups) {
            Queue<SubjectTeacherPair> groupRequiredClasses = new LinkedList<>(group.getRequiredClasses());

            for (int weekday = 0; weekday < 6; weekday++) {
                for (int classNumber = 0; classNumber < 5; classNumber++) {
                    if (groupRequiredClasses.size() == 0) {
                        break;
                    }
                    SubjectTeacherPair currentClass = groupRequiredClasses.poll();
                    ActivityTimeslot tc = new ActivityTimeslot(
                            new Activity(group, currentClass.getTeacher(), currentClass.getSubject()),
                            new Timeslot(weekday, classNumber), "<classroom>", fmt);
                    tcs.add(tc);
                }
                if (groupRequiredClasses.size() == 0) {
                    break;
                }
            }
        }

        return new Timetable( 6, tcs);
    }

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

    public static List<Group> parseExampleGroupsData() throws IOException {
        /*
        ArrayList<Subject> subjects = new ArrayList<>();
        for (String s : List.of("Теория игр", "Численные методы", "Физкултура", "Английский", "МЛ", "ДЛ", "Фин.")) {
            subjects.add(new Subject(s));
        }
        ArrayList<Teacher> teachers = new ArrayList<>();
        for (String t : List.of("Прохоров Ю.Г.", "Антипова Е.С.", "Физкултура", "Английский", "Глазков А.В.",
                "Багров А.А", "Мишин Ю.В.")) {
            teachers.add(new Teacher(t));
        }

        ArrayList<SubjectTeacherPair> stps = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            stps.add(new SubjectTeacherPair(subjects.get(i), teachers.get(i)));
        }

        Teacher emptyTeacher = new Teacher("Empty");
        Subject emptySubject = new Subject("Empty");
        stps.add(new SubjectTeacherPair(emptySubject, emptyTeacher));

        ArrayList<SubjectTeacherPair> pmiRequiredClasses = new ArrayList<>(List.of(
                stps.get(0), stps.get(0),
                stps.get(1),stps.get(1),stps.get(1),
                stps.get(2),
                stps.get(3),
                stps.get(4), stps.get(4),
                stps.get(5), stps.get(5),
                stps.get(6), stps.get(6)));
        return new Group("ПМИ", pmiRequiredClasses);
        */
        List<Group> allGroups = new ArrayList<>();
        String jsonString = Files.readString(Path.of("src/class_requirements.json"));
        JSONObject obj = new JSONObject(jsonString);
        JSONObject inst = obj.getJSONObject("ИИС - 3"); 
        Set<Teacher> teachers = new HashSet<>();
        Set<Subject> subjects = new HashSet<>();
        for (String gr: List.of("ПМИ", "Бизнес-информатика-1", "Бизнес-информатика-2")) {
            Group currentGroup = new Group(gr, new ArrayList<>());
            JSONArray classes = inst.getJSONObject(gr).getJSONArray("Предметы");
            for (int i = 0; i < classes.length(); i++) {
                String className = classes.getJSONObject(i).getString("Название");
                String teacherName = classes.getJSONObject(i).getString("Преподаватель");
                int n = classes.getJSONObject(i).getInt("Количество");
                for (int j = 0; j < n; j++) {
                    currentGroup.addClass(new SubjectTeacherPair(new Subject(className), new Teacher(teacherName)));
                }
            }
            allGroups.add(currentGroup);
        }

        return allGroups;
    }



    public static void main(String[] args) {
        DayFormat firstShift = new DayFormat(new ArrayList<>(List.of("8:15 - 8:55", "9:05 - 9:50",
                "10:00 - 11:45", "12:30 - 13:15", "14:00 - 14:45")));

        List<Group> groups;
        try {
            groups = parseExampleGroupsData();
        } catch (IOException e) {
            System.err.println("Error in parseGroupsData: no such file");
            System.err.println(e);
            return;
        }
        
        List<Activity> activities = transformGroupsToActivities(groups);
        // Timetable gt = queueFillGroupsTimetable(firstShift, groups, null);
        List<Timeslot> timeslots = generaTimeslots(5, 6);
        Timeslot[] timeslotsArr = new Timeslot[timeslots.size()];
        timeslots.toArray(timeslotsArr);
        
        GeneticSearch engine = new GeneticSearch(300, 
                firstShift, new ArrayList<>(List.of(
                    new TeacherIntersections(true))));
        engine.genesis(activities, timeslotsArr);
        engine.search();
    }
}
