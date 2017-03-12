package br.furb.packing;

public enum StopCriteria {

	LOOP, //
	TIME;

	public static StopCriteria getValue(String value) {
		if ("Loop".equals(value)) {
			return LOOP;
		} else if ("Tempo".equals(value)) {
			return TIME;
		} else {
			throw new IllegalArgumentException("Invalid Stop Criteria: " + value);
		}
	}

}
