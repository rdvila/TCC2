package br.furb.packing.genetic;

import java.security.InvalidParameterException;
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
		return null;
	}

	public PackingResult best(List<Chromosome> chromosomes) {
		return null;
	}
	
}
