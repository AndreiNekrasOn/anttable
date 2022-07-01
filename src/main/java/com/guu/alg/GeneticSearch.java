package com.guu.alg;


/*
* Initialization: algorithm generates a population from set of items
* 
* Selection:
* 1. The fitness function is evaluated for each individual, providing fitness
* values, which are then normalized. Normalization means dividing the fitness
* value of each individual by the sum of all fitness values, so that the sum of
* all resulting fitness values equals 1.
* 2. Accumulated normalized fitness values are computed: the accumulated
* fitness
* value of an individual is the sum of its own fitness value plus the fitness
* values of all the previous individuals; the accumulated fitness of the last
* individual should be 1, otherwise something went wrong in the normalization
* step.
* 3. A random number R between 0 and 1 is chosen.
* 4. The selected individual is the first one whose accumulated normalized
* value
* is greater than or equal to R.
* (In the roulette wheel selection, the probability of choosing an individual
* for breeding of the next generation is proportional to its fitness, the
* better the fitness is, the higher chance for that individual to be chosen)
* 
* Genetic operation:
* Mutation is simply a permutation (the number of elements depends on
* mutation rate)
* Crossover or recombination - for ex. select few with good
* need to define a chromosome
* select a point(s) in both parents chromosomes, swap for two children
* 
* Termination:
*  - Good enough
*  - Max generations reached
*  - Alloactaed time reached
*  - Plato ("plateau")
*  - 
* 
*  
*/


public abstract class GeneticSearch<T, R> {
    /*
     * T - type of a value of a chromosome
     */
    protected class TRPair {
        T t;
        R r;
    }
    T optimalValue;
    


    GeneticSearch() {
        return;
    }
}
