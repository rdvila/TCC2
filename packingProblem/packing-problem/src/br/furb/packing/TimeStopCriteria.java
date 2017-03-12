package br.furb.packing;

public class TimeStopCriteria implements IStopCriteria {

	private final int stopValue;

	private final long currentMillis;

	public TimeStopCriteria(int stopValue) {
		this.stopValue = stopValue;
		currentMillis = System.currentTimeMillis();
	}

	@Override
	public boolean continueRun() {
		return (System.currentTimeMillis() - currentMillis) < stopValue;
	}

}
