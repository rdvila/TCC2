package br.furb.packing.genetic;

import java.util.List;

import br.furb.packing.PackingResult;

public class Population {
	
	List<Chromosome> chromosomes;
	
	public Population(List<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	PackingResult evolve(Fitness fitness) {
		for (Chromosome c : chromosomes) {
			c.evolve();
		}
		
		return fitness.best(chromosomes);
	}
	
	Population newPolulation(Selection selection, Crossover crossover, Mutation mutation, Population oldPopulation) {
		List<Chromosome> selectionList = selection.doOperation(chromosomes, oldPopulation);
		List<Chromosome> crossoverList = crossover.doOperation(selectionList);
		List<Chromosome> mutationList  = mutation.doOperation(crossoverList);
		return new Population(mutationList);
		
	}

}
