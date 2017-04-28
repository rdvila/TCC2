package br.furb.packing;

import java.util.Arrays;

import br.furb.common.Polygon;
import br.furb.dataset.SVGReader;
import br.furb.dataset.SVGWriter;
import br.furb.packing.jenetic.JeneticAlgorithm;
import br.furb.view.ui.IDataChangeListener;

public class PackingExecutor {

	public PackingResult executePacking(Polygon[] polygons, double height, //
			int rotations, StopCriteria stopCriteria, int stopValue, LocalSearch localSearch, //
			IDataChangeListener... listeners) {

		PackingAlgorithm algorithm;
		if (localSearch == LocalSearch.HILL_CLIMBING) {
			algorithm = new HillClimbingAlgorithm();
		} else if (localSearch == LocalSearch.TABU_SEARCH) {
			algorithm = new TabuSearch();
		} else {
			algorithm = new JeneticAlgorithm();
		}

		algorithm.addLisneter(listeners);

		Arrays.sort(polygons, new Polygon.HeightComparator());

		NFPImplementation nfp = new NoFitPolygon();
		
		long start = System.currentTimeMillis();
		PackingResult result = algorithm.doPacking(nfp, polygons, rotations, height, stopCriteria, stopValue);
		long end = System.currentTimeMillis();
		System.out.println(String.format(">>>> TIME: %dms <<<<", end - start));
		
		return result;
	}

	public static void main(String[] args) {

		String[] names = { "nest5-converted" };
		String path = "C:\\Users\\rodrigo\\Desktop\\TCC2\\tests\\";

		for (String name : names) {

			PackingExecutor executor = new PackingExecutor();
			SVGReader reader = new SVGReader();
			SVGWriter writer = new SVGWriter();

			Polygon[] polygons = reader.readXML(path + name + ".svg");
			PackingResult result = executor.executePacking(polygons, 1000, 1, StopCriteria.getValue("Loop"), 2,
					LocalSearch.JENETIC);
			writer.writeXML(path + name + "-result.svg", result.getPacking(), result.maxX(), result.maxY());
			System.out.println("height: " +result.getHeight());
		}
	}

}
