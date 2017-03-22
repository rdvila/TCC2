package br.furb.packing;

import java.util.Arrays;

import br.furb.common.Polygon;
import br.furb.dataset.SVGReader;
import br.furb.dataset.SVGWriter;
import br.furb.packing.genetic.GeneticAlgorithm;
import br.furb.packing.jenetic.JeneticAlgorithm;
import br.furb.view.ui.IDataChangeListener;

public class PackingExecutor {

	public PackingResult executePacking(Polygon[] polygons, double height, //
			int rotations, StopCriteria stopCriteria, int stopValue, LocalSearch localSearch,//
			IDataChangeListener... listeners) {

		PackingAlgorithm algorithm;
		if (localSearch == LocalSearch.HILL_CLIMBING) {
			algorithm = new HillClimbingAlgorithm();
		} else if (localSearch == LocalSearch.TABU_SEARCH){
			algorithm = new TabuSearch();
		} else if (localSearch == LocalSearch.GENETIC){
			algorithm = new GeneticAlgorithm();
		} else {
			algorithm = new JeneticAlgorithm();
		}

		algorithm.addLisneter(listeners);

		Arrays.sort(polygons, new Polygon.HeightComparator());
		return algorithm.doPacking(polygons, rotations, height, stopCriteria, stopValue);
	}
	
	public static void main(String[] args) {
		PackingExecutor executor = new PackingExecutor();
		SVGReader reader = new SVGReader();
		SVGWriter writer = new SVGWriter();
		
		if (args.length < 2) {
			System.out.println("USAGE: packing <input> <output>");
			System.exit(-1);
		}
		
		Polygon[] polygons = reader.readXML(args[0]);
		System.out.println("Reading..."+args[0]);
		PackingResult result = executor.executePacking(polygons, 100, 1, StopCriteria.getValue("Loop"), 5, LocalSearch.JENETIC);
		writer.writeXML(args[1], result.getPacking());
		System.out.println("Saving..."+args[1]);
	}

}
