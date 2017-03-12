package br.furb.packing;

import java.util.Arrays;

import br.furb.common.Polygon;
import br.furb.view.ui.IDataChangeListener;

public class PackingExecutor {

	public PackingResult executePacking(Polygon[] polygons, double height, //
			int rotations, StopCriteria stopCriteria, int stopValue, LocalSearch localSearch,//
			IDataChangeListener... listeners) {

		HillClimbingAlgorithm algorithm;
		if (localSearch == LocalSearch.HILL_CLIMBING) {
			algorithm = new HillClimbingAlgorithm();
		} else {
			algorithm = new TabuSearch();
		}

		algorithm.addLisneter(listeners);

		Arrays.sort(polygons, new Polygon.HeightComparator());
		return algorithm.doPacking(polygons, rotations, height, stopCriteria, stopValue);
	}

}
