package br.furb.packing;

public enum LocalSearch {

	HILL_CLIMBING, //
	TABU_SEARCH,
	GENETIC;

	public static LocalSearch getValue(String value) {
		if ("Hill Climbing".equals(value)) {
			return HILL_CLIMBING;
		} else if ("Tabu Search".equals(value)) {
			return TABU_SEARCH;
		} else if ("Genetic".equals(value)) {
				return GENETIC;
		} else {
			throw new IllegalArgumentException("Invalid Local Search: " + value);
		}
	}

}
