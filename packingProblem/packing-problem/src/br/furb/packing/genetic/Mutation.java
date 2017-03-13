package br.furb.packing.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.furb.common.Polygon;

public class Mutation {
	
	private float mutationFactor;
	private Random random;
	
	public Mutation(Random random, float mutationFactor) {
		this.random = random;
		this.mutationFactor = mutationFactor;
	}

	public List<Chromosome> doOperation(List<Chromosome> crossoverList) {
		ArrayList<Chromosome> mutations = new ArrayList<Chromosome>();
		int mutationLength = (int)(mutationFactor * crossoverList.size());
		
		for (int i=0; i<mutationLength; i++) {
			Chromosome mutation = crossoverList.remove((random.nextInt(crossoverList.size()-1)));
			mutate(mutation);
			mutations.add(mutation);
		}
		
		for (Chromosome c : crossoverList) {
			mutations.add(c);
		}
		
		return mutations;
	}

	private void mutate(Chromosome chromosome) {
		
		int first_pos  = 0;
		int last_pos   = chromosome.polygonsList.length-1;
		int center_pos = (int)((chromosome.polygonsList.length-1)/2);
		
		Polygon first  = chromosome.polygonsList[first_pos];
		Polygon last   = chromosome.polygonsList[last_pos];
		Polygon center = chromosome.polygonsList[center_pos];
		
		chromosome.polygonsList[first_pos]   = last;
		chromosome.polygonsList[last_pos]    = center;
		chromosome.polygonsList[center_pos]  = first;
		
	}

}
