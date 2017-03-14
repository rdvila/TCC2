package br.furb.packing.jenetic;

import org.jenetics.EnumGene;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.Phenotype;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.codecs;

import br.furb.common.Polygon;
import br.furb.packing.BottomLeftFillAgorithm;
import br.furb.packing.PackingAlgorithm;
import br.furb.packing.PackingResult;
import br.furb.packing.StopCriteria;
import br.furb.view.ui.IDataChangeListener;

import static org.jenetics.engine.limit.bySteadyFitness;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

public class JeneticAlgorithm implements PackingAlgorithm {
	
	static Polygon[] mPolygonsList;
	static int mRotationsNumber;
	static double mSheetHeight;
	
	// Calculate the path length of the current genotype.
	private static PackingResult dist(final int[] permutation) {		
		Polygon[] polygonsList = new Polygon[mPolygonsList.length];
		for (int i=0;i<permutation.length;i++) {
			polygonsList[i] = mPolygonsList[permutation[i]]; // check limits
		}
		BottomLeftFillAgorithm bottomLeftFill = new BottomLeftFillAgorithm();
		return bottomLeftFill.doPacking(polygonsList, mRotationsNumber, mSheetHeight);
	}

	private IDataChangeListener[] listeners;
				

	@Override
	public PackingResult doPacking(Polygon[] polygonsList, int rotationsNumber, double sheetHeight,
			StopCriteria stopCriteria, int stopValue) {
		
		JeneticAlgorithm.mPolygonsList = polygonsList;
		JeneticAlgorithm.mRotationsNumber = rotationsNumber;
		JeneticAlgorithm.mSheetHeight = sheetHeight;
		
		int ITEMS_LEN = polygonsList.length;
		int POPULATION_MAX = stopValue;
		int POPULATION_SIZE = 50;
		
		final Engine<EnumGene<Integer>, PackingResult> engine = Engine
				.builder(
						JeneticAlgorithm::dist,
					codecs.ofPermutation(ITEMS_LEN))
				.optimize(Optimize.MINIMUM)
				.maximalPhenotypeAge(11)
				.populationSize(POPULATION_SIZE)
				.alterers(
					new SwapMutator<>(0.2),
					new PartiallyMatchedCrossover<>(0.35))
				.build();

			// Create evolution statistics consumer.
			final EvolutionStatistics<PackingResult, ?>
				statistics = EvolutionStatistics.ofComparable();

			final Phenotype<EnumGene<Integer>, PackingResult> best =
				engine.stream()
				// Truncate the evolution stream after 7 "steady"
				// generations.
				.limit(bySteadyFitness(15))
				// The evolution will stop after maximal 100
				// generations.
				.limit(POPULATION_MAX)
				// Update the evaluation statistics after
				// each generation
				.peek(statistics)
				// Collect (reduce) the evolution stream to
				// its best phenotype.
				.collect(toBestPhenotype());

			System.out.println(statistics);
			System.out.println(best.getFitness().getHeight());
			notifyListeners(best.getFitness());
		
		
		return best.getFitness();
	}

	public void addLisneter(IDataChangeListener[] listeners) {
		this.listeners = listeners;
	}

	public void notifyListeners(PackingResult result) {
		for (IDataChangeListener listener : listeners) {
			listener.notifyChanged(result);
		}
	}



}
