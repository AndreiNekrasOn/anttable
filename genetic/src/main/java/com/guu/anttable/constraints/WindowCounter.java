package com.guu.anttable.constraints;

import java.util.*;
import java.util.function.Function;

import org.jenetics.*;

import com.guu.anttable.utils.Activity;
import com.guu.anttable.utils.NamedEntity;

public class WindowCounter {

    /**
     * Counts the length of all windows for NamedEntity
     */
    public static double count(Genotype<IntegerGene> gt, final List<Activity> activities, Function<Activity, NamedEntity> getEntity,
            int timeslotsSize, int dayDuration) {
        Chromosome<IntegerGene> c = gt.getChromosome();
        Map<String, int[]> entityTimetable = new HashMap<>(); // можно улучшить через битовые операции

        for (int i = 0; i < c.length(); i++) {
            String currentName = getEntity.apply(activities.get(i)).getName();
            if (!entityTimetable.containsKey(currentName)) {
                entityTimetable.put(currentName, new int[timeslotsSize]);
            }
            entityTimetable.get(currentName)[c.getGene(i).intValue()] = 1; 
        }

        int totalWindowCount = 0;
        for (int[] timetable : entityTimetable.values()) {
            int currentWindowLength = 0;
            boolean firstActivity = true;
            for (int i = 0; i < timetable.length; i++) {
                if (i % dayDuration == 0) { // начало нового дня
                    firstActivity = true;
                    currentWindowLength = 0; // сбрасываем счетчик, так как последние пары пустые
                }
                if (firstActivity) {
                    if (timetable[i] == 0) {
                        continue;
                    }
                    firstActivity = false;
                }
                if (timetable[i] == 0) { 
                    currentWindowLength++;
                } else { // если пары нет, увеличиваем счетчик окна
                    totalWindowCount += currentWindowLength;
                }
            }
        }
        return totalWindowCount;
    }
    
}
