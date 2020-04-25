/**
 * 
 */
package com.iit.aos.pa4.rsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.util.Constants;

/**
 * @author Shantanoo
 *
 */
public class ReadKeys {

	private static final Logger logger = LogManager.getLogger(ReadKeys.class);
	
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
}
