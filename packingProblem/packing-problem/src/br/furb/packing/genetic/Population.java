package br.furb.packing.genetic;

import java.util.List;
import java.util.Random;

import br.furb.packing.NFPImplementation;
import br.furb.packing.PackingResult;

public class Population {
	
	List<Chromosome> chromosomes;
	
	public Population(List<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	Chromosome evolve(NFPImplementation nfp, Fitness fitness) {
		for (Chromosome c : chromosomes) {
			c.evolve(nfp);
		}	
		return fitness.best(chromosomes);
	}
	
	Population newPolulation(Selection selection, Crossover crossover, Mutation mutation, Population oldPopulation, Random random) {
		List<Chromosome> selectionList = selection.doOperation(chromosomes);
		List<Chromosome> crossoverList = crossover.doOperation(selectionList);
		List<Chromosome> mutationList  = mutation.doOperation(crossoverList);
		return new Population(mutationList);
		
	}

	public int size() {
		if (chromosomes != null)
			return chromosomes.size();
		
		return 0;
	}

}
