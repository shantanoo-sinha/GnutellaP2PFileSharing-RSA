package com.iit.aos.pa4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// This class is performs the activity of the super peer.
// Taht is keeping track of Leafnodes. Propagating messages , maintaining TTl etc.
public class Server extends Thread {
	
	private static final Logger logger = LogManager.getLogger(Server.class);
    // Initialize local variables.
    String fileDir;
    int portNumber;
    ServerSocket serverSocket = null;
    Socket socket = null;
    int peer_id;
    static String topologyFile;
    static ArrayList<String> messages;
    String keysDir, sharedKeysDir;
    
    //Parameterized constructor.
    public Server(int peerID, int port, String fileDir, String keysDir, String sharedKeysDir) {
        this.portNumber = port;
        this.fileDir = fileDir;
        this.peer_id = peerID;
        Server.messages = new ArrayList<String>();
        this.keysDir = keysDir;
        this.sharedKeysDir = sharedKeysDir;
    }

    // Thread body
    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
                logger.info("Superpeer: " + peer_id + ", started at " + socket.getRemoteSocketAddress());
                // Communicates with Other Superpeer. Though this Thread is just a wrapper ,
                // Actual Heavy lifting is performed by ServerListener class
                new ServerListener(socket, fileDir, peer_id, messages, keysDir, sharedKeysDir).start();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}
