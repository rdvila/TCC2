package br.furb.dataset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import br.furb.common.Point;
import br.furb.common.Polygon;

public class SVGWriter {
	
	String SVG1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<svg    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"   xmlns:cc=\"http://creativecommons.org/ns#\"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"   xmlns:svg=\"http://www.w3.org/2000/svg\"   xmlns=\"http://www.w3.org/2000/svg\"    xmlns:sodipodi=\"http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd\"   xmlns:inkscape=\"http://www.inkscape.org/namespaces/inkscape\" width=\"%d\" height=\"%d\">\n<g>\n";
	String PATH = "<path id=\"%s\" style=\"fill:%s;fill-rule:evenodd;stroke:#000000;stroke-width:0;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1\" d=\"%s\"/>\n";
	String SVG2 = "</g>\n</svg>";
	
	public void writeXML(String name, Polygon[] polygons, double width, double height) {
		try {
			Files.write(Paths.get(name), String.format(SVG1, Double.valueOf(width).intValue(), Double.valueOf(height).intValue()).getBytes(), StandardOpenOption.CREATE);
			
			for (Polygon p : polygons) {
				Files.write(Paths.get(name), String.format(PATH, "path"+String.valueOf(p.getId()), p.color(), toSVGPath(p)).getBytes(), StandardOpenOption.APPEND);
			}
			
			Files.write(Paths.get(name), SVG2.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String toSVGPath(Polygon p) {
		
		StringBuilder buffer = new StringBuilder();	
		List<Point> points = p.getPoints(); 
		Point po = points.get(0);
		double x = po.getX();
		double y = po.getY();
		
		buffer.append(String.format(Locale.US, "m %f,%f ", x,y));
		for (int i=1; i<points.size(); i++) {
			Point pl = points.get(i-1);
			po = points.get(i);
			x = po.getX() - pl.getX();
			y = po.getY() - pl.getY();
			String formated = String.format(Locale.US, "%f,%f ", x,y); 
			buffer.append(formated);
		}
		buffer.append("z");	
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		
		String[] names = {"nest4"};
		String path    = "C:\\Users\\rodrigo\\Desktop\\";
		
		for (String name: names) {
			SVGReader reader = new SVGReader();
			SVGWriter writer = new SVGWriter();
			Polygon[] polygons = reader.readXML(path+name+".svg");
			for (Polygon p : polygons) {
				for (Point po : p.getPoints()) {
					System.out.println(String.format("%f x %f", po.getX(), po.getY()));
				}
			}
			writer.writeXML(path+name+"-converted.svg", polygons, reader.getBorderX(), reader.getBorderY());
		}
	}
}

