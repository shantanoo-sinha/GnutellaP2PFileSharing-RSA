/**
 * 
 */
package com.iit.aos.pa4;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Shantanoo
 *
 */
// This class performs the heavy lifting for Actual File send.
public class FileSender extends Thread {

	private static final Logger logger = LogManager.getLogger(FileSender.class);
	int portno;
	String sharedDirectory, fileName;
	String keysDir, sharedKeysDir;
	ServerSocket socket;
	int peerID;

	// Parameterized constructor
	FileSender(int peerID, ServerSocket socket, int portno, String FileDir, String keysDir, String sharedKeysDir) {
		this.peerID = peerID;
		this.socket = socket;
		this.portno = portno;
		this.sharedDirectory = FileDir;
		this.keysDir = keysDir;
		this.sharedKeysDir = sharedKeysDir;
	}

	public void run() {
		try {
			// This is an infinite for loop keeps running at the background , that always
			// listens for
			// incoming query messages . And responds.
			while (true) {
				logger.info("\n\n (N.B. In the background parallely Waiting for new File download Request) \n\n");
				// Following 4 lines are boilerplate socket and Objectstream code.
				// http://www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm
				Socket sock = socket.accept();
				// TODO
				InputStream is = sock.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				fileName = (String) ois.readObject();
				File myFile = new File(sharedDirectory + "/" + fileName);
				byte[] mybytearray = new byte[(int) myFile.length()];
				// Using BufferedInputStream to buffer large file content.
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
				bis.read(mybytearray, 0, mybytearray.length);
				OutputStream os = sock.getOutputStream();
				os.write(mybytearray, 0, mybytearray.length);
				// Done writing . Now close the socket and flush stream.
				os.flush();
				sock.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}