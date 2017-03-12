package br.furb.packing;

public class StopCriteriaControl {

	public static IStopCriteria getStopCriteria(StopCriteria stopCriteria, int stopValue) {
		if (stopCriteria == StopCriteria.LOOP) {
			return new LoopStopCriteria(stopValue);
		} else if (stopCriteria == StopCriteria.TIME) {
			return new TimeStopCriteria(stopValue);
		}
		throw new IllegalArgumentException("Invalid Stop Criteria: " + stopCriteria);
	}
}
