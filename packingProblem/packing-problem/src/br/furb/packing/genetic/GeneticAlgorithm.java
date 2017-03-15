package br.furb.packing.genetic;

import java.util.ArrayList;
import java.util.Random;

import br.furb.common.Polygon;
import br.furb.packing.IStopCriteria;
import br.furb.packing.NFPImplementation;
import br.furb.packing.PackingAlgorithm;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.packing.StopCriteriaControl;
import br.furb.view.ui.IDataChangeListener;

public class GeneticAlgorithm implements PackingAlgorithm {
	
	private static final int MAX_POPULATION = 1000;
	Fitness fitness = new Fitness();
	Random random = new Random(343);
	private IDataChangeListener[] listeners;

	@Override
	public PackingResult doPacking(NFPImplementation nfp, Polygon[] polygonsList, int rotationsNumber, double sheetHeight,
			StopCriteria stopCriteria, int stopValue) {

		IStopCriteria stopControl = StopCriteriaControl.getStopCriteria(stopCriteria, stopValue);

		Population population = createFirstPopulation(polygonsList, rotationsNumber, sheetHeight, random);
		PackingResult bestResult = population.evolve(nfp, fitness).result;
		notifyListeners(bestResult);
				
		Selection selection = new Selection(random, 0.5, fitness);
		Crossover crossover = new Crossover(random, MAX_POPULATION);
		Mutation  mutation  = new Mutation(random, 0.3f);

		
		for (int i=0; stopControl.continueRun(); i+=1) {
			population = population.newPolulation(selection, crossover, mutation, population, random);
			PackingResult packingResult = population.evolve(nfp, fitness).result;
			 if (packingResult.getHeight() < bestResult.getHeight()) {
					bestResult = packingResult;
					notifyListeners(bestResult);
				}
			notifyListeners(bestResult);
			System.out.println(String.format("Population  %d: %d (%f)", i, population.size(), packingResult.getHeight()));
			System.out.println(String.format("Best Result %f", bestResult.getHeight()));
		}
		
		return bestResult;
	}
		
	private Population createFirstPopulation(Polygon[] polygonsList, int rotationsNumber, double sheetHeight, Random random) {
		ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
		Chromosome root = new Chromosome(polygonsList, rotationsNumber, sheetHeight, random);
		chromosomes.add(root);
		
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		chromosomes.add(root.shuffle());
		
		Population population = new Population(chromosomes);
		return population;
	}

	public void addLisneter(IDataChangeListener[] listeners) {
		this.listeners = listeners;
	}

	public void notifyListeners(PackingResult result) {
		for (IDataChangeListener listener : listeners) {
			listener.notifyChanged(result);
		}
	}

}
