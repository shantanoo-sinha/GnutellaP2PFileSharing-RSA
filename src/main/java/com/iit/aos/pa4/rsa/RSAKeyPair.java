/**
 * 
 */
package com.iit.aos.pa4.rsa;

/**
 * @author Shantanoo
 *
 */
public class RSAKeyPair {
	
	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;
	
	/**
	 * @param privateKey
	 * @param publicKey
	 */
	public RSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		super();
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public RSAPrivateKey getPrivate() {
		return privateKey;
	}

	public RSAPublicKey getPublic() {
		return publicKey;
	}
}