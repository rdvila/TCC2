package br.furb.packing;

import br.furb.common.Polygon;

public class TabuSearch extends HillClimbingAlgorithm {

	private static final int NEIGHBOURHOOD_SIZE = 5;

	private static final int TABU_LENGTH = 200;

	private TabuList<Polygon> tabuList;

	@Override
	public PackingResult doPacking(NFPImplementation nfp, Polygon[] polygonsList, int rotationsNumber, double sheetHeight, //
			StopCriteria stopCriteria, int stopValue) {

		IStopCriteria stopControl = StopCriteriaControl.getStopCriteria(stopCriteria, stopValue);

		tabuList = new TabuList<Polygon>(TABU_LENGTH);
		PackingResult[] neighbour = new PackingResult[NEIGHBOURHOOD_SIZE];
		tabuList.add(polygonsList);

		BottomLeftFillAgorithm bottomLeftFill = new BottomLeftFillAgorithm(nfp);
		PackingResult packingResult = bottomLeftFill.doPacking(polygonsList, rotationsNumber, sheetHeight);
		PackingResult bestEvaluation = packingResult;
		notifyListeners(bestEvaluation);
		PackingResult currentEvaluation = packingResult;

		for (; stopControl.continueRun();) {

			EnOpt opt = selectOperator();

			for (int j = 0; j < NEIGHBOURHOOD_SIZE; j++) {
				Polygon[] polygonsNeighbour = generateNotTabuNeighbour(currentEvaluation.getPacking(), opt);
				PackingResult doPacking = bottomLeftFill.doPacking(polygonsNeighbour, rotationsNumber, sheetHeight);
				neighbour[j] = doPacking;
			}
			currentEvaluation = getBestNeighbour(neighbour);

			if (currentEvaluation.getHeight() < bestEvaluation.getHeight()) {
				try {
					bestEvaluation = (PackingResult) currentEvaluation.clone();
					notifyListeners(bestEvaluation);
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return bestEvaluation;
	}

	private PackingResult getBestNeighbour(PackingResult[] neighbour) {
		PackingResult bestResult = null;
		for (int i = 0; i < neighbour.length; i++) {

			if (bestResult == null) {
				bestResult = neighbour[i];
				continue;
			}
			if (neighbour[i].getHeight() < bestResult.getHeight()) {
				bestResult = neighbour[i];
			}
		}
		return bestResult;
	}

	private Polygon[] generateNotTabuNeighbour(Polygon[] polygonsList, EnOpt opt) {
		Polygon[] generatedNeighbour;

		do {
			generatedNeighbour = generateNeighbour(opt, polygonsList);
		} while (tabuList.contains(generatedNeighbour));

		tabuList.add(generatedNeighbour);

		return generatedNeighbour;
	}

}
