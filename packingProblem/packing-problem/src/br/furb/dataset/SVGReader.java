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
	
	public SVGReader() {
		borderX = 50;
		borderY = 50;
	}

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
			NodeList nodeLst = doc.getElementsByTagName("path");
			System.out.println("Information of all polygons");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) fstNode;

					polygons.add(toPolygon(element.getAttribute("d").replaceAll("[mz]", ""), s));
											
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

	private Polygon toPolygon(String d, int id) {
		Polygon p = new Polygon(id);		
		String[] paths = d.trim().split(" ");		
		double X = 0;
		double Y = 0;
		for (int i=0; i<paths.length; i++) {
			String[] splt = paths[i].split(",");
			X = Double.valueOf(splt[0]);
			Y = Double.valueOf(splt[1]);
			p.addPoint(new Point(X, Y));
		}
		return p;
	}

	@Override
	public double getBorderX() {
		return borderX;
	}

	@Override
	public double getBorderY() {
		return borderY;
	}
}
