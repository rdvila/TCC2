package br.furb.view.ui;

import br.furb.packing.PackingResult;

public interface IDataChangeListener {

	void notifyChanged();

	void notifyChanged(PackingResult packingResult);

}
