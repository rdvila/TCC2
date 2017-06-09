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

public class MultipleJeneticExecutor {

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
		
		String input = pinput;
		int[] Lheight = {25, 50, 75, 100};
		int[] Lrotations = {1, 2, 3, 4};
		int[] Lgenerations = {5, 10, 20, 50, 100, 200};
		int[] LpopSize = {10, 20, 50, 100, 200};
		int time = 60000*20;
		int executions = 5;
		
		MultipleJeneticExecutor executor = new MultipleJeneticExecutor();
		SVGReader reader = new SVGReader();
		SVGWriter writer = new SVGWriter();
		Polygon[] polygons = reader.readXML(input + ".svg");
		
		for (int execution=0; execution<executions; execution++) {
			for (int height: Lheight) {
				for (int rotations: Lrotations) {
					for (int generations : Lgenerations) {
						for (int popSize : LpopSize) {							
							
							long start = System.currentTimeMillis();
							PackingResult result = executor.executePacking(polygons, height, rotations, generations, popSize, time);
							long end = System.currentTimeMillis();
							
							long total = end - start;
							
							PackingResult.dataset = input;
							String outname = input + "-" + height + "-" + rotations + "-" + generations + "-" + popSize + "-" + time + '-' + execution;
							writer.writeXML(outname + "-result.svg", result.getPacking(), result.maxX(), result.maxY());
							
							String resultTable = input + "," +  height + "," + rotations + "," + generations + "," + result.maxX() + "," + result.maxY() + ","  + popSize + "," + result.getHeight() + "," + total + "," + execution;
							
							try {
								Files.write(Paths.get(outname + "-info.txt"), resultTable.getBytes(), StandardOpenOption.CREATE);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							System.out.println(resultTable);
							
						}
					}
				}
			}
			
		}
		
		
		System.exit(0);
	}

}
