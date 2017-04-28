package br.furb.packing;

public enum LocalSearch {

	HILL_CLIMBING, //
	TABU_SEARCH,
	JENETIC;

	public static LocalSearch getValue(String value) {
		if ("Hill Climbing".equals(value)) {
			return HILL_CLIMBING;
		} else if ("Tabu Search".equals(value)) {
			return TABU_SEARCH;
		} else if ("Jenetic".equals(value)) {
			return JENETIC;
		} else {
			throw new IllegalArgumentException("Invalid Local Search: " + value);
		}
	}

}
