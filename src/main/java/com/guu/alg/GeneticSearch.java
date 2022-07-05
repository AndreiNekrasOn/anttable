package com.guu.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.guu.constraints.Constraint;
import com.guu.utils.Activity;
import com.guu.utils.ActivityTimeslot;
import com.guu.utils.DayFormat;
import com.guu.utils.Timeslot;
import com.guu.utils.Timetable;

public class GeneticSearch {
    private Timetable bestTimetable; // global max over all populations
    private List<Timetable> currentPopulation;
    private List<Constraint> constraints;
    private int maxPopulationSize; 
    private DayFormat format;

    public GeneticSearch(int maxPopulationSize, DayFormat fmt, 
            List<Constraint> constraints) {
        this.maxPopulationSize = maxPopulationSize;
        this.format = fmt;
        this.constraints = constraints;
    }
    
    public void genesis(List<Activity> activities,
            Timeslot[] timeslots) {
        currentPopulation = new ArrayList<>();
        for (int i = 0; i < maxPopulationSize; i++) {
            List<ActivityTimeslot> preTT = new ArrayList<>();
            for (Activity a : activities) {
                int randomIdx = new Random().nextInt(timeslots.length);
                preTT.add(new ActivityTimeslot(a, timeslots[randomIdx], 
                    "cabinet", format));
            }
            currentPopulation.add(new Timetable(preTT));
        }
    }

    public void evaluation() {
        currentPopulation.forEach(element -> element.setFitnessScore(constraints));
        bestTimetable = currentPopulation.stream()
                .min((a, b) -> { 
                    return (int) (a.getFitnessScore() - b.getFitnessScore()); // bug ? don't know why cast to int
                })
                .orElse(null);
    }


    public Timetable getBestTimetable() {
        return bestTimetable;
    }
    public List<Timetable> getCurrentPopulation() {
        return currentPopulation;
    }



}