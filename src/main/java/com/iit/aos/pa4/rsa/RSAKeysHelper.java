/**
 * 
 */
package com.iit.aos.pa4.rsa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.util.Constants;

/**
 * @author Shantanoo
 *
 */
public class RSAKeysHelper {

	private static final Logger logger = LogManager.getLogger(RSAKeysHelper.class);
	
	public static RSAPrivateKey readPrivateKey(int id, String keysDir) {
		logger.info("Reading private key.");
		RSAPrivateKey rsaPrivateKey = null;
		String privateKeyFile = keysDir + File.separator + Constants.CLIENT_PREFIX + id + Constants.PRIVATE_KEY_SUFFIX;
		
		try {
			FileReader privateFileReader = new FileReader(Paths.get(privateKeyFile).toFile());
            BufferedReader bufferedReader = new BufferedReader(privateFileReader);

            String line = null, N = null, D = null;
            if((line = bufferedReader.readLine()) != null) {
            	N = line;
            	logger.debug("N=> " + line);
            }
            if((line = bufferedReader.readLine()) != null) {
            	D = line;
            	logger.debug("D=> " + line);
            }
            bufferedReader.close();
	        
            String nString = new String(Base64.getDecoder().decode(N)).toString();
			String dString = new String(Base64.getDecoder().decode(D)).toString();
			
			BigInteger n = new BigInteger(nString);
			BigInteger d = new BigInteger(dString);
			
			rsaPrivateKey = new RSAPrivateKey(n, d);
			
		} catch (IOException e1) {
			logger.error("Client exception: Unable to read RSA private key.");
			e1.printStackTrace();
		}
		return rsaPrivateKey;
	}
	
	public static void writeKeys(int id, String keysDir, RSAKeyPair rsaKeyPair) {

		logger.info("Writing RSA keys");

		String privateKeyFile = keysDir + File.separator + Constants.CLIENT_PREFIX + id + Constants.PRIVATE_KEY_SUFFIX;
		String publicKeyFile = keysDir + File.separator + Constants.CLIENT_PREFIX + id + Constants.PUBLIC_KEY_SUFFIX;

		if (!new File(keysDir).exists())
			new File(keysDir).mkdirs();

		FileOutputStream privateOut = null, publicOut = null;
		FileWriter privateFileWriter = null, publicFileWriter = null;
		try {
			// logger.info("N: " + rsa.getN());
			// logger.info("e: " + rsa.getE());
			// logger.info("d: " + rsa.getD());

			privateFileWriter = new FileWriter(privateKeyFile);
			BufferedWriter bufferedWriter = new BufferedWriter(privateFileWriter);
			bufferedWriter.write(Base64.getEncoder().encodeToString(("" + rsaKeyPair.getPrivate().getModulus()).getBytes()));
			bufferedWriter.newLine();
			bufferedWriter.write(Base64.getEncoder().encodeToString(("" + rsaKeyPair.getPrivate().getPrivateExponent()).getBytes()));
			// Always close files.
			bufferedWriter.close();

			publicFileWriter = new FileWriter(publicKeyFile);
			bufferedWriter = new BufferedWriter(publicFileWriter);
			bufferedWriter.write(Base64.getEncoder().encodeToString(("" + rsaKeyPair.getPublic().getModulus()).getBytes()));
			bufferedWriter.newLine();
			bufferedWriter.write(Base64.getEncoder().encodeToString(("" + rsaKeyPair.getPublic().getPublicExponent()).getBytes()));
			// Always close files.
			bufferedWriter.close();
			logger.info("RSA keys generated");
		} catch (IOException e) {
			logger.error("Exception: Unable to write RSA keys.");
			e.printStackTrace();
		} finally {
			try {
				if (privateOut != null)
					privateOut.close();
				if (publicOut != null)
					publicOut.close();
			} catch (IOException e) {
				logger.error("Exception: Unable to close FileOutputStream.");
				e.printStackTrace();
			}
		}
	}
	
	public static RSAPublicKey readPublicKey(int id, String keysDir) {
		logger.info("Reading public key.");
		RSAPublicKey rsaPublicKey = null;
		String publicKeyFile = keysDir + File.separator + Constants.CLIENT_PREFIX + id + Constants.PUBLIC_KEY_SUFFIX;
		
		try {
            FileReader publicFileReader = new FileReader(Paths.get(publicKeyFile).toFile());
            BufferedReader bufferedReader = new BufferedReader(publicFileReader);
            
            String line = null, N = null, E = null;
            if((line = bufferedReader.readLine()) != null) {
            	N = line;
            	logger.debug("N=> " + line);
            }
            if((line = bufferedReader.readLine()) != null) {
            	E = line;
            	logger.debug("E=> " + line);
            }
            bufferedReader.close();
	        
            String nString = new String(Base64.getDecoder().decode(N)).toString();
			String eString = new String(Base64.getDecoder().decode(E)).toString();
			
			BigInteger n = new BigInteger(nString);
			BigInteger e = new BigInteger(eString);

			rsaPublicKey = new RSAPublicKey(n, e);
			
		} catch (IOException e1) {
			logger.error("Client exception: Unable to read RSA public key.");
			e1.printStackTrace();
		}
		return rsaPublicKey;
	}
	
	public static byte[] readPublicKeyFile(int id, String keysDir) {
		
		String publicKeyFile = keysDir + File.separator + Constants.CLIENT_PREFIX + id + Constants.PUBLIC_KEY_SUFFIX;
		byte[] publicKey = null;
		try {
			publicKey = Files.readAllBytes(Paths.get(publicKeyFile));
		} catch (IOException e1) {
			logger.error("Exception: Unable to share RSA public key.");
			e1.printStackTrace();
			publicKey = "Unable to share RSA public key. Please try again".getBytes();
		}
		return publicKey;
	}
}
