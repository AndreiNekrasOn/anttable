package com.guu.anttable.constraints;

import java.util.*;

import com.guu.anttable.utils.*;

public class TeacherIntersections extends Constraint {

    public TeacherIntersections(Boolean hard) {
        setHard(true);
    }

    public double checkConstraint(Timetable gt) {
        List<ActivityTimeslot> ats = gt.getClasses();
        Set<TeacherMetaData> teachers = new HashSet<>();
        for (ActivityTimeslot a : ats) {
            teachers.add(
                    new TeacherMetaData(a.getActivity().getTeacher().getName(),
                            new HashSet<>()));
        }

        for (TeacherMetaData teacher : teachers) {
            for (ActivityTimeslot a : ats) {
                if (!a.getActivity().getTeacher().getName().equals(teacher.getName())) {
                    continue;
                }
                if (teacher.getTimeslots().contains(a.getTimeslot())) {
                    teacher.incrementIntersections();
                } else {
                    teacher.getTimeslots().add(a.getTimeslot());
                }
            }
        }

        return teachers.stream()
                .mapToInt(TeacherMetaData::getIntersections)
                .sum();
    }

    private class TeacherMetaData {

        private String name;
        private Set<Timeslot> timeslots;
        private int intersections;

        public TeacherMetaData(String name, Set<Timeslot> timeslots) {
            this.name = name;
            this.timeslots = timeslots;
            this.intersections = 0;
        }

        @Override
        public String toString() {
            return "TeacherMetaData [intersections=" + intersections +
                    ", name=" + name + ", timeslots=" + timeslots + "]\n";
        }

        public String getName() {
            return name;
        }

        public Set<Timeslot> getTimeslots() {
            return timeslots;
        }

        public int getIntersections() {
            return intersections;
        }

        public void incrementIntersections() {
            if (name.equals("Физкультура") || name.equals("Англ")) {
                return;
            }
            intersections += 1;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TeacherMetaData other = (TeacherMetaData) obj;
            return name.equals(other.getName());
        }
    }
}
