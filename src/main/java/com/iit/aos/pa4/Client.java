package com.iit.aos.pa4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.model.Message;
import com.iit.aos.pa4.util.Constants;

/*class LeafNode extends Thread {

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
			//TODO
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
}*/

/*// Responsible for initiating File transfer . This class by itself does little
// Mainly delegates work to helper class FileSender.

class FileDownloader extends Thread {

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
}*/

/*// This class performs the heavy lifting for Actual File send.
class FileSender extends Thread {

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
				//TODO
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
}*/

public class Client {
	private static final Logger logger = LogManager.getLogger(Client.class);
	static String topologyFile;
	static String filesDir;
	static String keysDir;
	private static String sharedKeysDir;

	// Entry point of the application.
	public static void main(String[] args) {
		try {
			// Initializing local variables.
			int ports, portserver, ttl;
			int sequenceNumber = 0;
			String messageID/* , sharedDir */;
			ArrayList<Thread> thread = new ArrayList<Thread>();
			// Maintains an associative array of the Leaf nodes.
			ArrayList<LeafNode> peers = new ArrayList<LeafNode>();
			// Parse input arguments to get the topology and shared path
			int peerID = Integer.parseInt(args[1]);
			filesDir = args[2];
			keysDir = new File(new File(filesDir).getParent()).getParent() + File.separator + Constants.KEYS_FOLDER;
			sharedKeysDir = new File(keysDir) + File.separator + Constants.SHARED_FOLDER;
			Properties prop = new Properties();
			topologyFile = args[0];
			// Peer startup message.
			logger.info("Super-peer " + peerID + " stated with master storage " + filesDir + File.separator
					+ Constants.MASTER_FOLDER + ", Topology: " + topologyFile);
			/*
			 * logger.info("Super-peer " + peer_id + " stated with shared storage " +
			 * filesDir + File.separator + Constants.SHARED_FOLDER + " Topology: " +
			 * topologyFile);
			 */
			InputStream is = new FileInputStream(topologyFile);
			// Reading from topology file using Java Properties.
			prop.load(is);
			ports = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + peerID + Constants.SERVER_PORT));
			// Initiate the file downloader
			FileDownloader sd = new FileDownloader(peerID, ports, filesDir, keysDir, sharedKeysDir);
			sd.start();
			portserver = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + peerID + Constants.PORT));
			Server cs = new Server(portserver, filesDir, peerID, keysDir, sharedKeysDir);
			cs.start();
			while (true) {
				logger.info("Enter the file name to download (with extension)");
				String fileToDownload = new Scanner(System.in).nextLine();
				// ++sequenceNumber;
				messageID = peerID + "." + (++sequenceNumber);
				String[] neighbours = prop.getProperty(Constants.CLIENT_PREFIX + peerID + Constants.NEXT).split(",");
				ttl = neighbours.length;
				// File name to search is been recieved from user . Now search through every
				// neighbours and check
				// Who got the file in it's Leaf.
				for (int i = 0; i < neighbours.length; i++) {
					int connectingport = Integer.parseInt(prop.getProperty("Client" + neighbours[i] + Constants.PORT));
					int neighbouringpeer = Integer.parseInt(neighbours[i]);
					LeafNode cp = new LeafNode(connectingport, neighbouringpeer, fileToDownload, messageID, peerID, ttl,
							keysDir, sharedKeysDir);
					Thread t = new Thread(cp);
					t.start();
					// Every leafnode is started as a separate Thread , so that they dont block each
					// other
					thread.add(t);
					peers.add(cp);
				}
				for (int i = 0; i < thread.size(); i++) {
					try {
						((Thread) thread.get(i)).join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int[] peersWithFiles;

				logger.info("Leafnodes containing the file are: ");
				int peerFromDownload = 0;
				for (int i = 0; i < peers.size(); i++) {
					peersWithFiles = ((LeafNode) peers.get(i)).getarray();
					for (int j = 0; j < peersWithFiles.length; j++) {
						if (peersWithFiles[j] == 0)
							break;
						logger.info("Client " + peersWithFiles[j]);
						// Randomly selects a leaf node from available leaf nodes
						// This ios kind of static load balancer.
						peerFromDownload = peersWithFiles[j];
					}
				}
				if (peerFromDownload > 0) {
					logger.info("Selecting leafnode: Client " + peerFromDownload + " to download the requested file "
							+ fileToDownload);
					int portToDownload = Integer.parseInt(
							prop.getProperty(Constants.CLIENT_PREFIX + peerFromDownload + Constants.SERVER_PORT));
					// File found . Initiate Download .
					downloadFile(peerID, peerFromDownload, portToDownload, fileToDownload, filesDir, keysDir,
							sharedKeysDir);
					logger.info("File: " + fileToDownload + " downloaded from Leafnode: Client " + peerFromDownload
							+ " to Leafnode: Client " + peerID);
				} else {
					logger.info("File not found on any nodes.");
				}
				peers.clear();
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	// Responsible for initiating the file Transfer and saving the transferred file
	// to its private storage.
	public static void downloadFile(int peerID, int peerFromDownload, int portToDownload, String filename,
			String filesDir, String keysDir, String sharedKeysDir) {
		try {
			Socket clientsocket = new Socket(Constants.LOCALHOST, portToDownload);
			ObjectOutputStream ooos = new ObjectOutputStream(clientsocket.getOutputStream());
			ooos.flush();

			ooos.writeObject(filename);

			String outputFile = filesDir + "/" + filename;
			byte[] mybytearray = new byte[1024];
			// Boilerplate file transfer code using socket and Object output Stream
			// http://www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm
			InputStream is = clientsocket.getInputStream();
			FileOutputStream fos = new FileOutputStream(outputFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = is.read(mybytearray, 0, mybytearray.length);
			bos.write(mybytearray, 0, bytesRead);
			bos.close();
			clientsocket.close();
			logger.info(filename + " file is transferred to your private storage: " + filesDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
