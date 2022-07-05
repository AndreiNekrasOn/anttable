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
    private List<Constraint> constraints;
    private int maxPopulationSize; 
    private DayFormat format;
    private Timetable firstSelected;
    private Timetable secondSelected;
    private Random rng;
    private int mutationRate;
    private Timeslot[] timeslots;
    
    public GeneticSearch(int maxPopulationSize, DayFormat fmt, 
            List<Constraint> constraints) {
        this.maxPopulationSize = maxPopulationSize;
        this.format = fmt;
        this.constraints = constraints;
        this.rng = new Random();
        mutationRate = 2;
    }
    
    public void genesis(List<Activity> activities,
            Timeslot[] timeslots) {
        this.timeslots = timeslots;
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
                bestGlobalTimetable.getFitnessScore() <  bestTimetable.getFitnessScore()) {
            bestGlobalTimetable = bestTimetable;
        }
    }

    public void selection() {
        // Roulette Wheel Selection
        // p_i = \frac{\sum_i^N f_i - f_i}{\sum_i^N f_i} ?
        // numerator is such due to minimization of the fitness ?
        // current implementation might search for the worst result in our case
        // https://stackoverflow.com/questions/12774014/roulette-wheel-selection-for-genetic-algorithm-in-java

        // double denominator = currentPopulation.stream()
        //         .mapToDouble(Timetable::getFitnessScore).sum();
        // double random = rng.nextDouble() * denominator;
        // firstSelected = currentPopulation.get(0);
        // for (Timetable individual : currentPopulation) {
        //     random -= (individual.getFitnessScore());
        //     if (random <= 0) {
        //         break;
        //     } else {
        //         firstSelected = individual;
        //     }
        // }

        // random = rng.nextDouble() * denominator;
        // secondSelected = currentPopulation.get(0);
        // for (Timetable individual : currentPopulation) {
        //     random -= individual.getFitnessScore();
        //     if (random <= 0) {
        //         break;
        //     } else {
        //         secondSelected = individual;
        //     }
        // }

        // now tournament selection from

        List<Timetable> tournament = new ArrayList<>();
        int tournamentSize = 5;
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
        int maxIterations = 1000;
        double stoppingFitness = 0;

        for (int i = 0; i < maxIterations && 
                !(bestTimetable != null && bestTimetable.getFitnessScore() <= stoppingFitness); 
                i++) {
            iteration();
            System.out.printf("Iteration %d : best score %f\n", 
                    i, bestTimetable.getFitnessScore());
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