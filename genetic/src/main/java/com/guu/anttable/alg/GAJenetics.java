package com.guu.anttable.alg;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.jenetics.*;
import org.jenetics.engine.*;
import org.jenetics.util.*;

import com.guu.anttable.constraints.DaysCounter;
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
        return best;
    }

    private static double eval(Genotype<IntegerGene> gt) {
        // keep soft constraints < 1
        // and hard constraints 0 or >= #soft constraints
        Function<Activity, NamedEntity> groupGetter = actitvity -> actitvity.getGroup();
        Function<Activity, NamedEntity> teacherGetter = actitvity -> actitvity.getTeacher();

        double score = 0;
        double groupsConflicts = IntersectionsCounter.count(gt, GROUPS, ACTIVITIES, groupGetter);
        double teachersConflicts = IntersectionsCounter.count(gt, TEACHERS, ACTIVITIES, teacherGetter);
        score = teachersConflicts + groupsConflicts;
        score *= 5; // number of soft constraints + 1

        score += evalCommon(gt, GROUPS, groupGetter);
        score += evalCommon(gt, TEACHERS, teacherGetter);

        return score;
    }

    /**
     * Each use adds 2 soft constraints to score
     */
    private static <T> double evalCommon(Genotype<IntegerGene> gt, Set<NamedEntity> allT, Function<Activity, NamedEntity> getT) {
        double score = 0;
        double windows = WindowCounter.count(gt, ACTIVITIES, getT, TIMESLOTS.size(), fmt.getTimes().size()); // for now fix 5
        score += (windows) / (TIMESLOTS.size());

        double days = DaysCounter.count(gt, ACTIVITIES, getT, TIMESLOTS.size(), fmt.getTimes().size());
        score += days / (allT.size() * fmt.getTimes().size());

        return score;

    }
}
