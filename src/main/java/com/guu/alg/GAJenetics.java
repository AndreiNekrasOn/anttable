package com.guu.alg;

import java.util.*;
import java.util.stream.IntStream;

import org.jenetics.*;
import org.jenetics.engine.*;
import org.jenetics.util.*;

import com.guu.utils.*;

public class GAJenetics {
    final static List<Timeslot> TIMESLOTS = new ArrayList<>();
    final static List<Activity> ACTIVITIES = new ArrayList<>();
    final static Set<Group> GROUPS = new HashSet<>();
    final static Set<Teacher> TEACHERS = new HashSet<>();
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

    public static Timetable decode(Genotype<IntegerGene> bestGenotype, DayFormat fmt) {
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
                .offspringSelector(new TournamentSelector<>())
                .survivorsSelector(new RouletteWheelSelector<>())
                // .alterers(new Mutator<>(), new Crossover<>()) // use default for now
                .build();
        Genotype<IntegerGene> result = RandomRegistry.with(new Random(0), r -> engine.stream()
                .limit(10000)
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype()));
        System.out.println(statistics);
        System.out.println(result.toString());
        return decode(result, fmt);
    }

    static class Counter {
        private int i;

        public Counter(int i) {
            this.i = i;
        }

        public void inc() {
            i++;
        }

        public int get() {
            return i;
        }
    }

    interface Callable<I, O> {

        public O call(I input);
    }

    private static <T> int countIntersections(Chromosome<IntegerGene> c,
            Set<T> tSet, Callable<Activity, T> getT) {
        Map<T, Set<Integer>> schedule = new HashMap<>();
        tSet.forEach(t -> schedule.put(t, new HashSet<>()));
        final Counter cnt = new Counter(0);
        IntStream.range(0, c.length()).forEach(i -> {
            final T t = getT.call(ACTIVITIES.get(i));
            if (schedule.get(t).contains(c.getGene(i).intValue())) {
                cnt.inc();
            } else {
                schedule.get(t).add(c.getGene(i).intValue());
            }

        });
        return cnt.get();
    }

    private static double eval(Genotype<IntegerGene> gt) {
        Chromosome<IntegerGene> c = gt.getChromosome();
        double groupsConflicts = countIntersections(c, GROUPS, new Callable<Activity, Group>() {
            public Group call(Activity a) {
                return a.getGroup();
            }
        });
        double teachersConflicts = countIntersections(c, TEACHERS, new Callable<Activity, Teacher>() {
            public Teacher call(Activity a) {
                return a.getTeacher();
            }
        });
        double score = (teachersConflicts / (TEACHERS.size() * TIMESLOTS.size()) +
                groupsConflicts / (GROUPS.size() * TIMESLOTS.size()));
        return score;
    }
}
