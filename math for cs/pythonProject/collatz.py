iterations = 0
while True:
    try:
        x = int(input("Initial value of x: "))
        if x >= 1:
            break
    except TypeError:
        print("Numbers only, please.")
        continue
    except EOFError:
        print("Please input something....")
        continue
while x != 1:
    if x % 2 == 0:
        x = x // 2
        iterations += 1
    else:
        x = 3 * x + 1
        iterations += 1
print(f"Number of iterations to reach 1: {iterations}")