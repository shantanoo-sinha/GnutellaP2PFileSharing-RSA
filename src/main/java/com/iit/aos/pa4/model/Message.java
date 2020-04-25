package com.iit.aos.pa4.model;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = -3717453955706477986L;
	private String messageID;
	private int TTL;
	private String fileName;
	private int fromPeerID;
	
	/**
	 * No-args constructor
	 */
	public Message() {
		super();
	}

	/**
	 * @param messageID
	 * @param TTL
	 * @param fileName
	 * @param fromPeerID
	 */
	public Message(String message_ID, int ttl, String file_name, int fromPeerId) {
		super();
		this.messageID = message_ID;
		this.TTL = ttl;
		this.fileName = file_name;
		this.fromPeerID = fromPeerId;
	}

	public String getMessage_ID() {
		return messageID;
	}

	public void setMessage_ID(String message_ID) {
		this.messageID = message_ID;
	}

	public int getTtl() {
		return TTL;
	}

	public int decreaseTtl() {
		return --TTL;
	}
	
	public void setTtl(int ttl) {
		this.TTL = ttl;
	}

	public String getFile_name() {
		return fileName;
	}

	public void setFile_name(String file_name) {
		this.fileName = file_name;
	}

	public int getFromPeerId() {
		return fromPeerID;
	}

	public void setFromPeerId(int fromPeerId) {
		this.fromPeerID = fromPeerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + fromPeerID;
		result = prime * result + ((messageID == null) ? 0 : messageID.hashCode());
		result = prime * result + TTL;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fromPeerID != other.fromPeerID)
			return false;
		if (messageID == null) {
			if (other.messageID != null)
				return false;
		} else if (!messageID.equals(other.messageID))
			return false;
		if (TTL != other.TTL)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [messageID=" + messageID + ", TTL=" + TTL + ", fileName=" + fileName + ", fromPeerID="
				+ fromPeerID + "]";
	}
}
