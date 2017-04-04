for x in fu poly1a poly2b poly3b poly4b; do
	for y in $x-converted*-info.txt; do
		cat $y >> $x-converted-all.txt && echo >> $x-converted-all.txt;
	done;
done;
	