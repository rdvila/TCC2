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

svgpath1 = '''<path
       style="fill:none;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"
       d="'''
svgpath2 = ''' Z"/>
'''

for p in paths:
	new_path = Path()
	path     = parse_path(p.attrib["d"])
	for sp in path:
		name = type(sp).__name__
		if name == 'Line':
			new_path.append(sp)
		else:
			start = None
			values = list(frange(0, 1, decimal.Decimal('0.01')))
			for px in values:
				if start is None:
					start = sp.point(float(px))
				else:
					end   = sp.point(float(px))
					new_path.append(Line(start, end))
					start = end
	print(svgpath1 + new_path.d() + svgpath2)