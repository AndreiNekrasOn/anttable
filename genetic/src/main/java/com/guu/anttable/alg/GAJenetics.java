package com.guu.anttable.alg;

import java.util.*;
import java.util.stream.IntStream;

import org.jenetics.*;
import org.jenetics.engine.*;
import org.jenetics.util.*;

import com.guu.anttable.constraints.IntersectionsCounter;
import com.guu.anttable.constraints.WindowCounter;
import com.guu.anttable.utils.*;

public class GAJenetics {

    final static List<Timeslot> TIMESLOTS = new ArrayList<>();
    final static List<Activity> ACTIVITIES = new ArrayList<>();
    final static Set<NamedEntity> GROUPS = new HashSet<>();
    final static Set<NamedEntity> TEACHERS = new HashSet<>();
    static DayFormat fmt;

    public static void initialize(List<Timeslot> timeslots, List<Activity> activities, DayFormat fmt) {
        TIMESLOTS.clear();
        ACTIVITIES.clear();
        TIMESLOTS.addAll(timeslots);
        ACTIVITIES.addAll(activities);
        GAJenetics.fmt = fmt;
        decoupleActivities();
    }

    private static void decoupleActivities() {
        for (Activity a : ACTIVITIES) {
            GROUPS.add(a.getGroup());
            TEACHERS.add(a.getTeacher());
        }
    }

    public static Timetable decode(Genotype<IntegerGene> bestGenotype) {
        final List<ActivityTimeslot> classes = new ArrayList<>();
        Chromosome<IntegerGene> c = bestGenotype.getChromosome();
        IntStream.range(0, c.length()).forEach(i -> {
            ActivityTimeslot at = new ActivityTimeslot(ACTIVITIES.get(i),
                    TIMESLOTS.get(c.getGene(i).intValue()), "",
                    GAJenetics.fmt);
            classes.add(at);
        });
        return new Timetable(classes);
    }

    public static Timetable run() {
        EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(IntegerChromosome.of(
                IntRange.of(0, TIMESLOTS.size() - 1), ACTIVITIES.size()));
        final Engine<IntegerGene, Double> engine = Engine
                .builder(GAJenetics::eval, gtf)
                .optimize(Optimize.MINIMUM)
                .executor(Runnable::run) // ONE THREAD FOR DEBUG AND Reproducibility
                .populationSize(60)
                .offspringFraction(0.7)
                .offspringSelector(new TournamentSelector<>()) // why this combination?
                .survivorsSelector(new RouletteWheelSelector<>())
                // .alterers(new Mutator<>(), new Crossover<>()) // use default for now
                .build();
        Genotype<IntegerGene> result = RandomRegistry.with(new Random(0), r -> engine.stream()
                .limit(10000)
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype()));
        System.out.println(statistics);
        System.out.println(result.toString());
        Timetable best = decode(result);
        best.setFitnessScore(eval(result));
        System.out.println("NOW FOR WINDOW COUNTER FOR BEST");
        System.out.println(WindowCounter.count(result, TEACHERS, ACTIVITIES, a -> a.getTeacher(), TIMESLOTS.size(), 5));
        return best;
    }

    private static double eval(Genotype<IntegerGene> gt) {
        double groupsConflicts = IntersectionsCounter.count(gt, GROUPS, ACTIVITIES, actitvity -> actitvity.getGroup());
        double teachersConflicts = IntersectionsCounter.count(gt, TEACHERS, ACTIVITIES, actitvity -> actitvity.getTeacher());
        double groupWindows = WindowCounter.count(gt, GROUPS, ACTIVITIES, actitvity -> actitvity.getGroup(), TIMESLOTS.size(), 5); // for now fix 5
        double teachersWindows = WindowCounter.count(gt, TEACHERS, ACTIVITIES, actitvity -> actitvity.getTeacher(), TIMESLOTS.size(), 5);
        double score = teachersConflicts + groupsConflicts;
        // score = score * 100; // higher weight for hard constraints
        score += (groupWindows + 2 * teachersWindows) / (TIMESLOTS.size() * 3); // in (0, 1), guaranteed
        return score;
    }
}
