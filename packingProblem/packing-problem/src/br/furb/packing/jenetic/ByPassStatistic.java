package br.furb.packing.jenetic;

import java.util.function.Consumer;

import org.jenetics.engine.EvolutionResult;

import br.furb.packing.PackingResult;

public class ByPassStatistic<
C extends Comparable<? super C>,
FitnessStatistics
>
implements Consumer<EvolutionResult<?, C>>  {

	private JeneticAlgorithm algorithm;

	public ByPassStatistic(JeneticAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	@Override
	public void accept(EvolutionResult<?, C> t) {
		PackingResult best = (PackingResult) t.getBestFitness();
		algorithm.notifyListeners(best);
	}
}
