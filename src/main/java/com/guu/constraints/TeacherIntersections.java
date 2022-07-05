package com.guu.constraints;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.guu.utils.ActivityTimeslot;
import com.guu.utils.Timetable;

public class TeacherIntersections extends Constraint{
    private class TeacherMetaData {
        private String name;
        private Set<String> timeslots;
        private int intersections;
        public TeacherMetaData(String name, Set<String> timeslots) {
            this.name = name;
            this.timeslots = timeslots;
            this.intersections = 0;
        }
        
        public String getName() {
            return name;
        }
        public Set<String> getTimeslots() {
            return timeslots;
        }
        public int getIntersections() {
            return intersections;
        }
        public void incrementIntersections() {
            intersections += 1;
        }
        
        @Override
        public int hashCode(){
            return name.hashCode();
        } 

        @Override 
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TeacherMetaData other = (TeacherMetaData) obj;
            return name.equals(other.getName());
        }   
    }

    public TeacherIntersections(Boolean hard) {
        setHard(true);
    }

    public double checkConstraint(Timetable gt) {
        List<ActivityTimeslot> ats = gt.getClasses();
        Set<TeacherMetaData>  teachers = new HashSet<>();
        for (ActivityTimeslot a : ats) {
            teachers.add(
                new TeacherMetaData(a.getActivity().getTeacher().getName(), 
                        new HashSet<>()));
        }
        for (TeacherMetaData teacher : teachers) {
            for (ActivityTimeslot a : ats) {
                if (teacher.getTimeslots().contains(a.getTimeslot().toString())) {
                    teacher.incrementIntersections();
                } else {
                    teacher.getTimeslots().add(a.getTimeslot().toString());
                }
            }
        }

        
        return teachers.stream()
                .mapToInt(TeacherMetaData::getIntersections)
                .sum();
    }
}
