package br.furb.packing;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import br.furb.common.Polygon;
import br.furb.view.ui.IDataChangeListener;

public class HillClimbingAlgorithm  implements PackingAlgorithm {

	private final Random random = new Random();

	private final Random randomPolygon = new Random();

	private IDataChangeListener[] listeners;

	public PackingResult doPacking(NFPImplementation nfp, Polygon[] polygonsList, int rotationsNumber, double sheetHeight, //
			StopCriteria stopCriteria, int stopValue) {

		IStopCriteria stopControl = StopCriteriaControl.getStopCriteria(stopCriteria, stopValue);

		PackingResult bestResult = null;
		BottomLeftFillAgorithm bottomLeftFill = new BottomLeftFillAgorithm(nfp);

		PackingResult packingResult = bottomLeftFill.doPacking(polygonsList, rotationsNumber, sheetHeight);
		bestResult = packingResult;
		notifyListeners(bestResult);
		Polygon[] bestPolygonList = polygonsList;
		Polygon[] currentPolygonList;

		for (; stopControl.continueRun();) {

			EnOpt opt = selectOperator();
			currentPolygonList = generateNeighbour(opt, bestPolygonList);

			packingResult = bottomLeftFill.doPacking(currentPolygonList, rotationsNumber, sheetHeight);

			if (packingResult.getHeight() < bestResult.getHeight()) {
				bestResult = packingResult;
				bestPolygonList = currentPolygonList;
				notifyListeners(bestResult);
			}
		}
		return bestResult;
	}

	/**
	 * Muda a posição dos polígonos de acordo com a opção randomicamente escolhida.
	 * 
	 * @param opt
	 * @param polygonsList
	 */
	protected Polygon[] generateNeighbour(EnOpt opt, Polygon[] polygonsList) {
		Polygon[] generatedPolyginsList;

		switch (opt) {
		// Troca o polígono selecionado de lugar com o próximo
		case Opt1:
			generatedPolyginsList = generateNeighbourOpt1(polygonsList);
			break;
		case Opt2:
			generatedPolyginsList = generateNeighbour(2, polygonsList);
			break;
		case Opt3:
			generatedPolyginsList = generateNeighbour(3, polygonsList);
			break;
		case Opt4:
			generatedPolyginsList = generateNeighbour(4, polygonsList);
			break;
		case OptN:
			generatedPolyginsList = generateNeighbour(selectPolygon(polygonsList.length), polygonsList);
			break;

		default:
			throw new IllegalStateException("Operador inválido: " + opt);
		}
		return generatedPolyginsList;
	}

	/**
	 * Polígono troca de posição com o seu vizinho à direita.
	 * 
	 * @param polygonsList
	 */
	private Polygon[] generateNeighbourOpt1(Polygon[] polygons) {
		final Polygon[] polygonsList = copyPolygonsList(polygons);

		int randomPosition = selectPolygon(polygonsList.length);
		int posNextPolygon = 0;

		// não é o último polígono
		if (randomPosition != polygonsList.length - 1) {
			posNextPolygon = randomPosition + 1;
		}
		Polygon nextPolygon = polygonsList[posNextPolygon];
		polygonsList[posNextPolygon] = polygonsList[randomPosition];
		polygonsList[randomPosition] = nextPolygon;

		return polygonsList;

	}

	private Polygon[] generateNeighbour(int changes, Polygon[] polygons) {

		final Polygon[] polygonsList = copyPolygonsList(polygons);
		final Polygon[] selectedPolygons = new Polygon[changes];
		final Set<Integer> positionsSet = new HashSet<Integer>(changes);

		for (int i = 0; i < changes; i++) {
			int randomPosition = selectPolygon(polygonsList.length);
			while (positionsSet.contains(randomPosition) && polygonsList.length != positionsSet.size()) {
				randomPosition = selectPolygon(polygonsList.length);
			}
			positionsSet.add(randomPosition);
		}

		Integer[] positionsArray = new Integer[positionsSet.size()];
		positionsSet.toArray(positionsArray);
		for (int i = 0; i < positionsArray.length; i++) {
			selectedPolygons[i] = polygonsList[positionsArray[i]];
		}

		for (int i = 0; i < positionsArray.length; i++) {
			int pos = i + 1;
			if (pos == positionsArray.length) {
				pos = 0;
			}
			polygonsList[positionsArray[i]] = selectedPolygons[pos];
		}
		return polygonsList;
	}

	protected EnOpt selectOperator() {
		int randomInt = random.nextInt(100);
		return EnOpt.getOpt(randomInt);
	}

	/**
	 * Seleciona um polígono aleatória na lista de polígonos
	 * 
	 * @param length
	 * @return
	 */
	private int selectPolygon(int length) {
		return randomPolygon.nextInt(length);
	}

	private Polygon[] copyPolygonsList(Polygon[] polygonsList) {
		final Polygon[] polygonsCopy = new Polygon[polygonsList.length];
		for (int i = 0; i < polygonsList.length; i++) {
			try {
				polygonsCopy[i] = (Polygon) polygonsList[i].clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		return polygonsCopy;
	}

	protected static enum EnOpt {
		Opt1, //
		Opt2, //
		Opt3, //
		Opt4, //
		OptN; //

		public static EnOpt getOpt(int randomInt) {
			EnOpt opt = null;
			if (randomInt < 30) {// 30%,
				opt = Opt1;
			} else if (randomInt < 55) {// 25%,
				opt = Opt2;
			} else if (randomInt < 75) {// 20%,
				opt = Opt3;
			} else if (randomInt < 90) {// 15%,
				opt = Opt4;
			} else if (randomInt < 100) {// 10%,
				opt = OptN;
			} else {
				throw new IllegalStateException("Valor incorreto: " + randomInt);
			}
			return opt;
		}
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
