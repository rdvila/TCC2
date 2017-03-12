package br.furb.packing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class TabuList<T> {

	private final CircularFifoBuffer tabuList;

	private final Set<Entry<T>> auxTabuList;

	public TabuList(int tabuLenght) {
		tabuList = new CircularFifoBuffer(tabuLenght);
		auxTabuList = new HashSet<Entry<T>>(tabuLenght);
	}

	public boolean contains(T[] generatedNeighbour) {
		Entry<T> entry = new Entry<T>(generatedNeighbour);
		boolean contains = auxTabuList.contains(entry);
		return contains;
	}

	public void add(T[] generatedNeighbour) {
		Entry<T> entry = new Entry<T>(generatedNeighbour);
		if (tabuList.isFull()) {
			Object removed = tabuList.remove();
			auxTabuList.remove(removed);
		}
		tabuList.add(entry);
		auxTabuList.add(entry);
	}

	private static class Entry<T> {
		private final T[] array;

		public Entry(T[] array) {
			this.array = array;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry<?> other = (Entry<?>) obj;
			return Arrays.equals(array, other.array);
		}
	}

}
