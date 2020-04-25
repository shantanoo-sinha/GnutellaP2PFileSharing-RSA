package com.iit.aos.pa4;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.rsa.RSA;
import com.iit.aos.pa4.rsa.RSAKeyPair;
import com.iit.aos.pa4.rsa.RSAKeysHelper;
import com.iit.aos.pa4.util.Constants;


public class Client {
	private static final Logger logger = LogManager.getLogger(Client.class);
	int peerID;
	static String topologyFile;
	String filesDir;
	String keysDir;
	private String sharedKeysDir;
	private RSAKeyPair rsaKeyPair;

	public Client() {
		super();
	}

	/**
	 * @param peerID
	 * @param topologyFile
	 * @param filesDir
	 * @param keysDir
	 */
	public Client(int peerID, String topologyFile, String filesDir, String keysDir) {
		super();
		this.peerID = peerID;
		Client.topologyFile = topologyFile;
		this.filesDir = filesDir;
		this.keysDir = keysDir;
	}

	public String getKeysDir() {
		return keysDir;
	}

	public String getSharedKeysDir() {
		return sharedKeysDir;
	}

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
			String topologyFile = args[0];
			int peerID = Integer.parseInt(args[1]);
			String filesDir = args[2];

			String keysDir = new File(new File(filesDir).getParent()).getParent() + File.separator + Constants.KEYS_FOLDER;
			logger.debug("keysDir:" + keysDir);
			String sharedKeysDir = new File(keysDir) + File.separator + Constants.SHARED_FOLDER;
			logger.debug("sharedKeysDir:" + sharedKeysDir);
			
			Client client = new Client(peerID, topologyFile, filesDir, keysDir);
			// Peer startup message.
			logger.info("Super-peer " + peerID + " stated with master storage " + filesDir + File.separator
					+ Constants.MASTER_FOLDER + ", Topology: " + topologyFile);

			Properties prop = new Properties();
			InputStream is = new FileInputStream(topologyFile);
			// Reading from topology file using Java Properties.
			prop.load(is);
			ports = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + client.peerID + Constants.SERVER_PORT));

			// Generate or load RSA keys
			client.generateKeys();
			
			// Initiate the file downloader
			FileDownloader fileDownloader = new FileDownloader(peerID, ports, filesDir, keysDir, sharedKeysDir);
			fileDownloader.start();

			portserver = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + peerID + Constants.PORT));
			Server server = new Server(peerID, portserver, filesDir, keysDir, sharedKeysDir);
			server.start();
			
			Scanner scanner = new Scanner(System.in);
			logger.info("\nEnter 'exit' to exit the application"
					+ "\nEnter 'print' to print the files with version and state information"
					+ "\nEnter 'share' to share node's public key with others"
					+ "\nEnter the name of file (with extension) you want to download:\n");
			while (true) {
				logger.info("Enter the file name to download (with extension)");
				String input = scanner.nextLine();
				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("e")) {
					logger.info("\nClient exiting...\n");
					scanner.close();
					System.exit(0);
				} else if (input.equalsIgnoreCase("share")) {
//					client.sharePublicKey();
				} /*else if (input.equalsIgnoreCase("print")) {
					logger.info("[" + id + "] " + "Printing file information:");
					logger.info("[" + id + "] " + "Master Files:");
					client.masterFiles.forEach((key, val) -> {
						logger.info("[" + id + "] " + val.getFileName() + ", Version: " + val.getVersion());
					});
					logger.info("[" + id + "] " + "Shared Files:");
					client.sharedFiles.forEach((key, val) -> {
						logger.info("[" + id + "] " + val.getFileName() + ", Version: " + val.getVersion() + ", Origin: " + val.getOriginServerID()
						+ ", Origin Super Peer: " + val.getOriginServerSuperPeerID() + "State: " + val.getState());
					});
				}*/ else if (input != null && (input.trim().contains(Constants.SPACE) || !input.trim().contains(Constants.DOT))) {
					logger.info("Incorrect command");
					logger.info("USAGE: <file name with extension>");
					logger.info("EXAMPLE: <123.txt>");
					logger.info("EXAMPLE: <e or exit>");
					logger.info("EXAMPLE: <print>");
					logger.info("EXAMPLE: <share>");
				} else {
					sequenceNumber = searchAndDownloadFile(++sequenceNumber, thread, peers, peerID, filesDir, keysDir, sharedKeysDir, prop, input);
				}
				logger.info("\nEnter 'exit' to exit the application"
						+ "\nEnter 'print' to print the files with version and state information"
						+ "\nEnter 'share' to share node's public key with others"
						+ "\nEnter the name of file (with extension) you want to download:\n");
			}
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	private static int searchAndDownloadFile(int sequenceNumber, ArrayList<Thread> thread, ArrayList<LeafNode> peers, int peerID,
			String filesDir, String keysDir, String sharedKeysDir, Properties prop, String input) {
		long startTime = System.currentTimeMillis();
		// ++sequenceNumber;
		String messageID = peerID + "." + (sequenceNumber);
		String[] neighbours = prop.getProperty(Constants.CLIENT_PREFIX + peerID + Constants.NEXT).split(",");
		int ttl = neighbours.length;
		// File name to search is been recieved from user . Now search through every
		// neighbours and check
		// Who got the file in it's Leaf.
		for (int i = 0; i < neighbours.length; i++) {
			int connectingport = Integer.parseInt(prop.getProperty("Client" + neighbours[i] + Constants.PORT));
			int neighbouringpeer = Integer.parseInt(neighbours[i]);
			LeafNode cp = new LeafNode(connectingport, neighbouringpeer, input, messageID, peerID, ttl,
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
			logger.info("Selecting leafnode: Client " + peerFromDownload + " to download the requested file " + input);
			int portToDownload = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + peerFromDownload + Constants.SERVER_PORT));
			// File found . Initiate Download .
			downloadFile(peerID, peerFromDownload, portToDownload, input, filesDir, keysDir, sharedKeysDir);
			logger.info("File: " + input + " downloaded from Leafnode: Client " + peerFromDownload + " to Leafnode" + ": Client " + peerID);
		} else {
			logger.info("File not found on any nodes.");
		}
		peers.clear();
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		double elapsedTimeInMSecond = (double) elapsedTime / 1000.000;
		logger.info("Process completed in " + TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS) + "(" + elapsedTimeInMSecond + ") second");
		return sequenceNumber;
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

	private void generateKeys() {
		if (new File(this.keysDir).exists()) {
			rsaKeyPair = new RSAKeyPair(RSAKeysHelper.readPrivateKey(this.peerID, this.keysDir), RSAKeysHelper.readPublicKey(this.peerID, this.keysDir));
			return;
		}
		logger.info("Generating RSA keys");
		RSA rsa = new RSA(1024);
		rsaKeyPair = rsa.getRSAKeyPair();
		RSAKeysHelper.writeKeys(this.peerID, keysDir, rsaKeyPair);
	}
	
	/*public void sharePublicKey() {
		byte[] publicKey = readPublicKey(this.id);
		prop.entrySet().stream().filter(entry -> entry.toString().contains(Constants.PORT)).forEach(entry -> {
//			logger.info("Client " + entry.getValue());
			String nodeAddress = Constants.RMI_LOCALHOST + entry.getValue() + Constants.PEER_SERVER;
//			logger.info("Node Address " + nodeAddress);
			if(this.nodeAddress.equalsIgnoreCase(nodeAddress))
				return;
			
			Registry registry;
			try {
//				byte[] publicKey = readPublicKey(this.id);
				registry = LocateRegistry.getRegistry(1099);
//				registry = LocateRegistry.getRegistry(Constants.LOCALHOST, 1099, new XorClientSocketFactory(pattern));
				IRemote serverStub = (IRemote) registry.lookup(nodeAddress);
				if(serverStub.sharePublicKey(publicKey, this.id))
					logger.info("Public Key shared with " + nodeAddress);
				else
					logger.error("Failed to share public key with " + nodeAddress);
			} catch (Exception e) {
				logger.error("[" + this.id + "] " + "Server exception: Unable to register shared files to SuperPeer.");
				e.printStackTrace();
			}			
		});
	}*/
}
