package br.furb.dataset;

import br.furb.common.Polygon;

public interface DatasetReader {
	
	Polygon[] readXML(String filePath);
	double getBorderX();
	double getBorderY();

}
