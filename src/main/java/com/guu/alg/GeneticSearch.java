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
    private Timetable bestGlobalTimetable;
    private Timetable bestTimetable; // global max over all populations
    private List<Timetable> currentPopulation;
    private Timetable firstSelected;
    private Timetable secondSelected;
    private Random rng;
    
    private Timeslot[] timeslots;
    private List<Constraint> constraints;
    private DayFormat format;

    // can this be final?
    private int maxPopulationSize; 
    private int mutationRate;
    private int tournamentSize;
    private int maxIterations;
    private double stoppingFitness;
    

    // using implementation of Builder pattern from
    // http://rwhansen.blogspot.com/2007/07/theres-builder-pattern-that-joshua.html

    public static class Builder {
        private int maxPopulationSize;
        private int mutationRate;
        private int tournamentSize;
        private int maxIterations;
        private double stoppingFitness;

        private Timeslot[] timeslots;
        private List<Constraint> constraints;
        private DayFormat format;

        public Builder(Timeslot[] timeslots, List<Constraint> constraints,
                DayFormat format) {
            this.timeslots = timeslots;
            this.constraints = constraints;
            this.format = format;
            // default values
            maxPopulationSize = 20;
            mutationRate = 2;
            tournamentSize = 5;
            maxIterations = 1000;
            stoppingFitness = 0;

        }

        public Builder maxPopulationSize(int maxPopulationSize) {
            this.maxPopulationSize = maxPopulationSize;
            return this;
        }

        public Builder mutationRate(int mutationRate) {
            this.mutationRate = mutationRate;
            return this;
        }

        public Builder tournamentSize(int tournamentSize) {
            this.tournamentSize = tournamentSize;
            return this;
        }

        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Builder stoppingFitness(double stoppingFitness) {
            this.stoppingFitness = stoppingFitness;
            return this;
        }
        
        public GeneticSearch build() {
            GeneticSearch engine = new GeneticSearch(timeslots, constraints, format);
            engine.maxPopulationSize = maxPopulationSize;
            engine.mutationRate = mutationRate;
            engine.tournamentSize = tournamentSize;
            engine.maxIterations = maxIterations;
            engine.stoppingFitness = stoppingFitness;
            return engine;
        }
    }

    private GeneticSearch(Timeslot[] timeslots, List<Constraint> constraints,
            DayFormat format) {
        this.timeslots = timeslots;
        this.constraints = constraints;
        this.format = format;
        rng = new Random();
    }

    
    public void genesis(List<Activity> activities,
            Timeslot[] timeslots) {
        currentPopulation = new ArrayList<>();
        for (int i = 0; i < maxPopulationSize; i++) {
            List<ActivityTimeslot> preTT = new ArrayList<>();
            for (Activity a : activities) {
                int randomIdx = rng.nextInt(timeslots.length);
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
        if (bestGlobalTimetable == null || 
                bestGlobalTimetable.getFitnessScore() >  
                    bestTimetable.getFitnessScore()) {
            bestGlobalTimetable = bestTimetable;
        }
    }

    public void selection() {
        List<Timetable> tournament = new ArrayList<>();
        tournamentSize = 5;
        for (int i = 0; i < tournamentSize; i++) {
            int idx = rng.nextInt(currentPopulation.size());
            tournament.add(currentPopulation.get(idx));
        }
        firstSelected = tournament.stream()
                .min((a, b) -> (int) (a.getFitnessScore() - b.getFitnessScore()))
                .get();
        for (int i = 0; i < tournamentSize; i++) {
            int idx = rng.nextInt(currentPopulation.size());
            tournament.add(currentPopulation.get(idx));
        }
        secondSelected = tournament.stream()
                .min((a, b) -> (int) (b.getFitnessScore() - a.getFitnessScore()))
                .get();
    }

    public void evolution() {
        crossOver();
        firstSelected = mutate(firstSelected);
        secondSelected = mutate(secondSelected);
    }

    public void crossOver() {
        // assumes that order of activities is the same for all timetables
        int size = firstSelected.getClasses().size();
        int numberOfCrosses = rng.nextInt(size);
        for (int i = 0; i < numberOfCrosses; i++) {
            int idx = rng.nextInt(size);
            Timeslot tmp = firstSelected.getClasses().get(idx).getTimeslot();
            firstSelected.getClasses().get(idx).setTimeslot(
                    secondSelected.getClasses().get(idx).getTimeslot()    
            );
            secondSelected.getClasses().get(idx).setTimeslot(tmp);
        }
    }

    public Timetable mutate(Timetable t) {
        // select #mutationRate activities
        // switch their timeslots
        Timetable result = new Timetable(t.getClasses());
        
        int size = t.getClasses().size();
        for (int i = 0; i < mutationRate; i++) {
            int idxT = rng.nextInt(timeslots.length);
            int idxA = rng.nextInt(size);
            result.getClasses().get(idxA).setTimeslot(timeslots[idxT]);
        }
        return result;
    }

    public void iteration() {
        // assumes that population size is even.
        List<Timetable> newPopulation = new ArrayList<>();
        while (newPopulation.size() < currentPopulation.size()) {
            evaluation();
            selection();
            evolution();
            newPopulation.add(firstSelected);
            newPopulation.add(secondSelected);
        }
        currentPopulation = newPopulation;
    }

    public Timetable search() {
        // genesis must be run before this
        maxIterations = 100;
        stoppingFitness = 0;

        for (int i = 0; i < maxIterations; i++) {
            if (bestTimetable == null || bestTimetable.getFitnessScore() > 
                    stoppingFitness) {
                iteration();
            }
        }
        return bestGlobalTimetable;
    }

    public Timetable getBestTimetable() {
        return bestTimetable;
    }
    public List<Timetable> getCurrentPopulation() {
        return currentPopulation;
    }



}