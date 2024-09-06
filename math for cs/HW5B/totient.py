from sieve import sieve


def prime_fac(n):
    factors = []
    for p in sieve(n):
        count = 0
        while n % p == 0:
            n //= p
            count += 1
        if count > 0:
            factors.append((p, count))
    return factors


def phi(prime_factors):
    result = 1
    for factor, exponent in prime_factors:
        result *= (factor - 1) * factor ** (exponent - 1)
    return result
