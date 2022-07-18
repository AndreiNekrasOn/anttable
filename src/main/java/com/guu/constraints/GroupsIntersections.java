package com.guu.constraints;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.guu.utils.ActivityTimeslot;
import com.guu.utils.Timeslot;
import com.guu.utils.Timetable;


// TODO: Either Make AbstractInterscetions or intersection class generic
public class GroupsIntersections extends Constraint{
    private class MetaData {
        private String name;
        private Set<Timeslot> timeslots;
        private int intersections;
        public MetaData(String name, Set<Timeslot> timeslots) {
            this.name = name;
            this.timeslots = timeslots;
            this.intersections = 0;
        }
        
        @Override
        public String toString() {
            return "[intersections=" + intersections + ", name=" + name + 
                    ", timeslots=" + timeslots + "]\n";
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
            MetaData other = (MetaData) obj;
            return name.equals(other.getName());
        }   
    }

    public GroupsIntersections(Boolean hard) {
        setHard(true);
    }

    public double checkConstraint(Timetable gt) {
        List<ActivityTimeslot> ats = gt.getClasses();
        Set<MetaData>  groups = new HashSet<>();
        for (ActivityTimeslot a : ats) {
            groups.add(
                new MetaData(a.getActivity().getGroup().getName(), 
                        new HashSet<>()));
        }
    
        for (MetaData group : groups) {
            for (ActivityTimeslot a : ats) {
                if (!a.getActivity().getGroup().getName().equals(group.getName())) {
                    continue;
                }
                if (group.getTimeslots().contains(a.getTimeslot())) {
                    group.incrementIntersections();
                } else {
                    group.getTimeslots().add(a.getTimeslot());
                }
            }
        }


        return groups.stream()
                .mapToInt(MetaData::getIntersections)
                .sum();
    }
}
