import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import br.furb.common.Polygon;
import br.furb.dataset.SVGReader;
import br.furb.dataset.SVGWriter;
import br.furb.packing.HillClimbingAlgorithm;
import br.furb.packing.LocalSearch;
import br.furb.packing.NFPImplementation;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.packing.jnfp.JNFP;

public class HillExecutor {

	public PackingResult executePacking(Polygon[] polygons, double height, //
			int rotations, StopCriteria stopCriteria, int stopValue, LocalSearch localSearch) {

		HillClimbingAlgorithm algorithm = new HillClimbingAlgorithm();
		Arrays.sort(polygons, new Polygon.HeightComparator());
		
		NFPImplementation nfp = new JNFP();
			
		PackingResult result = algorithm.doPacking(nfp, polygons, rotations, height, stopCriteria, stopValue);

		return result;
	}

	public static void main(String[] args) {

		String pinput = args[0];
		String pheight = args[1];
		String protations = args[2];
		String ptime = args[3];
		
		String input = pinput;
		int height = Integer.valueOf(pheight).intValue();
		int rotations = Integer.valueOf(protations).intValue();
		int time = Integer.valueOf(ptime).intValue(); // generations
		LocalSearch algorithm = LocalSearch.HILL_CLIMBING;
		
		HillExecutor executor = new HillExecutor();
		SVGReader reader = new SVGReader();
		SVGWriter writer = new SVGWriter();

		Polygon[] polygons = reader.readXML(input + ".svg");
		
		long start = System.currentTimeMillis();
		PackingResult result = executor.executePacking(polygons, height, rotations, StopCriteria.getValue("Tempo"), time,
				algorithm);
		long end = System.currentTimeMillis();
		
		long total = end - start;
		
		String outname = input + "-hill-" + pheight + "-" + protations + "-" + time;
		writer.writeXML(outname + "-result.svg", result.getPacking(), result.maxX(), result.maxY());
		
		String resultTable = input + "," +  pheight + "," + protations + "," + time + "," + result.maxX() + "," + result.maxY() + "," + result.getHeight() + "," + total;
		
		try {
			Files.write(Paths.get(outname + "-info.txt"), resultTable.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultTable);
	}

}
