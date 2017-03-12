package br.furb.packing;

public class LoopStopCriteria implements IStopCriteria {

	private final int stopValue;

	private int currentCount;

	public LoopStopCriteria(int stopValue) {
		this.stopValue = stopValue;
	}

	@Override
	public boolean continueRun() {
		return currentCount++ < stopValue;
	}

}
