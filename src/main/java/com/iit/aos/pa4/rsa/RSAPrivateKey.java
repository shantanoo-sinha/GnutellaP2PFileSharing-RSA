/**
 * 
 */
package com.iit.aos.pa4.rsa;

import java.math.BigInteger;

/**
 * @author Shantanoo
 *
 */
public class RSAPrivateKey {

    private BigInteger modulus;
    private BigInteger privateExponent;

    /**
     * Creates a new RSAPrivateKey.
     *
     * @param modulus the modulus
     * @param privateExponent the private exponent
     */
    public RSAPrivateKey(BigInteger modulus, BigInteger privateExponent) {
        this.modulus = modulus;
        this.privateExponent = privateExponent;
    }

    /**
     * Returns the modulus.
     *
     * @return the modulus
     */
    public BigInteger getModulus() {
        return this.modulus;
    }

    /**
     * Returns the private exponent.
     *
     * @return the private exponent
     */
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
}