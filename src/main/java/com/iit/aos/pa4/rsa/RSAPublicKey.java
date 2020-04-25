/**
 * 
 */
package com.iit.aos.pa4.rsa;

import java.math.BigInteger;

/**
 * @author Shantanoo
 *
 */
public class RSAPublicKey {
	
	private BigInteger modulus;
    private BigInteger publicExponent;

    /**
     * Creates a new RSAPublicKey.
     *
     * @param modulus the modulus
     * @param publicExponent the public exponent
     */
    public RSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
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
     * Returns the public exponent.
     *
     * @return the public exponent
     */
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
}