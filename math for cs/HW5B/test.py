from totient import prime_fac, phi

# Test prime_fac
print("Prime factorization of 24:", prime_fac(24))  # Expected output: [(2, 3), (3, 1)]

# Test phi
prime_factors = [(2, 3), (3, 1)]
print("Euler's totient function for prime factors [(2, 3), (3, 1)]:", phi(prime_factors))  # Expected output: 8


def prime_fac(n):
    primes = sieve(n)
    factors = []
    for prime in primes:
        if prime * prime > n:
            break
        if n % prime == 0:
            power = 0
            while n % prime == 0:
                n //= prime
                power += 1
            factors.append((prime, power))
    if n > 1:
        factors.append((n, 1))
    return factors


def testprimefact():
    assert prime_fac(1) == [], "Test with 1 fail"
    assert prime_fac(2) == [(2, 1)], "Test with 2 fail"
    assert prime_fac(3) == [(3, 1)], "Test with 3 fail"
    assert prime_fac(4) == [(2, 2)], "Test with 4 fail"
    assert prime_fac(6) == [(2, 1), (3, 1)], "Test with 6 fail"
    assert prime_fac(24) == [(2, 3), (3, 1)], "Test with 2 fail"
    assert prime_fac(100) == [(2, 2), (5, 2)], "Test with 100 fail"
    print("all tests passed")


def testphi():
    assert phi([]) == 1, "Test with empty fail"
    assert phi([(2, 1)]) == 1, "Test 2, 1 fail"
    assert phi([(3, 1)]) == 2, "Test 3"
    assert phi([(2, 2)]) == 2, "Test 2, 2 fail"
    assert phi([(2, 1), (3, 1)]) == 2, "4 fail"
    assert phi([(2, 3), (3, 1)]) == 8, "5 fail"
    assert phi([(2, 2), (5, 2)]) == 40, "test fail"
    print("all test pass")


if __name__ == "__main__":
    testprimefact()
    testphi()