package com.guu.anttable.constraints;

import io.jenetics.*;

public class WindowCounter {

    /**
     * Counts the length of all windows for NamedEntity
     */
    public static double count(Genotype<IntegerGene> gt, final Integer[][] activities, int isGroup,
            int timeslotsSize, int dayDuration, int size) {
        Chromosome<IntegerGene> c = gt.chromosome();
        int[] entityTimetable = new int[size];
        for (int i = 0; i < c.length(); i++) {
            int currIdx = activities[i][isGroup];
            entityTimetable[currIdx] |= 1 << c.get(i).intValue();
        }

        int totalWindowCount = 0;
        for (int timetable : entityTimetable) {
            int currentWindowLength = 0;
            boolean firstActivity = true;
            // можно распараллелить
            for (int i = 0; i < timeslotsSize; i++) {
                if (i % dayDuration == 0) { // начало нового дня
                    firstActivity = true;
                    currentWindowLength = 0; // сбрасываем счетчик, так как последние пары пустые
                }
                if (firstActivity) {
                    if ((timetable & 1 << i) == 0) {
                        continue;
                    }
                    firstActivity = false;
                }
                if ((timetable & 1<<i) == 0) { 
                    currentWindowLength++; // если пары нет, увеличиваем счетчик окна
                } else { 
                    totalWindowCount += currentWindowLength;
                }
            }
        }
        return totalWindowCount;
    }
    
}
