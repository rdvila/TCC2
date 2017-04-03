import sys
import decimal
from svg.path import Path, Line, Arc, CubicBezier, QuadraticBezier
from svg.path import parse_path
import xml.etree.ElementTree as ET
from random import choice


def frange(x, y, jump):
	while x < y:
		yield x
		x += jump
		
def clamp(x): 
  return max(0, min(x, 255))

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
       style="fill:none;fill-rule:evenodd;stroke:#{0:02x}{1:02x}{2:02x};stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"
       d="'''
svgpath2 = ''' Z"/>
'''

print(SVG1)
for p in paths:
	path     = parse_path(p.attrib["d"])
	start    = None
	for px in list(frange(0, 1, decimal.Decimal('0.05'))):
		new_path = Path()
		if start is None:
			start = path.point(float(px))
		else:
			end   = path.point(float(px))
			new_path.append(Line(start, end))
			start = end
			r = choice(range(256))
			g = choice(range(256))
			b = choice(range(256))
			print((svgpath1 + new_path.d() + svgpath2).format(clamp(r), clamp(g), clamp(b)))
print(SVG2)