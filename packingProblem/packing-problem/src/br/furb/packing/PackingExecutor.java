package br.furb.packing;

import java.util.Arrays;

import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.dataset.SVGReader;
import br.furb.dataset.SVGWriter;
import br.furb.packing.genetic.GeneticAlgorithm;
import br.furb.packing.jenetic.JeneticAlgorithm;
import br.furb.packing.jnfp.JNFP;
import br.furb.packing.jnfp.JNFPWithCache;
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
		} else if (localSearch == LocalSearch.GENETIC) {
			algorithm = new GeneticAlgorithm();
		} else {
			algorithm = new JeneticAlgorithm();
		}

		algorithm.addLisneter(listeners);

		Arrays.sort(polygons, new Polygon.HeightComparator());

		// NFPImplementation nfp = new NoFitPolygon();
		NFPImplementation nfp = new JNFP();

		long start = System.currentTimeMillis();
		
		PackingResult result = algorithm.doPacking(nfp, polygons, rotations, height, stopCriteria, stopValue);
		
		long end = System.currentTimeMillis();

		System.out.println(String.format(">>>> TIME: %dms <<<<", end - start));
		// SVGWriter writer = new SVGWriter();
		// writer.writeXML("C:\\Users\\rodrigo\\Desktop\\result.svg",
		// result.getPacking());
		return result;
	}

	public static void main(String[] args) {

		String[] names = { "nest5-converted" };
		String path = "C:\\Users\\rodrigo\\Desktop\\";

		for (String name : names) {

			PackingExecutor executor = new PackingExecutor();
			SVGReader reader = new SVGReader();
			SVGWriter writer = new SVGWriter();

			Polygon[] polygons = reader.readXML(path + name + ".svg");
			PackingResult result = executor.executePacking(polygons, 1000, 1, StopCriteria.getValue("Loop"), 1,
					LocalSearch.HILL_CLIMBING);
			writer.writeXML(path + name + "-result.svg", result.getPacking(), result.maxX(), result.maxY());
			System.out.println("height: " +result.getHeight());
		}
	}

}
