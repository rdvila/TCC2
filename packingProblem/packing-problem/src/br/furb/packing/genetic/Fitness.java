package br.furb.packing.genetic;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.furb.packing.PackingResult;

public class Fitness {
	
	public double calculate(Chromosome chromosome) {
		PackingResult result = chromosome.getResult();
		
		if (result == null) {
			throw new InvalidParameterException("Chromosome not evolved!");
		}
		return result.getHeight();
	}

	public List<Chromosome> sort(List<Chromosome> chromosomes) {
		Collections.sort(chromosomes, new Comparator<Chromosome>() {

			@Override
			public int compare(Chromosome o1, Chromosome o2) {
				if (o1.result.getHeight() < o2.getResult().getHeight()) {
					return 1;
				
				} else if (o1.result.getHeight() > o2.getResult().getHeight()){
					return -1;
				}
				
				return 0;
			}
		});
		
		return chromosomes;
	}

	public Chromosome best(List<Chromosome> chromosomes) {
		Chromosome best = null;
		for (Chromosome c: chromosomes) {
			if (best == null || c.result.getHeight() < best.result.getHeight()) {
				best = c;
			}
		}
		return best;
	}
	
}
