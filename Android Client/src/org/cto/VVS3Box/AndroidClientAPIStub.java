/**
 * 
 */
package org.cto.VVS3Box;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.api.AndroidClientAPI;
import org.cto.VVS3Box.api.FileTransferManager;
import org.cto.VVS3Box.api.VVEXcption;
import org.cto.VVS3Box.api.VVFileInfo;
import org.cto.VVS3Box.api.VVSessionInfo;
import org.cto.VVS3Box.api.VVUserInfo;

/**
 * abstraction layer for accessing VV services over the network. currently a
 * stub but should be replaced with actual implementation (REST/other)
 * 
 * @author lior
 * 
 */
public class AndroidClientAPIStub implements AndroidClientAPI {

	private final DummyTransferManager DUMMY_TRANSFER_MANAGER = new DummyTransferManager();
	private final VVUserInfo DUMMY_USER = new VVUserInfo("userId23",
			"John Smith", 1233574456, 444559878, 30);

	private static final Log logger = LogFactory
			.getLog(AndroidClientAPIStub.class);

	private static final AndroidClientAPIStub vvapi = new AndroidClientAPIStub();

	private VVSessionInfo vvSessionInfo = null;
	private Map<String, VVFileInfo> allFiles;

	public AndroidClientAPIStub() {
		generateDummyFiles();
	}

	@Override
	public VVSessionInfo login(String identityToken) throws VVEXcption {
		this.logout();
		logger.debug("Logging in with identity key: " + identityToken);
		vvSessionInfo = new VVSessionInfo(identityToken, "sessionKey1234",
				new Date(), new Date(Long.MAX_VALUE));
		return vvSessionInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lior.VVS3Box.api.AndroidClientAPI#isLoggedIn()
	 */
	@Override
	public boolean isLoggedIn() {
		return vvSessionInfo != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lior.VVS3Box.api.AndroidClientAPI#logout()
	 */
	@Override
	public void logout() {
		vvSessionInfo = null;
		logger.debug("Logging out");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lior.VVS3Box.api.AndroidClientAPI#getUserInfo()
	 */
	@Override
	public VVUserInfo getUserInfo() {
		if (vvSessionInfo == null)
			return null;
		return DUMMY_USER;
	}

	@Override
	public VVSessionInfo getSession() {
		return vvSessionInfo;
	}

	@Override
	public FileTransferManager getTransferManager() {
		return DUMMY_TRANSFER_MANAGER;
	}

	/*
	 * generate a set of random files
	 */
	private void generateDummyFiles() {
		allFiles = new HashMap<String, VVFileInfo>();
		addFileInfo("\\rootfile.txt", 5643);
		addFileInfo("\\folder1\\subfolder1\\image.jpg", 95973);
		addFileInfo("\\folder1\\subfolder1\\some file.docx", 41211);
		addFileInfo("\\folder1\\a picture.bmp", 51483);
		addFileInfo("\\folder1\\subfolder2\\file file.txt.jpg", 85311);
		addFileInfo("\\folder1\\subfolder2\\backup.dat", 193812858);
		String pfx = "\\bigfolder2\\";
		Random rand = new Random();
		// for (int i = 120; --i > 0;)
		// addFileInfo(pfx + "file-" + i + ".dat", rand
		// .nextInt(1000000));
	}

	/**
	 * adds a file, optionally creating {@link VVFileInfo} objects for its
	 * parent folders
	 * 
	 * @param files
	 * @return
	 */
	private void addFileInfo(String pathname, long size) {
		if (allFiles.containsKey(pathname.toLowerCase()))
			return;
		allFiles.put(pathname.toLowerCase(), new VVFileInfo(pathname, size,
				new Date(), "hash-" + pathname, this));
		// create the parent folder structure
		int index = pathname.length() - 1;
		// root folder:
		if (index == 0)
			return;
		// it's a folder, ignore last "\"
		if (pathname.endsWith("\\"))
			index--;
		index = pathname.lastIndexOf("\\", index);
		if (index >= 0) {
			String parentPath = pathname.substring(0, index + 1);
			addFileInfo(parentPath, 0);
		}
	}

	@Override
	public void createFolder(String fullpath) throws VVEXcption {
		addFileInfo(fullpath, 0);

	}

	@Override
	public void delete(String fullpath) throws VVEXcption {
		Iterator<Map.Entry<String, VVFileInfo>> iter = allFiles.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, VVFileInfo> entry = iter.next();
			if (entry.getKey().startsWith(fullpath)) {
				iter.remove();
			}
		}
	}

	public static AndroidClientAPI getApi() {
		return vvapi;
	}

	/**
	 * A stub for a file manager - it simulates file uploads/downloads
	 * 
	 * @author lior
	 * 
	 */
	private final class DummyTransferManager implements FileTransferManager {
		@Override
		public void upload(File fileToUpload, String pathToSave,
				ProgressHandler progressHandler) throws VVEXcption {
			logger.info("uploading file to: " + pathToSave);
			int size = (int) fileToUpload.length();
			try {
				for (int i = 0; i < 10; i++) {
					progressHandler.updateProgress(i * 10);
					Thread.sleep(1000);
				}
				addFileInfo(pathToSave, size);
				logger.info("upload complete!");
			} catch (InterruptedException e) {
				throw new VVEXcption(e);
			}
		}

		@Override
		public void download(String pathToDownload, OutputStream fileToSave,
				ProgressHandler progressHandler) throws VVEXcption {
			logger.info("downloading file: " + pathToDownload);
			try {
				VVFileInfo file = getFileInfoAt(pathToDownload);
				if (file == null)
					throw new VVEXcption("Download error! file not found: "
							+ pathToDownload);
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(fileToSave));
				writer.write("Downloaded file. File info:\n" + file.toString());
				for (int i = 0; i < 10; i++) {
					progressHandler.updateProgress(i * 10);
					Thread.sleep(1000);
				}
				writer.close();
				logger.info("download complete!");
			} catch (IOException e) {
				throw new VVEXcption(e);
			} catch (InterruptedException e) {
				throw new VVEXcption(e);
			}
		}
	}

	@Override
	public String getWebLoginUrl() {
		return "https://login.bankhapoalim.co.il/cgi-bin/poalwwwc";
	}

	@Override
	public VVFileInfo getRootFolder() throws VVEXcption {
		return getFileInfoAt("\\");
	}

	@Override
	public List<VVFileInfo> getChildrenOf(String path) throws VVEXcption {
		List<VVFileInfo> result = new ArrayList<VVFileInfo>();
		for (VVFileInfo file : allFiles.values()) {
			if (file.getFolderName().equals(path)) {
				result.add(file);
			}
		}
		return result;
	}

	@Override
	public VVFileInfo getFileInfoAt(String path) throws VVEXcption {
		return allFiles.get(path.toLowerCase());
	}

}
