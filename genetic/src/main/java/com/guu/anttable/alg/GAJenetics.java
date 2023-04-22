package com.guu.anttable.alg;

import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

import com.guu.anttable.constraints.*;
import com.guu.anttable.utils.*;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;

import static io.jenetics.engine.Limits.byExecutionTime;
import static io.jenetics.engine.Limits.byFitnessThreshold;;

// TODO: major refactoring needed
public class GAJenetics {

    static Timeslot[] TIMESLOTS;
    static Activity[] ACTIVITIES;
    final static Set<NamedIdEntity> GROUPS = new HashSet<>();
    final static Set<NamedIdEntity> TEACHERS = new HashSet<>();
    static int TEACHER_IDX = 0;
    static int GROUP_IDX = 0;

    static Integer[][] ACTIVITIES_PRIMITIVE;


    public GAJenetics(List<Timeslot> timeslots, List<Activity> activities) {
        TIMESLOTS = timeslots.toArray(new Timeslot[0]);
        ACTIVITIES = activities.toArray(new Activity[0]);
        // для оптимизации хотим держать в активностях три индекса вместо трёх строк
        decoupleActivities();
    }

    private void decoupleActivities() {
        ACTIVITIES_PRIMITIVE = new Integer[ACTIVITIES.length][2];
        TEACHER_IDX = 0;
        GROUP_IDX = 0;
        for (int i = 0; i < ACTIVITIES.length; i++) {
            Activity a = ACTIVITIES[i];
            if (!GROUPS.contains(a.getGroup())) {
                a.getGroup().setId(GROUP_IDX++);
                GROUPS.add(a.getGroup());
            }
            if (!TEACHERS.contains(a.getTeacher())) {
                a.getTeacher().setId(TEACHER_IDX++);
                TEACHERS.add(a.getTeacher());
            }
            ACTIVITIES_PRIMITIVE[i][0] = a.getGroup().getId();
            ACTIVITIES_PRIMITIVE[i][1] = a.getTeacher().getId();
        }

    }

    public Timetable decode(Genotype<IntegerGene> bestGenotype) {
        final List<ActivityTimeslot> classes = new ArrayList<>();
        Chromosome<IntegerGene> c = bestGenotype.getChromosome();
        IntStream.range(0, c.length()).forEach(i -> {
            ActivityTimeslot at = new ActivityTimeslot(ACTIVITIES[i], TIMESLOTS[c.getGene(i).intValue()], "");
            classes.add(at);
        });
        return new Timetable(classes);
    }

    public Timetable run() {
        EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(IntegerChromosome.of(
                IntRange.of(0, TIMESLOTS.length - 1), ACTIVITIES.length));
        final Engine<IntegerGene, Double> engine = Engine
                .builder(GAJenetics::eval, gtf)
                .optimize(Optimize.MINIMUM)
                .executor(Runnable::run) // ONE THREAD FOR DEBUG AND Reproducibility
                .populationSize(1000)
                .offspringFraction(0.9)
                .offspringSelector(new RouletteWheelSelector<>()) // why this combination?
                .survivorsSelector(new RouletteWheelSelector<>())
                // .alterers(new Mutator<>(), new Crossover<>()) // use default for now
                .build();
        Genotype<IntegerGene> result = RandomRegistry.with(new Random(0), r -> engine.stream()
                .limit(byFitnessThreshold(1.))
                .limit(byExecutionTime(Duration.ofSeconds(60)))
                .limit(100000)
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
        double score = 0;
        double groupsConflicts = IntersectionsCounter.count(gt, GROUP_IDX, ACTIVITIES_PRIMITIVE, 0);
        double teachersConflicts = IntersectionsCounter.count(gt, TEACHER_IDX, ACTIVITIES_PRIMITIVE, 1);
        score = teachersConflicts + groupsConflicts;
        score *= 5; // number of soft constraints + 1

        score += evalCommon(gt, GROUPS.size(), 0);
        score += evalCommon(gt, TEACHERS.size(), 1);

        return score;
    }

    /**
     * Each use adds 2 soft constraints to score
     */
    private static <T> double evalCommon(Genotype<IntegerGene> gt, int tSize, int isGroup) {
        double score = 0;
        int activitiesPerDay = 5;

        double windows = WindowCounter.count(gt, ACTIVITIES_PRIMITIVE, isGroup, TIMESLOTS.length, activitiesPerDay, tSize); 
        score += (windows) / (TIMESLOTS.length);

        double days = DaysCounter.count(gt, ACTIVITIES_PRIMITIVE, isGroup, TIMESLOTS.length, activitiesPerDay, tSize);
        score += days / (tSize * activitiesPerDay);

        return score;

    }
}
