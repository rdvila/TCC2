import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import br.furb.common.Polygon;
import br.furb.dataset.SVGReader;
import br.furb.dataset.SVGWriter;
import br.furb.packing.LocalSearch;
import br.furb.packing.NFPImplementation;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.packing.jenetic.JeneticAlgorithm;
import br.furb.packing.jnfp.JNFP;

public class JeneticExecutor {

	public PackingResult executePacking(Polygon[] polygons, double height, //
			int rotations, int generations, int populationSize, int time) {

		JeneticAlgorithm algorithm = new JeneticAlgorithm();
		Arrays.sort(polygons, new Polygon.HeightComparator());
		
		NFPImplementation nfp = new JNFP();			
		PackingResult result = algorithm.doPackingCustom(nfp, polygons, rotations, height, generations, populationSize, time);

		return result;
	}

	public static void main(String[] args) {

		String pinput = args[0];
		String pheight = args[1];
		String protations = args[2];
		String ploops = args[3];
		String pPopsize = args[4];
		String pTime = args[5];
		
		String input = pinput;
		int height = Integer.valueOf(pheight).intValue();
		int rotations = Integer.valueOf(protations).intValue();
		int generations = Integer.valueOf(ploops).intValue();
		int popSize = Integer.valueOf(pPopsize).intValue();
		int time = Integer.valueOf(pTime).intValue();
		
		JeneticExecutor executor = new JeneticExecutor();
		SVGReader reader = new SVGReader();
		SVGWriter writer = new SVGWriter();

		Polygon[] polygons = reader.readXML(input + ".svg");
		
		long start = System.currentTimeMillis();
		PackingResult result = executor.executePacking(polygons, height, rotations, generations, popSize, time);
		long end = System.currentTimeMillis();
		
		long total = end - start;
		
		String outname = input + "-" + pheight + "-" + protations + "-" + ploops + "-" + popSize + "-" + pTime;
		writer.writeXML(outname + "-result.svg", result.getPacking(), result.maxX(), result.maxY());
		
		String resultTable = input + "," +  pheight + "," + protations + "," + ploops + "," + result.maxX() + "," + result.maxY() + ","  + popSize + "," + result.getHeight() + "," + total;
		
		try {
			Files.write(Paths.get(outname + "-info.txt"), resultTable.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(resultTable);
	}

}
