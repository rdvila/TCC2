for x in dagli dighe1 dighe2 fu han jakobs1 jakobs2 marques poly1a poly2b poly3b poly4b poly5b shapes0 shapes1 shirts trousers; do
	for y in $x-converted*-info.txt; do
		cat $y >> $x-converted-all.txt && echo >> $x-converted-all.txt;
	done;
done;
	