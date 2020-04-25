/**
 * 
 */
package com.iit.aos.pa4;

import java.io.IOException;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Shantanoo
 *
 */
// Responsible for initiating File transfer . This class by itself does little
// Mainly delegates work to helper class FileSender.

public class FileDownloader extends Thread {

	private static final Logger logger = LogManager.getLogger(FileDownloader.class);
	int portno;
	String FileDirectory;
	ServerSocket serverSocket;
	String keysDir, sharedKeysDir;
	int peerID;

	FileDownloader(int peerID, int portno, String FileDirectory, String keysDir, String sharedKeysDir) {
		this.peerID = peerID;
		this.portno = portno;
		this.FileDirectory = FileDirectory;
		this.keysDir = keysDir;
		this.sharedKeysDir = sharedKeysDir;
	}

	public void run() {
		try {
			// Just create the ServerSocket abstraction and pass that to actual FileSender
			serverSocket = new ServerSocket(portno);
		} catch (IOException io) {
			io.printStackTrace();
		}
		new FileSender(peerID, serverSocket, portno, FileDirectory, keysDir, sharedKeysDir).start();
	}
}