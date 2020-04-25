package com.iit.aos.pa4.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class RSA {
	private final static BigInteger one = new BigInteger("1");
	private final static SecureRandom random = new SecureRandom();

	private BigInteger privateKey;
	private BigInteger publicKey;
	private BigInteger modulus;

	private int bitLen = 1024;
	
	private RSAPrivateKey rsaPrivateKey;
	private RSAPublicKey rsaPublicKey;

	// generate an N-bit (roughly) public and private key
	public RSA(int N) {
		this.bitLen = N;

		BigInteger p = BigInteger.probablePrime(N / 2, random);
		BigInteger q = BigInteger.probablePrime(N / 2, random);
		BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

		modulus = p.multiply(q);
		publicKey = new BigInteger("65537"); // common value in practice = 2^16 + 1
		privateKey = publicKey.modInverse(phi);
		
		rsaPrivateKey = new RSAPrivateKey(modulus, privateKey);
		rsaPublicKey = new RSAPublicKey(modulus, publicKey);
	}
	
	/** Create an instance that can encrypt using someone else's public key. */
	public RSA(BigInteger newn, BigInteger newe, BigInteger newd) {
		this.modulus = newn;
		this.publicKey = newe;
		this.privateKey = newd;
	}

	public BigInteger encrypt(BigInteger message) {
		return message.modPow(publicKey, modulus);
	}

	public BigInteger decrypt(BigInteger encrypted) {
		return encrypted.modPow(privateKey, modulus);
	}

	public BigInteger encrypt(byte[] message) {
		return new BigInteger(message).modPow(publicKey, modulus);
	}

	public BigInteger decrypt(byte[] encrypted) {
		return new BigInteger(encrypted).modPow(privateKey, modulus);
	}
	
	public byte[] encryptToBytes(byte[] message) {
		BigInteger localBigInteger2 = new BigInteger(1, message).modPow(publicKey, modulus);
		return toByteArray(localBigInteger2, getByteLength());
	}

	public byte[] decryptToBytes(byte[] encrypted) {
		BigInteger localBigInteger2 = new BigInteger(1, encrypted).modPow(privateKey, modulus);
		return toByteArray(localBigInteger2, getByteLength());
	}

	public String toString() {
		String s = "";
		s += "public  = " + publicKey + "\n";
		s += "private = " + privateKey + "\n";
		s += "modulus = " + modulus;
		return s;
	}

	public static void main(String[] args) {
		int N = 1024;
		RSA key = new RSA(N);
		System.out.println(key);

		// create random message, encrypt and decrypt
		BigInteger message = new BigInteger(N - 50, random);

		//// create message by converting string to integer
		// String s = "test";
		// byte[] bytes = s.getBytes();
		// BigInteger message = new BigInteger(bytes);

		BigInteger encrypt = key.encrypt(message);
		BigInteger decrypt = key.decrypt(encrypt);
		System.out.println("message   = " + message);
		System.out.println("encrypted = " + encrypt);
		System.out.println("decrypted = " + decrypt);

		System.out.println("***********************");
		BigInteger encrypt1 = key.encrypt(message.toByteArray());
		BigInteger decrypt1 = key.decrypt(encrypt1.toByteArray());
		System.out.println("message   = " + message);
		System.out.println("encrypted = " + encrypt1);
		System.out.println("decrypted = " + decrypt1);
	}
	
	public int getByteLength() {
		int i = getModulus().bitLength();
		return i + 7 >> 3;
	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}

	public BigInteger getPublicKey() {
		return publicKey;
	}

	public BigInteger getModulus() {
		return modulus;
	}
	
	private static byte[] toByteArray(BigInteger bi, int len) {
        byte[] b = bi.toByteArray();
        int n = b.length;
        if (n == len) {
            return b;
        }
        // BigInteger prefixed a 0x00 byte for 2's complement form, remove it
        if ((n == len + 1) && (b[0] == 0)) {
            byte[] t = new byte[len];
            System.arraycopy(b, 1, t, 0, len);
            return t;
        }
        // must be smaller
        assert (n < len);
        byte[] t = new byte[len];
        System.arraycopy(b, 0, t, (len - n), n);
        return t;
    }
	
	public byte[] encryptData(byte[] plainMessage) {
    	int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
		int blocksCount = (int) Math.ceil((double) plainMessage.length / maxBlockSize);
		byte[][] blocksCollection = new byte[blocksCount][];
		
		byte[] encrypted = null;
        int i = 0;
        int startIndex;
        int endIndex;
        int sizeOfBlocks = 0;
        while (i < blocksCount) {
            startIndex = i * (maxBlockSize);
            endIndex = startIndex + maxBlockSize;
            try {
                byte[] message = Arrays.copyOfRange(plainMessage, startIndex, Math.min(plainMessage.length,endIndex));
                byte[] paddedMessage = pad(message, keySize);
				encrypted = encryptToBytes(paddedMessage);
                sizeOfBlocks += encrypted.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
            blocksCollection[i] = encrypted;
            i++;
        }
        i = 0;
        int n = blocksCollection.length;
        byte[] encryptedMessage = new byte[0];
        while (i < n) {
        	encryptedMessage = concatenateByteArrays(encryptedMessage, blocksCollection[i]);
            i++;
        }
        return encryptedMessage;
    }
	
	public byte[] decryptData(byte[] encryptedMessage) throws Exception {
		int keySize = getByteLength();
		int maxBlockSize = (keySize - 11);
        int blocksCount = encryptedMessage.length / keySize;
        
        int i = 0, startIndex=0, endIndex=0;
        byte[] byteChunkData, decryptedChunk;
        byte[] decryptedMessage = new byte[0];
        
        while (i < blocksCount) {
            startIndex = i * (keySize);
            endIndex = startIndex + keySize;
            byteChunkData = Arrays.copyOfRange(encryptedMessage, startIndex, endIndex);
            decryptedChunk = decryptToBytes(byteChunkData);
            byte[] unpaddedDecryptedChunk = unpad(decryptedChunk, maxBlockSize); 
            decryptedMessage = concatenateByteArrays(decryptedMessage, unpaddedDecryptedChunk);
            i++;
        }
        System.out.println("Decrypted message:" + Arrays.toString(decryptedMessage));
        return decryptedMessage;
	}
	
	public byte[] pad(byte[] data, int paddedSize) {
    	byte[] byteArray = new byte[paddedSize];
    	System.arraycopy(data, 0, byteArray, paddedSize - data.length, data.length);
    	
    	int i = paddedSize - data.length - 2;
		int j = 0;
		byteArray[(j++)] = 0;
		
		byte[] byteArray2 = new byte[64];
		int k = -1;
		while (i-- > 0) {
			int m;
			do {
				if (k < 0) {
					SecureRandom random = new SecureRandom();
                    random.nextBytes(byteArray2);
					k = byteArray2.length - 1;
				}
				m = byteArray2[(k--)] & 0xFF;
			} while (m == 0);
			byteArray[(j++)] = ((byte) m);
		}
    	return byteArray;
    }
    
	public byte[] unpad(byte[] paramArrayOfByte, int maxDataSize) throws Exception {
		int i = 0;
//		int j = 0;
		if (paramArrayOfByte[(i++)] != 0) {
//			j = 1;
			throw new Exception("Bad Padding Exception");
		}
		int k = 0;
		while (i < paramArrayOfByte.length) {
			int m = paramArrayOfByte[(i++)] & 0xFF;
			if ((m == 0) && (k == 0)) {
				k = i;
			}
			if ((i == paramArrayOfByte.length) && (k == 0)) {
//				j = 1;
				throw new Exception("Bad Padding Exception");
			}
		}
		int m = paramArrayOfByte.length - k;
		if (m > maxDataSize) {
//			j = 1;
			throw new Exception("Bad Padding Exception");
		}
		byte[] arrayOfByte1 = new byte[k];
		System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, k);

		byte[] arrayOfByte2 = new byte[m];
		System.arraycopy(paramArrayOfByte, k, arrayOfByte2, 0, m);

		return arrayOfByte2;
	}
    
    public static byte[] concatenateByteArrays(byte[] decryptedMessage, byte[] decryptedChunk) {
		byte[] c = new byte[decryptedMessage.length + decryptedChunk.length];
		System.arraycopy(decryptedMessage, 0, c, 0, decryptedMessage.length);
		System.arraycopy(decryptedChunk, 0, c, decryptedMessage.length, decryptedChunk.length);
		return c;
	}
    
    public RSAKeyPair getRSAKeyPair() {
		return new RSAKeyPair(rsaPrivateKey, rsaPublicKey);
	}
}
