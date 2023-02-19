package com.guu.anttable.constraints;

import java.util.*;
import java.util.function.Function;

import org.jenetics.*;

import com.guu.anttable.utils.Activity;
import com.guu.anttable.utils.NamedEntity;

public class IntersectionsCount {

    public static double count(Genotype<IntegerGene> gt, final Set<NamedEntity> lookup, 
            final List<Activity> activities, Function<Activity, NamedEntity> getEntity) {
        Chromosome<IntegerGene> c = gt.getChromosome();
        Map<NamedEntity, Set<Integer>> schedule = new HashMap<>();
		lookup.forEach(t -> schedule.put(t, new HashSet<>()));
        int counter = 0;
        for (int i = 0; i < c.length(); i++) {
            NamedEntity curr = getEntity.apply(activities.get(i));
            if (schedule.get(curr).contains(c.getGene(i).intValue())) {
                counter++;
            } else {
                schedule.get(curr).add(c.getGene(i).intValue());
            }
        }
        return counter;
    }
}
