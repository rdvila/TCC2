package br.furb.packing.genetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import br.furb.common.Polygon;

public class Crossover {
	
	private Random random;
	private int maxPopulation;

	public Crossover(Random random, int maxPopulation) {
		this.random = random;
		this.maxPopulation = maxPopulation;
	}

	public List<Chromosome> doOperation(List<Chromosome> selectionList) {
		ArrayList<Chromosome> crossoverList = new ArrayList<Chromosome>();
		Chromosome parent1 = null;
		Chromosome parent2 = null;
		
		
		for (int i=1 ;i<selectionList.size(); i++) {
			if ((crossoverList.size()) >= maxPopulation-1) {
				break;
			}
			
			parent1 = selectionList.get(i-1);
			parent2 = selectionList.get(i);
			crossoverList.add(parent1);
				
			for (Chromosome c : crossover(parent1, parent2)) {
				crossoverList.add(c);
			}
		}
		
		if (parent2 != null) {
			crossoverList.add(parent2);
		}
		
		return crossoverList;
	}

	private List<Chromosome> crossover(Chromosome parent1, Chromosome parent2) {
		ArrayList<Chromosome> children = new ArrayList<Chromosome>();
		
		Chromosome child1 = parent1.copy();
		Chromosome child2 = parent2.copy();
		
		copyOver(child1, parent2);
		copyOver(child2, parent1);
		
		children.add(child1);
		children.add(child2);
		
		return children;
	}

	private void copyOver(Chromosome child, Chromosome parent) {
		HashMap<Polygon, Integer> childOut = new HashMap<Polygon, Integer>();		
		for (int i=0; i<child.polygonsList.length; i++) {
			if (i %2 != 0) {
				childOut.put(child.polygonsList[i], 1);
				child.polygonsList[i] = null;
			}
		}
		

		for (int i=1; i<child.polygonsList.length; i+=2) {
			Polygon p = parent.polygonsList[i];
			if (childOut.containsKey(p)) {
				child.polygonsList[i] = p;
				childOut.remove(p);
			}
		}
		
		LinkedList<Polygon> out = new LinkedList<Polygon>(childOut.keySet());
		for (int i=1; i<child.polygonsList.length; i+=2) {
			Polygon p = child.polygonsList[i];
			if (p == null) {
				p = out.poll();
				child.polygonsList[i] = p;
			}
		}
	}
	
}