package br.furb.packing.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import br.furb.common.Polygon;
import br.furb.packing.BottomLeftFillAgorithm;
import br.furb.packing.PackingResult;

public class Chromosome {
	
	Polygon[] polygonsList;
	int rotationsNumber;
	double sheetHeight;
	PackingResult result;
	private Random random;
	
	public Chromosome(Polygon[] polygonsList, int rotationsNumber, double sheetHeight, Random random) {
		super();
		this.polygonsList = polygonsList;
		this.rotationsNumber = rotationsNumber;
		this.sheetHeight = sheetHeight;
		this.random = random;
	}
	
	public Chromosome copy() {
		
		Polygon[] copyPolygonsList = new Polygon[polygonsList.length];
		for (int i=0; i<polygonsList.length; i++) {
			copyPolygonsList[i] = polygonsList[i];
		}
		
		return new Chromosome(copyPolygonsList, rotationsNumber, sheetHeight, random);
	}
	
	public Chromosome shuffle() {
		
		ArrayList<Polygon> copyPolygonsArrayList = new ArrayList<Polygon>();
		for (int i=0; i<polygonsList.length; i++) {
			copyPolygonsArrayList.add(polygonsList[i]);
		}
		Collections.shuffle(copyPolygonsArrayList, random);
		Polygon[] copyPolygonsList = new Polygon[copyPolygonsArrayList.size()];
		copyPolygonsArrayList.toArray(copyPolygonsList);
		
		
		return new Chromosome(copyPolygonsList, rotationsNumber, sheetHeight, random);
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