import sys

fn = sys.argv[1]

leftiright = True
rightileft = True

with open(fn, "r") as f:
    leftside = f.readline()
    rightside = f.readline()

file_content = leftside + " " + rightside

variables = set()

for char in leftside + rightside:
    if char.isupper() and char.isalpha():
        variables.add(char)

variable_length = 2 ** len(variables)

for row in range(variable_length):
    dic = {}
    bin_row = str(bin(row)[2:])
    while len(bin_row) < len(variables):
        bin_row = '0' + bin_row
    for var in sorted(variables):
        dic[var] = bool(int(bin_row[0]))
        bin_row = bin_row[1:]
    dic["T"] = True
    dic["F"] = False
    leftTrue = eval(leftside, dic)
    rightTrue = eval(rightside, dic)
    if leftTrue == True and rightTrue == False:
        leftiright = False
    if leftTrue == False and rightTrue == True:
        rightileft = False

if leftiright and rightileft:
    print("EQUIVALENT")
elif rightileft:
    print("RIGHT implies LEFT")
elif leftiright:
    print("LEFT implies RIGHT")
else:
    print("NO IMPLICATION")