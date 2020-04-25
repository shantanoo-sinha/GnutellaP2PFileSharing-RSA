/**
 * 
 */
package com.iit.aos.pa4;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.model.Message;
import com.iit.aos.pa4.util.Constants;

/**
 * @author Shantanoo
 *
 */
public class LeafNode extends Thread {

	private static final Logger logger = LogManager.getLogger(LeafNode.class);

	int leafNodePort, connectedSuperpeer, frompeerId, timeToLive;
	String filetodownload, msgid;
	Socket socket = null;
	int[] peersArray;
	Message MF = new Message();
	String keysDir, sharedKeysDir;

	public LeafNode(int leafNodePort, int connectedSuperpeer, String filetodownload, String msgid, int frompeerId,
			int timeToLive, String keysDir, String sharedKeysDir) {
		// Initializing LeafNode local variables.
		this.leafNodePort = leafNodePort;
		this.connectedSuperpeer = connectedSuperpeer;
		this.filetodownload = filetodownload;
		this.msgid = msgid;
		this.frompeerId = frompeerId;
		this.timeToLive = timeToLive;
		this.keysDir = keysDir;
		this.sharedKeysDir = sharedKeysDir;
	}

	public void run() {
		try {
			// TODO
			// Initiate the socket connection to communicate with Superpeer that
			// File exixts or Not.
			socket = new Socket(Constants.LOCALHOST, leafNodePort);
			OutputStream os = socket.getOutputStream();
			// Object output stream wraps around socket and transfers file
			ObjectOutputStream oos = new ObjectOutputStream(os);
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			// Initializing the message with to - from and id trail
			MF.setFile_name(filetodownload);
			MF.setMessage_ID(msgid);
			MF.setFromPeerId(frompeerId);
			MF.setTtl(timeToLive);
			// Writing the message
			oos.writeObject(MF);

			peersArray = (int[]) ois.readObject();
		} catch (IOException io) {
			io.printStackTrace();
		} catch (ClassNotFoundException cp) {
			cp.printStackTrace();
		}
	}

	public int[] getarray() {
		return peersArray;
	}
}