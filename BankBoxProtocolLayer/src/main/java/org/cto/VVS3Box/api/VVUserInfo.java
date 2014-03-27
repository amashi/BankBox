package org.cto.VVS3Box.api;

public class VVUserInfo {

	private final String username;
	private final String fullName;
	private final long spaceUsed;
	private final long spaceTotal;
	private final int filesCount;

	public String getUsername() {
		return username;
	}

	public String getFullname() {
		return fullName;
	}

	public long getSpaceUsed() {
		return spaceUsed;
	}

	public long getSpaceTotal() {
		return spaceTotal;
	}
	
	public int getFilesCount() {
		return filesCount;
	}

	public VVUserInfo(String username, String fullName, long spaceTotal, long spaceUsed, int filesCount) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.spaceTotal = spaceTotal;
		this.spaceUsed = spaceUsed;
		this.filesCount = filesCount;
	}
}
