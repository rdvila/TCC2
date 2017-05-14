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
		System.out.println((System.currentTimeMillis() - currentMillis));
		System.out.println(stopValue);
		System.out.println((System.currentTimeMillis() - currentMillis) < stopValue);
		return (System.currentTimeMillis() - currentMillis) < stopValue;
	}

}
