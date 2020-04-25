package com.iit.aos.pa4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iit.aos.pa4.model.Message;
import com.iit.aos.pa4.util.Constants;

// This class is performs the activity of the super peer.
// Taht is keeping track of Leafnodes. Propagating messages , maintaining TTl etc.
public class Server extends Thread {
	
	private static final Logger logger = LogManager.getLogger(Server.class);
    // Initialize local variables.
    String FileDir;
    int port_no;
    ServerSocket serverSocket = null;
    Socket socket = null;
    int peer_id;
    static ArrayList<String> msg;
    String keysDir, sharedKeysDir;
    
    //Parameterized constructor.
    Server(int port, String SharedDir, int peer_id, String keysDir, String sharedKeysDir) {
        port_no = port;
        FileDir = SharedDir;
        this.peer_id = peer_id;
        msg = new ArrayList<String>();
        this.keysDir = keysDir;
        this.sharedKeysDir = sharedKeysDir;
    }

    // Thread body
    public void run() {
        try {
            serverSocket = new ServerSocket(port_no);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
                logger.info("Superpeer: " + peer_id + ", started at " + socket.getRemoteSocketAddress());
                // Communicates with Other Superpeer. Though this Thread is just a wrapper ,
                // Actual Heavy lifting is performed by ServerListener class
                new ServerListener(socket, FileDir, peer_id, msg, keysDir, sharedKeysDir).start();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}


/*class CommunicateWithPeer extends Thread {
	
	private static final Logger logger = LogManager.getLogger(CommunicateWithPeer.class);
    protected Socket socket;
    String FileDirectory;
    String fname;
    int peer_id;
    ArrayList<String> peermsg;
    ArrayList<Thread> thread = new ArrayList<Thread>();
    ArrayList<LeafNode> peerswithfiles = new ArrayList<LeafNode>();
    int[] peersArray_list = new int[20];
    int[] a = new int[20];
    int countofpeers = 0;
    Message message = new Message();
    String keysDir, sharedKeysDir;

    CommunicateWithPeer(Socket socket, String FileDirectory, int peer_id, ArrayList<String> peermsg, String keysDir, String sharedKeysDir) {
        this.socket = socket;
        this.FileDirectory = FileDirectory;
        this.peer_id = peer_id;
        this.peermsg = peermsg;
        this.keysDir = keysDir;
        this.sharedKeysDir = sharedKeysDir;
    }

    public void run() {
        try {
        	//TODO
            // Socket boilerplate , using Objectoutput stream.
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            boolean peerduplicate;
            // Search query recieved . We are using socket to commicate between super peers for search queries
            // as well. This lines are not to be confused for file transfer socket.
            message = (Message) ois.readObject();

            logger.info("Received query from " + message.getFromPeerId());
            //Keeping the associative array for the message.
            //If in the array we already have same message id , then we dont again forward tonthat supeer peer.
            // This saves the system from message flooding.
            peerduplicate = this.peermsg.contains(message.getMessage_ID());
            if (peerduplicate == false) {
                this.peermsg.add(message.getMessage_ID());
            } else {
                logger.info("Recieved Same query before.");
            }

            fname = message.getFile_name();
            // If same message is not relayed to the peer.
            if (!peerduplicate) {
                File newfind;
                File directoryObj = new File(FileDirectory);
                String[] filesList = directoryObj.list();
                for (int j = 0; j < filesList.length; j++) {
                    newfind = new File(filesList[j]);
                    if (newfind.getName().equals(fname)) {
                        // File actually found at super peer . Success.
                        // Queryhit.
                        logger.info("Found file . queryhit: " + fname);
                        peersArray_list[countofpeers++] = peer_id;
                        break;
                    }
                }
                logger.info("Msg from Superpeer: Search in Leafnode completed");
                Properties prop = new Properties();
                Client M = new Client();
                String fileName = M.topologyFile;
                is = new FileInputStream(fileName);
                prop.load(is);
                String temp = prop.getProperty(Constants.CLIENT_PREFIX + peer_id + Constants.NEXT);
                if (temp != null && message.getTtl() > 0) {
                    String[] neighbours = temp.split(Constants.SPLIT_REGEX);

                    for (int i = 0; i < neighbours.length; i++) {
                        if (message.getFromPeerId() == Integer.parseInt(neighbours[i])) {
                            continue;
                        }
                        int connectingport = Integer.parseInt(prop.getProperty(Constants.CLIENT_PREFIX + neighbours[i] + Constants.PORT));
                        int neighbouringpeer = Integer.parseInt(neighbours[i]);

                        logger.info("Query forwarded to " + neighbouringpeer);
                        LeafNode cp = new LeafNode(connectingport, neighbouringpeer, fname, message.getMessage_ID(), peer_id, message.decreaseTtl(), keysDir, sharedKeysDir);
                        Thread t = new Thread(cp);
                        t.start();
                        thread.add(t);
                        peerswithfiles.add(cp);

                    }
                }
                for (int i = 0; i < thread.size(); i++) {
                    ((Thread) thread.get(i)).join();
                }
                for (int i = 0; i < peerswithfiles.size(); i++) {
                    a = ((LeafNode) peerswithfiles.get(i)).getarray();
                    for (int j = 0; j < a.length; j++) {
                        if (a[j] == 0)
                            break;
                        peersArray_list[countofpeers++] = a[j];
                    }
                }
            }
            oos.writeObject(peersArray_list);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}*/