package br.furb.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.furb.common.Point;
import br.furb.common.Polygon;

public class SVGReader implements DatasetReader {

	private double borderX;
	private double borderY;

	@Override
	public Polygon[] readXML(String filePath) {
		List<Polygon> polygons = new ArrayList<Polygon>();

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new IllegalArgumentException("Arquivo não encontrado: "
						+ filePath);
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Root element "
					+ doc.getDocumentElement().getNodeName());
			
			NodeList nodeRoot = doc.getElementsByTagName("svg");
			Element elementRoot = (Element) nodeRoot.item(0);
			borderX = Double.valueOf(elementRoot.getAttribute("width").trim()).doubleValue();
			borderY = Double.valueOf(elementRoot.getAttribute("height").trim()).doubleValue();			
			
			NodeList nodeLst = doc.getElementsByTagName("path");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) fstNode;

					polygons.add(toPolygon(element.getAttribute("d").replaceAll("[zLZ]", "").trim(), s));
											
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

	private Polygon toPolygon(String d, int id) {
		Polygon p = new Polygon(id);
		
		boolean M = d.charAt(0) == 'M';
		String[] paths = d.replaceAll("[mM]", "").trim().split(" ");
		double X = 0;
		double Y = 0;		
		if (M) {
			for (int i=0; i<paths.length; i++) {
				
				if (paths[i] == null || paths[i].trim().length() == 0) {
					continue;
				}
				
				String[] splt = paths[i].split(",");
				X = Double.valueOf(splt[0]);
				Y = Double.valueOf(splt[1]);
				p.addPoint(new Point(X, Y));
			}
		} else {
			for (int i=0; i<paths.length; i++) {
				
				if (paths[i] == null || paths[i].trim().length() == 0) {
					continue;
				}
				
				String[] splt = paths[i].split(",");
				X += Double.valueOf(splt[0]);
				Y += Double.valueOf(splt[1]);
				p.addPoint(new Point(X, Y));
			}
		}
		return p.normalize();
	}

	@Override
	public double getBorderX() {
		return borderX;
	}

	@Override
	public double getBorderY() {
		return borderY;
	}
	
	public static void main(String[] args) {
		
		String[] names = {
				"albano",
				"blaz",
				"dagli",
				"dighe1",
				"dighe2",
				"fu",
				"han",
				"jakobs1",
				"jakobs2",
				"mao",
				"marques",
				"poly1a",
				"poly2a",
				"poly2b",
				"poly3a",
				"poly3b",
				"poly4a",
				"poly4b",
				"poly5a",
				"poly5b",
				"shapes0",
				"shapes1",
				"shirts",
				"swim",
				"trousers"};
		String path    = "C:\\Users\\rodrigo\\Desktop\\TCC2\\tests\\teste5\\";
		
		for (String name: names) {
			XMLReader reader = new XMLReader();
			SVGWriter writer = new SVGWriter();
			Polygon[] polygons = reader.readXML(path+name+".xml");
			for (Polygon p : polygons) {
				for (Point po : p.getPoints()) {
					System.out.println(String.format("%f x %f", po.getX(), po.getY()));
				}
			}
			writer.writeXML(path+name+"-converted.svg", polygons, reader.getBorderX(), reader.getBorderY());
		}
	}
}
