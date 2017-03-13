package br.furb.packing.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Selection {

	private double survivelRate;
	private Fitness fitness;
	private Random random;

	public Selection(Random random, double survivelRate, Fitness fitness) {
		this.random = random;
		this.survivelRate = survivelRate;
		this.fitness = fitness;
	}

	public List<Chromosome> doOperation(List<Chromosome> chromosomes) {
		ArrayList<Chromosome> survivel = new ArrayList<Chromosome>();
		List<Chromosome> sorted = fitness.sort(chromosomes);
		int survivelLength = (int)(sorted.size() * survivelRate);
		for (int i=0; i<survivelLength; i++) {
			survivel.add(sorted.get(i));
		}
		return survivel;
	}

}
