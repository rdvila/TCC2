package br.furb.dataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.Locale;

import br.furb.common.Point;
import br.furb.common.Polygon;

public class SVGWriter {
	
	String SVG1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"   xmlns:cc=\"http://creativecommons.org/ns#\"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"   xmlns:svg=\"http://www.w3.org/2000/svg\"   xmlns=\"http://www.w3.org/2000/svg\"    xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"%d\" height=\"%d\">\n<g fill=\"none\" stroke=\"black\" stroke-width=\"1\">\n";
	String PATH = "<path d=\"%s\"/>\n";
	String SVG2 = "</g>\n</svg>";
	
	public void writeXML(String name, Polygon[] polygons) {
		int width  = 500;
		int height = 500;
		try {
			Files.write(Paths.get(name), String.format(SVG1, width, height).getBytes(), StandardOpenOption.CREATE);
			
			for (Polygon p : polygons) {
				Files.write(Paths.get(name), String.format(PATH, toSVGPath(p)).getBytes(), StandardOpenOption.APPEND);
			}
			
			Files.write(Paths.get(name), SVG2.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String toSVGPath(Polygon p) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("m ");
		double X=0;
		double Y=0;
		Formatter formatter = new Formatter(Locale.US);
		for (Point po : p.getPoints()) {
			double x = po.getX();
			double y = po.getY();
			buffer.append(formatter.format("%f,%f ", x,y));
			X=po.getX();
			Y=po.getY();
		}
		buffer.append("z");
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		
		String[] names = {"fu", "poly1a", "poly2b", "poly3b", "poly4b"};
		String path    = "C:\\Users\\rodrigo\\Desktop\\TCC2\\packingProblem\\packing-problem\\src\\br\\furb\\dataset\\source\\";
		
		for (String name: names) {
			XMLReader reader = new XMLReader();
			SVGWriter writer = new SVGWriter();
			Polygon[] polygons = reader.readXML(path+name+".xml");
			for (Polygon p : polygons) {
				for (Point po : p.getPoints()) {
					System.out.println(String.format("%fx%f", po.getX(), po.getY()));
				}
			}
			writer.writeXML(path+name+".svg", polygons);
		}
		
		
	}
}

