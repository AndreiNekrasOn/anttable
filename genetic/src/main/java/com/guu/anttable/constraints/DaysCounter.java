package com.guu.anttable.constraints;

import io.jenetics.*;

public class DaysCounter {

    /**
     * Count number of days in timetable for NamedEntity
     * @param isGroup 0 if group else 1
     */
    public static double count(Genotype<IntegerGene> gt, final Integer[][] activities, int isGroup,
            int timeslotsSize, int dayDuration, int size) {
        Chromosome<IntegerGene> c = gt.chromosome();
        // int[][] entityDaysCount = new int[size][timeslotsSize / dayDuration];
        int[] entityDaysCount = new int[size];
        int totalDaysCount = 0;
        for (int i = 0; i < c.length(); i++) {
            int currentIdx = activities[i][isGroup];
            int geneIdx = c.get(i).intValue() / dayDuration;
            if ((entityDaysCount[currentIdx] & (1<<geneIdx)) == 0) {
                totalDaysCount++;
                entityDaysCount[currentIdx] |= 1<<geneIdx;
            }
        }
        return totalDaysCount;
    }
}
