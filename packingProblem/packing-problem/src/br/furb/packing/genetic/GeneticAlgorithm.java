package br.furb.packing.genetic;

import java.util.ArrayList;

import br.furb.common.Polygon;
import br.furb.packing.IStopCriteria;
import br.furb.packing.PackingAlgorithm;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.packing.StopCriteriaControl;
import br.furb.view.ui.IDataChangeListener;

public class GeneticAlgorithm implements PackingAlgorithm {
	
	Fitness fitness = new Fitness();

	@Override
	public PackingResult doPacking(Polygon[] polygonsList, int rotationsNumber, double sheetHeight,
			StopCriteria stopCriteria, int stopValue) {

		IStopCriteria stopControl = StopCriteriaControl.getStopCriteria(stopCriteria, stopValue);

		Population population = createFirstPopulation(polygonsList, rotationsNumber, sheetHeight);
		PackingResult bestResult = population.evolve(fitness);
		notifyListeners(bestResult);
				
		Selection selection = new Selection();
		Crossover crossover = new Crossover();
		Mutation mutation   = new Mutation(0.0f);

		for (; stopControl.continueRun();) {
			population = population.newPolulation(selection, crossover, mutation, population);
			bestResult = population.evolve(fitness);
			notifyListeners(bestResult);
		}
		
		return bestResult;
	}
		
	private Population createFirstPopulation(Polygon[] polygonsList, int rotationsNumber, double sheetHeight) {
		ArrayList<Chromosome> chromosomes = new ArrayList<Chromosome>();
		chromosomes.add(new Chromosome(polygonsList, rotationsNumber, sheetHeight));
		Population population = new Population(chromosomes);
		return population;
	}

	@Override
	public void addLisneter(IDataChangeListener[] listeners) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyListeners(PackingResult result) {
		// TODO Auto-generated method stub
		
	}

}
