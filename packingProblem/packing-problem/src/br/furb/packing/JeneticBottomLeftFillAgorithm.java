package br.furb.packing;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.awt.geom.Line2D;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.SinglePointCrossover;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.codecs;
import org.jenetics.util.Factory;
import org.jenetics.util.IntRange;

import br.furb.common.MathHelper;
import br.furb.common.Point;
import br.furb.common.Polygon;
import br.furb.common.Transform;

public class JeneticBottomLeftFillAgorithm {

	static Polygon[] mPolygonsList;
	static int mRotationsNumber;
	static double mSheetHeight;
	static NFPImplementation mNFP;
	static int countExecution = 0;
	static Integer syncSemaphore = 0;

	static Double fitness(final int[] gt) {
		ArrayList<Integer> rotationSequence =  new ArrayList<>(); // voltar aqui
		for(int i=0; i< gt.length; i++) {
			rotationSequence.add(gt[i]);
		}
		 double height = __doPacking(mPolygonsList, rotationSequence, mRotationsNumber, mSheetHeight).getHeight();
			
		 assert (height > 0);
		 assert (height > 1);
		 
		 return height;
	}

	public PackingResult doPacking(NFPImplementation nfp, Polygon[] polygonsList, int rotationsNumber,
			double sheetHeight, int generations, int populationSize, int time) {

		JeneticBottomLeftFillAgorithm.mPolygonsList = polygonsList;
		JeneticBottomLeftFillAgorithm.mRotationsNumber = rotationsNumber;
		JeneticBottomLeftFillAgorithm.mSheetHeight = sheetHeight;
		JeneticBottomLeftFillAgorithm.mNFP = nfp;
		
		int ITEMS_LEN = polygonsList.length;
		int GENERATIONS = generations;
		
		int maxPhenenonAge = (int) (GENERATIONS * 0.2);
		int tournamentSelectorFactor = (int) (GENERATIONS * 0.10);
		double mutatorFactor = 0.1;
		double crossoverFactor = 0.5;
		int steadyFitness = (int) (GENERATIONS * 0.3);
		
		boolean hasMaxPhenenonAge = System.getProperty( "pmaxPhenenonAge" ) != null;
		boolean hasTournamentSelectorFactor = System.getProperty("tournamentSelectorFactor") != null;
		boolean hasMutatorFactor = System.getProperty( "pmutatorFactor" ) != null;
		boolean hasCrossoverFactor = System.getProperty( "pcrossoverFactor" ) != null;
		boolean hasSteadyFitness = System.getProperty( "psteadyFitness" ) != null;
		
		if (hasMaxPhenenonAge) {
			maxPhenenonAge = Integer.parseInt(System.getProperty("pmaxPhenenonAge"));
			System.out.println("maxPhenenonAge changed to: " + String.valueOf(maxPhenenonAge));
		}
		
		if (hasTournamentSelectorFactor) {
			tournamentSelectorFactor = (int)(GENERATIONS * Double.parseDouble(System.getProperty("tournamentSelectorFactor")));
			System.out.println("tournamentSelectorFactor changed to: " + String.valueOf(tournamentSelectorFactor));
		}
		
		if (hasMutatorFactor) {
			mutatorFactor = Double.parseDouble(System.getProperty("pswapMutatorFactor"));
			System.out.println("swapMutatorFactor changed to: " + String.valueOf(mutatorFactor));
		}
		
		if (hasCrossoverFactor) {
			crossoverFactor = Double.parseDouble(System.getProperty("pcrossoverFactor"));
			System.out.println("crossoverFactor changed to: " + String.valueOf(crossoverFactor));
		}
		
		if (hasSteadyFitness) {
			steadyFitness = Integer.parseInt(System.getProperty("psteadyFitness"));
			System.out.println("steadyFitness changed to: " + String.valueOf(steadyFitness));
		}
		
		if (tournamentSelectorFactor < 2 ) {
			tournamentSelectorFactor = 2;
		}
		
		if (maxPhenenonAge < 2) {
			maxPhenenonAge = 2;
		}

		Codec<int[], IntegerGene> gtf = codecs.ofVector(IntRange.of(1, mRotationsNumber), ITEMS_LEN);
		final Engine<IntegerGene, Double> engine = Engine.builder(JeneticBottomLeftFillAgorithm::fitness, gtf)
				.optimize(Optimize.MINIMUM)
				.maximalPhenotypeAge(maxPhenenonAge)
				.populationSize(populationSize)
				.survivorsSelector(new StochasticUniversalSelector<IntegerGene, Double>())
				.offspringSelector(new TournamentSelector<>(tournamentSelectorFactor))
				.alterers(
						new Mutator<>(mutatorFactor),
						new SinglePointCrossover<>(crossoverFactor))
				.executor(Executors.newFixedThreadPool(8))
				.build();

		final Phenotype<IntegerGene, Double> best = 
				engine.stream()
				// Truncate the evolution stream after `steadyFitness` "steady" generations.
				.limit(bySteadyFitness(steadyFitness))
				// The evolution will stop after maximal time.
				.limit(byExecutionTime(Duration.ofMillis(time), Clock.systemUTC()))
				// The evolution will stop after maximal GENERATIONS generations.
				.limit(GENERATIONS)
				// Collect (reduce) the evolution stream to its best phenotype.
				.collect(toBestPhenotype());
		
		Genotype<IntegerGene> gt = best.getGenotype();
		ArrayList<Integer> rotationSequence =  new ArrayList<>(); // voltar aqui
		for(int i=0; i< gt.getNumberOfGenes(); i++) {
			rotationSequence.add(gt.get(0, i).getAllele());
		}
		
		 assert(rotationSequence.size() > 0);
		 assert(rotationSequence.size() > 1);
		 PackingResult packing = __doPacking(mPolygonsList, rotationSequence, mRotationsNumber, mSheetHeight);
		 double height = packing.getHeight();
		 assert (height > 0);
		 assert (height > 1);
		 return packing;
		 
		
	}

	private static PackingResult __doPacking(Polygon[] polygonsList, ArrayList<Integer> rotationSequence, int maxRotationNumber, double sheetHeight) {
		
		double resolution = 0.0;
		double maxHeight = 0;
		
		resolution = sheetHeight * 0.01;		

		Polygon[] rotadedPolygons = generatePolygons(polygonsList, rotationSequence, maxRotationNumber);
		ArrayList<Polygon> sheetShapes = new ArrayList<>();
		Point currentXPosition = new Point();
		
		currentXPosition.x = 0;		
		placeShape(rotadedPolygons[0], currentXPosition, 0);
		sheetShapes.add(rotadedPolygons[0]);


		// try all rotations configured
		for (int j = 1; j < rotadedPolygons.length; j++) {
			currentXPosition.x = 0;

			// place shape[i][j] at bottom left of sheet;
			placeShape(rotadedPolygons[j], currentXPosition, 0);

			
			boolean overlapped = false;
			do {
				// rever esse j no overlap
				overlapped = comparePolygons(rotadedPolygons[j], sheetShapes, sheetHeight, resolution, currentXPosition);
			} while (overlapped);
			
			sheetShapes.add(rotadedPolygons[j]);
		}
		
		assert (sheetShapes.size() == polygonsList.length);
		
		Polygon[] arraySheetShapes = new Polygon[sheetShapes.size()];
		for (int i=0; i<sheetShapes.size(); i++) {
			arraySheetShapes[i] = sheetShapes.get(i);
		}
		
		return new PackingResult(arraySheetShapes, maxHeight);
	}

	private static boolean comparePolygons(Polygon polygon, ArrayList<Polygon> sheetShapes, double sheetHeight, double resolution, Point currentXPosition) {
		boolean resolvedOverlapping = false;

		// while (Overlap(shapes[i][j], sheetshape[1..q])) // find feasible
		// position
		for (int j = 0; j < sheetShapes.size(); j++) {

			if (overlap(polygon, sheetShapes.get(j))) {
				// Resolve Overlapping
				resolvedOverlapping |= resolveOverlapping(polygon, sheetShapes.get(j));
				if (overlap(polygon, sheetShapes.get(j))) {
					assert resolveOverlapping(polygon, sheetShapes.get(j)) == false : "Overlapping error: sheetShape: "
							+ sheetShapes.get(j) + "polygon: " + polygon;
				}
			}
			// if (shape[i][j] off top of sheet) { x = x + resolution; place
			// shape[i][j] at (x,0); }
			if (polygon.maxY().getY() > sheetHeight) {
				currentXPosition.x += resolution;
				// Restart comparisons
				j = -1;
				// place shape[i][j] at bottom left of sheet plus
				// currentXPosition;
				placeShape(polygon, currentXPosition, 0);

			}
		}
		return resolvedOverlapping;
	}

	private static boolean resolveOverlapping(Polygon polygon, Polygon sheetShape) {
		Polygon noFitPolygonCached = getNoFitPolygon(polygon, sheetShape);

		Point ref = new Point();		
		ref.x = polygon.getRefPoint().x;
		ref.y = polygon.getRefPoint().y;

		boolean overlapping = false;
		double maxDistance = 0;

		List<Point> points = noFitPolygonCached.getPoints();
		for (Point point : points) {

			Line2D line2d = new Line2D.Double(point.x, point.y, point.next.x, point.next.y);
			if (line2d.intersectsLine(ref.x, ref.y, ref.x, Double.MAX_VALUE)) {

				// intersecting lines segment intersection point
				Point intesectionPoint = MathHelper.findIntersection(/**/polygon.getRefPoint(),
						new Point(ref.x, Float.MAX_VALUE), /**/new Point(line2d.getX1(), line2d.getY1()),
						new Point(line2d.getX2(), line2d.getY2()));

				double distance = MathHelper.calcularDistancia(polygon.getRefPoint(), intesectionPoint);
				distance = Double.isNaN(distance) ? 0 : distance;
				// System.out.println(distance);

				// if (distance > Transform.THRESHOLD) {
				// placeShape(polygon, refX, refY + distance);
				// overlapping = true;
				// break;
				// }

				if (distance > maxDistance) {
					maxDistance = distance;
				}

			}
		}
		if (maxDistance > Transform.THRESHOLD) { 
			placeShape(polygon, ref, ref.y + maxDistance);
			overlapping = true;
			// break;
		}
		return overlapping;
	}

	private static boolean overlap(Polygon polygon, Polygon sheetShape) {
		Polygon noFitPolygonCached = getNoFitPolygon(polygon, sheetShape);
		return MathHelper.isInside(polygon.getRefPoint(), noFitPolygonCached);
	}

	private static void placeShape(Polygon polygon, Point currentXPosition, double y) {
		Transform transform = new Transform();
		if (y == 0) {
			transform.executeTranslationPolygon(new Point(polygon.minY().x - polygon.minX().x + currentXPosition.x, y), polygon.minY(),
					polygon);
		} else {
			transform.executeTranslationPolygon(new Point(currentXPosition.x, y), polygon.getRefPoint(), polygon);
		}
	}

	private static Polygon getNoFitPolygon(Polygon polygon, Polygon sheetShape) {
		NFPImplementation nfp = JeneticBottomLeftFillAgorithm.mNFP.getnewInstance();
		Polygon noFitPolygonCached = nfp.calculateNotFitPolygon(sheetShape, polygon);
		moveShape(noFitPolygonCached, sheetShape.minYRight());
		return noFitPolygonCached;
	}

	private static void moveShape(Polygon polygon, Point target) {
		Transform transform = new Transform();
		transform.executeTranslationPolygon(target, polygon.minYRight(), polygon);
	}

	/**
	 * Gera os polígonos rotacionados
	 * 
	 * @param polygonsList
	 * @param rotationsNumber
	 * @return
	 */
	private static Polygon[] generatePolygons(Polygon[] polygonsList, ArrayList<Integer> rotationSequence, int maxRotationNumber) {
		double angleAlpha = 360 / maxRotationNumber;
		Polygon[] polygonsRotated= new Polygon[rotationSequence.size()];
		
		for (int i = 0; i < rotationSequence.size(); i++) {
			Transform transform = new Transform();
			Polygon originalPolygon = polygonsList[i];
			
			Polygon rotatedPolygon = transform.executeRotation(originalPolygon.getRefPoint(), //
					angleAlpha * rotationSequence.get(i), -1, originalPolygon);

			assert originalPolygon.getPoints().size() == rotatedPolygon.getPoints().size();

			polygonsRotated[i] = rotatedPolygon;
		}
		
		return polygonsRotated;
	}
}
