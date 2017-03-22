package br.furb.packing;

import br.furb.common.Polygon;
import br.furb.view.ui.IDataChangeListener;

public interface PackingAlgorithm {

	PackingResult doPacking(NFPImplementation nfpImplementation, Polygon[] polygonsList, int rotationsNumber, double sheetHeight, //
			StopCriteria stopCriteria, int stopValue);
	
	void addLisneter(IDataChangeListener[] listeners);

	void notifyListeners(PackingResult result);
	
}
