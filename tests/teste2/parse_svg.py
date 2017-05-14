import sys
import decimal
from svg.path import Path, Line, Arc, CubicBezier, QuadraticBezier
from svg.path import parse_path
import xml.etree.ElementTree as ET

def frange(x, y, jump):
  while x < y:
    yield x
    x += jump

ns = {'svg': 'http://www.w3.org/2000/svg'}
inputfile = sys.argv[1];

tree = ET.parse(inputfile)
root = tree.getroot()
paths = root.findall('./svg:g/svg:path', ns)

SVG1 = '''<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg    xmlns:dc="http://purl.org/dc/elements/1.1/"   xmlns:cc="http://creativecommons.org/ns#"   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"   xmlns:svg="http://www.w3.org/2000/svg"   xmlns="http://www.w3.org/2000/svg"    xmlns:sodipodi="http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd"   xmlns:inkscape="http://www.inkscape.org/namespaces/inkscape" width="500" height="500">
<g>
'''

SVG2 = '''  </g>
</svg>
'''

svgpath1 = '''<path
       style="fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"
       d="'''
svgpath2 = ''' Z"/>
'''

print(SVG1)
for p in paths:
	new_path = Path()
	path     = parse_path(p.attrib["d"])
	start    = None
	for px in list(frange(0, 1, decimal.Decimal('0.05'))):
		if start is None:
			start = path.point(float(px))
			print(start)
		else:
			end   = path.point(float(px))
			new_path.append(Line(start, end))
			start = end
	print(svgpath1 + new_path.d() + svgpath2)
print(SVG2)