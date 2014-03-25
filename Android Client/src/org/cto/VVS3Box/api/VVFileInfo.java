package org.cto.VVS3Box.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class VVFileInfo implements Comparable<VVFileInfo> {

	public static final VVFileInfo DIRECTORY_UP = new VVFileInfo("..", 0, null,

	null, null);

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	private final String hash;
	private final String pathname;
	private final long size;
	private final Date lastModified;
	private String fileName;
	private String folderName;
	private AndroidClientAPI api;

	public VVFileInfo(String pathname, long size, Date lastModified,
			String hash, AndroidClientAPI api) {
		super();
		this.hash = hash;
		this.pathname = pathname;
		this.size = size;
		this.lastModified = lastModified;
		this.fileName = "";

		int index = pathname.length() - 1;
		// root folder:
		if (index == 0) {
			this.folderName = "";
		}

		// it's a folder
		if (isFolder())
			index--;
		index = pathname.lastIndexOf("\\", index);
		this.folderName = pathname.substring(0, index + 1);
		this.fileName = pathname.substring(index + 1);

		this.api = api;
	}

	/**
	 * Gets the hex encoded 128-bit MD5 hash of this object's contents as
	 * computed by Amazon S3.
	 * 
	 * @return The hex encoded 128-bit MD5 hash of this object's contents as
	 *         computed by Amazon S3.
	 * 
	 * @see S3ObjectSummary#setETag(String)
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Returns the full path of this object
	 * 
	 * @return
	 */
	public String getFullPath() {
		return pathname;
	}

	public boolean isFolder() {
		return pathname.endsWith("\\");
	}

	/**
	 * Returns the subfolders within this folder. If the object is not a folder,
	 * an empty list is returned
	 * 
	 * @return
	 */
	public List<VVFileInfo> getSubfolders() {
		List<VVFileInfo> result = new ArrayList<VVFileInfo>();
		if (isFolder())
			try {
				for (VVFileInfo fi : api.getChildrenOf(pathname))
					if (fi.isFolder())
						result.add(fi);
			} catch (VVEXcption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return result;
	}

	/**
	 * returns the files within this folder
	 * 
	 * @return
	 */
	public List<VVFileInfo> getFiles() {
		List<VVFileInfo> result = new ArrayList<VVFileInfo>();
		if (isFolder())
			try {
				for (VVFileInfo fi : api.getChildrenOf(pathname))
					if (!fi.isFolder())
						result.add(fi);
			} catch (VVEXcption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return result;
	}

	/**
	 * Gets the size of this object in bytes.
	 * 
	 * @return The size of this object in bytes.
	 * 
	 * @see 3ObjectSummary#setSize(long)
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Gets the date when, according to Amazon S3, this object was last
	 * modified.
	 * 
	 * @return The date when, according to Amazon S3, this object was last
	 *         modified.
	 * 
	 * @see S3ObjectSummary#setLastModified(Date)
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * returns the full path to the folder containing this file
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	public String getFolderName() {
		return folderName;
	}

	@Override
	public String toString() {

		return DATE_FORMAT.format(lastModified) + "\t"
				+ VVFileInfo.humanReadableByteCount(size) + "\t: " + pathname
				+ ", eTag=" + hash;
	}

	@Override
	public int compareTo(VVFileInfo o) {
		if (this.equals(DIRECTORY_UP))
			return Integer.MIN_VALUE;
		return this.pathname.compareToIgnoreCase(o.pathname);
	}

	public boolean isRoot() {
		return pathname.equals("\\");
	}

	/**
	 * Converts a long number to a human readable string representing that
	 * number in bytes/MB/GB
	 * 
	 * @param bytes
	 * @return
	 */
	public static String humanReadableByteCount(long bytes) {
		int unit = 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		char pre = "KMGTPE".charAt(exp - 1);
		return String.format("%.1f %cB", bytes / Math.pow(unit, exp), pre);
	}

	public VVFileInfo getParentFolder() {
		if (!isRoot())
			try {
				return api.getFileInfoAt(folderName);
			} catch (VVEXcption e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
}
