package com.guu.anttable.constraints;

import java.util.*;
import java.util.function.Function;

import org.jenetics.*;

import com.guu.anttable.utils.Activity;
import com.guu.anttable.utils.NamedEntity;

public class IntersectionsCounter {

    public static double count(Genotype<IntegerGene> gt, final Set<NamedEntity> lookup, 
            final List<Activity> activities, Function<Activity, NamedEntity> getEntity) {
        Chromosome<IntegerGene> c = gt.getChromosome();
        Map<String, Set<Integer>> schedule = new HashMap<>();
		lookup.forEach(t -> schedule.put(t.getName(), new HashSet<>()));
        int counter = 0;
        for (int i = 0; i < c.length(); i++) {
            String curr = getEntity.apply(activities.get(i)).getName();
            if (schedule.get(curr).contains(c.getGene(i).intValue())) {
                counter++;
            } else {
                schedule.get(curr).add(c.getGene(i).intValue());
            }
        }
        return counter;
    }
}
