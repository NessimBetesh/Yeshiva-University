import sys

fn = sys.argv[1]

s = 0
us = 0
with open(fn, "r") as f:
    file_content = f.read()

variable = set()

for char in file_content:
    if char.isupper() and char.isalpha():
        variable.add(char)

variable_length = 2 ** len(variable)
ls = []

for row in range(variable_length):
    dic = {}
    bin_row = str(bin(row)[2:])
    while len(bin_row) < len(variable):
        bin_row = '0' + bin_row
    for var in sorted(variable):
        dic.update({var: bool(int(bin_row[0]))})
        bin_row = bin_row[1:]
    if eval(file_content, dic) == True:
        s += 1
    else:
        us += 1

print("Satisfied:", str(s) + ";", "Not Satisfied:", us)