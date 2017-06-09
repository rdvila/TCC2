import csv

datasets = ['fu-converted', 'poly1a-converted', 'poly2b-converted', 'poly3b-converted', 'poly4b-converted']
genetics = ['-25-1-99999-200-', '-50-1-99999-200-', '-75-1-99999-200-', '-100-1-99999-200-']
hill = ['-hill-25-1-', '-hill-50-1-', '-hill-75-1-', '-hill-100-1-']
tabu = ['-tabu-25-1-', '-tabu-50-1-', '-tabu-75-1-', '-tabu-100-1-']
time = ['120000']
end = '-info.txt'

def prettytime(string):
	if string == '60000': return '1'
	if string == '120000': return '2'
	if string == '300000': return '5'

def prettyname(string):
	if   '200' in string: return 'genético'
	elif 'hill' in string: return 'hill'
	elif 'tabu' in string: return 'tabu'
	else:
		print('nome não encontrado ' + string)
		exit()
	

def get_row(file):
	with open(file, newline='') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')
		for row in reader:
			return row;

def process_genetics(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[1], row[7], prettyname(partfile) ]))
		
		
def process_tabu(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[1], row[6], prettyname(partfile)]))
		
def process_hill(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[1], row[6], prettyname(partfile)]))

for a in datasets:
	for b in genetics:
		process_genetics(a+b)
	for b in hill:
		process_hill(a+b)
	for b in tabu:
		process_tabu(a+b)