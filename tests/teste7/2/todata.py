import csv

datasets = ['fu-converted', 'poly1a-converted', 'poly2b-converted', 'poly3b-converted', 'poly3b-converted']
genetics = ['-100-1-999999-50-', '-100-1-999999-100-', '-100-1-999999-200-']
hill = ['-hill-100-1-']
tabu = ['-tabu-100-1-']
time = ['60000', '120000', '300000']
end = '-info.txt'

def prettytime(string):
	if string == '60000': return '1'
	if string == '120000': return '2'
	if string == '300000': return '5'

def prettyname(string):
	if '-100-1-999999-50-' in string: return 'G_p50'
	if '-100-1-999999-100-' in string: return 'G_p100'
	if '-100-1-999999-200-' in string: return 'G_p200'
	if 'hill' in string: return 'hill'
	if 'tabu' in string: return 'tabu'	

def get_row(file):
	with open(file, newline='') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')
		for row in reader:
			return row;

def process_genetics(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[7], prettytime(x), prettyname(partfile)]))
		
		
def process_tabu(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[6], prettytime(x), prettyname(partfile)]))
		
def process_hill(partfile):
	for x in time:
		row = get_row(partfile+x+end)
		print(','.join([row[0], row[6], prettytime(x), prettyname(partfile)]))

for a in datasets:
	for b in genetics:
		process_genetics(a+b)
	for b in hill:
		process_hill(a+b)
	for b in tabu:
		process_tabu(a+b)