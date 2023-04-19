package com.guu.anttable.constraints;

import java.util.*;
import java.util.function.Function;

import org.jenetics.*;

import com.guu.anttable.utils.Activity;
import com.guu.anttable.utils.NamedEntity;

public class DaysCounter {

    /**
     * Count number of days in timetable for NamedEntity
     */
    public static double count(Genotype<IntegerGene> gt, final List<Activity> activities, Function<Activity, NamedEntity> getEntity,
            int timeslotsSize, int dayDuration) {
        Chromosome<IntegerGene> c = gt.getChromosome();
        Map<String, int[]> entityDaysCount = new HashMap<>(); // можно улучшить через битовые операции

        for (int i = 0; i < c.length(); i++) {
            String currentName = getEntity.apply(activities.get(i)).getName();
            if (!entityDaysCount.containsKey(currentName)) {
                entityDaysCount.put(currentName, new int[timeslotsSize / dayDuration]);
            }
            entityDaysCount.get(currentName)[c.getGene(i).intValue() / dayDuration] = 1;
        }

        int totalDaysCount = 0;
        for (int[] daysCount : entityDaysCount.values()) {
            totalDaysCount += Arrays.stream(daysCount).sum();
        }
        return totalDaysCount;
    }
}
