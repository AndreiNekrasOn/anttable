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
    final static Map<String, Integer> GROUPS = new HashMap<>();
    final static Map<String, Integer> TEACHERS = new HashMap<>();

    static Integer[][] ACTIVITIES_PRIMITIVE;


    public GAJenetics(List<Timeslot> timeslots, List<Activity> activities) {
        TIMESLOTS = timeslots.toArray(new Timeslot[0]);
        ACTIVITIES = activities.toArray(new Activity[0]);
        // для оптимизации хотим держать в активностях три индекса вместо трёх строк
        decoupleActivities();
    }

    private void decoupleActivities() {
        ACTIVITIES_PRIMITIVE = new Integer[ACTIVITIES.length][2];
        int teacherIdx = 0;
        int groupIdx = 0;

        for (int i = 0; i < ACTIVITIES.length; i++) {
            Activity a = ACTIVITIES[i];
            if (!GROUPS.containsKey(a.group().name())) {
                GROUPS.put(a.group().name(), groupIdx++);
            }
            if (!TEACHERS.containsKey(a.teacher().name())) {
                TEACHERS.put(a.teacher().name(), teacherIdx++);
            }
            ACTIVITIES_PRIMITIVE[i][0] = GROUPS.get(a.group().name());
            ACTIVITIES_PRIMITIVE[i][1] = TEACHERS.get(a.teacher().name());
        }
    }


    public Timetable decode(Genotype<IntegerGene> bestGenotype) {
        final List<ActivityTimeslot> classes = new ArrayList<>();
        Chromosome<IntegerGene> c = bestGenotype.chromosome();
        IntStream.range(0, c.length()).forEach(i -> {
            ActivityTimeslot at = new ActivityTimeslot(ACTIVITIES[i], TIMESLOTS[c.get(i).intValue()], "");
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
                .offspringFraction(0.6)
                .offspringSelector(new RouletteWheelSelector<>()) // why this combination?
                .survivorsSelector(new RouletteWheelSelector<>())
                // .alterers(new Mutator<>(0.7))
                .interceptor(EvolutionInterceptor.ofAfter(er-> {
                    System.out.format("%s - %s\n",  er.generation(), er.bestFitness());
                    return er;
                }))
                .build();
        Genotype<IntegerGene> result = RandomRegistry.with(new Random(0), r -> engine.stream()
                .limit(byFitnessThreshold(4.))
                .limit(10000)
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype()));
        System.out.println(statistics);
        System.out.println(result.toString());
        System.out.println(eval(result));
        Timetable best = decode(result);
        return best;
    }

    private static double eval(Genotype<IntegerGene> gt) {
        // keep soft constraints < 1
        // and hard constraints 0 or >= #soft constraints
        double score = 0;
        double groupsConflicts = IntersectionsCounter.count(gt, GROUPS.size(), ACTIVITIES_PRIMITIVE, 0);
        double teachersConflicts = IntersectionsCounter.count(gt, TEACHERS.size(), ACTIVITIES_PRIMITIVE, 1);
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
