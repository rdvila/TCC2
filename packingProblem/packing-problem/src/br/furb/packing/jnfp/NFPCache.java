package br.furb.packing.jnfp;

import java.util.HashMap;

import br.furb.common.Polygon;

public class NFPCache {
	
	private HashMap<NFPExecution, NoFitPolygon> cache = new HashMap<>();
	static NFPCache instance = null;
	
	private NFPCache() {
	}
	
	static public synchronized NFPCache getInstance() {
		if (instance == null) {
			instance = new NFPCache();
		}
		return instance;
	}
	
	public void clear() {
		cache.clear();
	}
	
	public void add(Polygon a, Polygon b, NoFitPolygon nfp) {
		cache.put(new NFPExecution(a, b), nfp);
	}
	
	public NoFitPolygon get(Polygon a, Polygon b) {
		NFPExecution exec = new NFPExecution(a, b);
		if (cache.containsKey(exec)) {
			return cache.get(exec);
		}	
		return null;
	}
		
	class NFPExecution {
		
		private Polygon a;
		private Polygon b;
				
		public NFPExecution(Polygon a, Polygon b) {
			this.a = a;
			this.b = b;
		}

		public Polygon getA() {
			return a;
		}

		public void setA(Polygon a) {
			this.a = a;
		}

		public Polygon getB() {
			return b;
		}

		public void setB(Polygon b) {
			this.b = b;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj != null && obj instanceof NFPExecution) {
				NFPExecution exec = (NFPExecution)obj;
				return exec.a.equals(a) && exec.b.equals(b);			
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return a.hashCode() * b.hashCode();
		}
		
	}
}
