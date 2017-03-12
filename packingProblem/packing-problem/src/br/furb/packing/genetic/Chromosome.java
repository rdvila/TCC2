package br.furb.packing.genetic;

import br.furb.common.Polygon;
import br.furb.packing.BottomLeftFillAgorithm;
import br.furb.packing.PackingResult;

public class Chromosome {
	
	Polygon[] polygonsList;
	int rotationsNumber;
	double sheetHeight;
	PackingResult result;
	
	public Chromosome(Polygon[] polygonsList, int rotationsNumber, double sheetHeight) {
		super();
		this.polygonsList = polygonsList;
		this.rotationsNumber = rotationsNumber;
		this.sheetHeight = sheetHeight;
	}

	public void evolve() {
		BottomLeftFillAgorithm bottomLeftFill = new BottomLeftFillAgorithm();
		this.result = bottomLeftFill.doPacking(polygonsList, rotationsNumber, sheetHeight);
	}

	public Polygon[] getPolygonsList() {
		return polygonsList;
	}

	public void setPolygonsList(Polygon[] polygonsList) {
		this.polygonsList = polygonsList;
	}

	public int getRotationsNumber() {
		return rotationsNumber;
	}

	public void setRotationsNumber(int rotationsNumber) {
		this.rotationsNumber = rotationsNumber;
	}

	public double getSheetHeight() {
		return sheetHeight;
	}

	public void setSheetHeight(double sheetHeight) {
		this.sheetHeight = sheetHeight;
	}

	public PackingResult getResult() {
		return result;
	}

	public void setResult(PackingResult result) {
		this.result = result;
	}
}