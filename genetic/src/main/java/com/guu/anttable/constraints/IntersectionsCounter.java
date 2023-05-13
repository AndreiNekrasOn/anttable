package com.guu.anttable.constraints;

import java.util.*;

import io.jenetics.*;

public class IntersectionsCounter {

    public static double count(Genotype<IntegerGene> gt, final int lookupSize, 
            final Integer[][] activities, int isGroup) {
        Chromosome<IntegerGene> c = gt.chromosome();
        List<Set<Integer>> schedule = new ArrayList<>(lookupSize);

        for (int i = 0; i < lookupSize; i++) {
            schedule.add(new HashSet<>());
        }
        int counter = 0;
        for (int i = 0; i < c.length(); i++) {
            int curr = activities[i][isGroup];
            if (schedule.get(curr).contains(c.get(i).intValue())) {
                counter++;
            } else {
                schedule.get(curr).add(c.get(i).intValue());
            }
        }
        return counter;
    }
}
