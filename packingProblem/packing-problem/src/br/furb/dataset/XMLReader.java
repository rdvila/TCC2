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
import br.furb.common.PolygonGenerator;

public class XMLReader implements DatasetReader {

	private double borderX;
	private double borderY;

	public double getBorderX() {
		return borderX;
	}

	public double getBorderY() {
		return borderY;
	}

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
			NodeList nodeLst = doc.getElementsByTagName("polygons");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;
					NodeList polLst = fstElmnt.getElementsByTagName("polygon");
					int i = 0;
					if (polLst.getLength() > 0) {
						Node polyNode = polLst.item(i);
						Element polyElmnt = (Element) polyNode;
						loop: while (polyElmnt.getAttribute("id").startsWith(
								"polygon")) {// Deve ser polygon

							if (i == 0) {
								borderX = Double.parseDouble(polyElmnt
										.getElementsByTagName("xMax").item(0)
										.getFirstChild().getNodeValue());
								borderY = Double.parseDouble(polyElmnt
										.getElementsByTagName("yMax").item(0)
										.getFirstChild().getNodeValue());
								i++;
								polyElmnt = (Element) polLst.item(i);
								continue loop;
							}

							Polygon polygon = new Polygon(
									PolygonGenerator.getId());

							NodeList linesLst = polyElmnt
									.getElementsByTagName("segment");
							for (int j = 0; j < linesLst.getLength(); j++) {
								Node lineSegment = linesLst.item(j);
								if (lineSegment.getNodeType() == Node.ELEMENT_NODE) {
									Element segmentElmnt = (Element) lineSegment;
									String x0 = segmentElmnt.getAttribute("x0");
									String y0 = segmentElmnt.getAttribute("y0");
									polygon.addPoint(new Point(Double
											.parseDouble(x0), Double
											.parseDouble(y0)));
								}
							}
							polygons.add(polygon);
							i++;
							polyElmnt = (Element) polLst.item(i);
						}
					}
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

}
