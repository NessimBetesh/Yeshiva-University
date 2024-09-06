def satisfy(args):
    with open(args, "r") as f:
        file_content = f.read().strip()

    variable = set()
    for char in file_content:
        if char.isupper() and char.isalpha():
            variable.add(char)
    #return variable

    return eval(file_content, environment)






    for char in args:
        if char.isalpha and char.isupper():
            variable.add(char)
    #ABBIAMO SET DI LETTERE (KEYS)

    # 1) CREARE IL DIZIONARIO
    dict = {}
    # 2) INSERIRE TUTTI I KEYS NEL DIZIONARIO
    #PER OGNI LETTERA METTO UNA KEY NEL DIZIONARIO
    for letter in variable:
        dict[letter] = []
    # 3) RIEMPIRE LA LISTA
    set_length = len(variable)
    list_length = 2**set_length



    #se char[1] == T and char.next == F
    #fai char[2] == T

    def satisfy(args):
        with open(args, "r") as f:
            file_content = f.read().strip()

        variables = set()
        for char in file_content:
            if char.isupper():
                variables.add(char)

        # We have the set of letters (variables)

        envs = []
        for i in range(2 ** len(variables)):
            env = {}
            for j, var in enumerate(variables):
                env[var] = (i >> j) & 1
            envs.append(env)

        satisfied_count = 0
        for env in envs:
            if eval(file_content, env):
                satisfied_count += 1

        not_satisfied_count = len(envs) - satisfied_count
        print(f"Satisfied: {satisfied_count}; Not Satisfied: {not_satisfied_count}")

        import sys
        if len(sys.argv) != 2:
            print("Usage: python satisfy.py <filename>")
        else:
            satisfy(sys.argv[1])

        # for letter in variable:
        # dict[letter] = []
        # 3) Fill in the list
    # set_length = len(variable)
    # list_length = 2**set_length