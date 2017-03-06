
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import javax.swing.JFrame;
import javax.swing.JPanel;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stiaan Uyttersprot
 */
public class OrbitMain {

	private static final String rodrigo = "C:\\Users\\rodrigo\\Desktop\\NFP-TEST\\";

	public static void main(String[] args) throws FileNotFoundException {

		DrawJavaFX drawTool = new DrawJavaFX();

		File folder;
		System.out.println("Orbiting tests");

		folder = new File(rodrigo);
		File[] listOfFilesRodrigo = folder.listFiles();

		NoFitPolygon nfp = null;

		boolean testMass = true;
		if(testMass){

			System.out.println("Rodrigo");
			System.out.println("---------------");
			nfp = generateNFPsForList(listOfFilesRodrigo, 0);
		}


		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				MainWindow ex = new MainWindow();
				ex.drawNFP(nfp);
				ex.setVisible(true);
			}
		});
	}

	public static NoFitPolygon generateNFPsForList(File[] listOfFiles, int rotations) throws FileNotFoundException {
		int n = 0;


		List<MultiPolygon> polygonsList = new ArrayList<>();

		int numberOfPolys = 100;
		MultiPolygon original;
		MultiPolygon inverse;
		MultiPolygon ninety;
		MultiPolygon twoseventy;
		for (File polygon : listOfFiles) {
			if (n == numberOfPolys) break;
			switch (rotations) {
				case 1:
					inverse = new MultiPolygon(polygon);
					inverse.replaceByNegative();

					polygonsList.add(inverse);
					break;
				case 2:
					original = new MultiPolygon(polygon);
					inverse = new MultiPolygon(polygon);
					inverse.replaceByNegative();
					polygonsList.add(original);
					polygonsList.add(inverse);
					break;
				case 4:
					original = new MultiPolygon(polygon);
					inverse = new MultiPolygon(polygon);
					inverse.replaceByNegative();
					ninety = new MultiPolygon(polygon);
					ninety.shiftNinety();
					twoseventy = new MultiPolygon(polygon);
					twoseventy.shiftNinety();
					polygonsList.add(original);
					polygonsList.add(inverse);
					polygonsList.add(ninety);
					polygonsList.add(twoseventy);
					break;
				default:
					polygonsList.add(new MultiPolygon(polygon));
					break;
			}

			n++;
		}
		System.out.println(n);

		long startTime;
		long endTime;
		long duration;

		int totalIts = 0;

		NoFitPolygon nfp = null;

		startTime = System.currentTimeMillis();

		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		for (MultiPolygon stat : polygonsList) {

			for (MultiPolygon orb : polygonsList) {

				nfp = Orbiting.generateNFP(new MultiPolygon(stat), new MultiPolygon(orb));

				totalIts++;
			}

		}
		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long diffMemory = endMemory - startMemory;
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime);
		System.out.println("current total: " + totalIts);
		System.out.println("fails: " + Orbiting.numberOfFails);
//		System.out.println("infinite stuck: " + Orbiting.numberStuckInfinite);		
		System.out.println("duration: " + duration + " ms");
		System.out.println("total itterations: " + totalIts);
		System.out.println("Memory used: " + diffMemory);
		Orbiting.numberOfFails = 0;
		Orbiting.numberStuckInfinite = 0;
		Orbiting.numberOfSecFails = 0;
		return nfp;
	}
}
