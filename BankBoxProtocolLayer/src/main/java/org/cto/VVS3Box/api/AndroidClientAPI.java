package org.cto.VVS3Box.api;

import java.util.List;

/**
 * abstraction layer for accessing VV services over the network. Implementation
 * should perform any REST/other services
 * 
 * @author lior
 * 
 */
public interface AndroidClientAPI {

	/**
	 * Login into the VV services. should invalidate any currently logged-in
	 * user
	 * 
	 * @param identityKey
	 *            The Identity Key is obtained from the Web Login process (after
	 *            a successful login) and used to initialize the session with
	 *            the V.V server
	 * @throws VVEXcption
	 *             if there was an error performing the login process
	 */
	public abstract VVSessionInfo login(String identityKey) throws VVEXcption;

	/**
	 * Returns information about the current session, or <B>null</B> if the user
	 * is logged out
	 * 
	 * @return
	 */
	public abstract VVSessionInfo getSession();

	/**
	 * 
	 * @return True if the user is logged in
	 */
	public abstract boolean isLoggedIn();

	/**
	 * logs out the current user and invalidates the session
	 * 
	 * @throws VVEXcption
	 *             if the process was unsuccessful. In this case the session
	 *             should be invalidated and the user logged off
	 */
	public abstract void logout() throws VVEXcption;;

	/**
	 * Returns updated summary about the currently logged-in user
	 * 
	 */
	public abstract VVUserInfo getUserInfo() throws VVEXcption;

	/**
	 * Returns the root folder. This method might be called frequently
	 * 
	 * @return
	 * @throws VVEXcption
	 */
	public abstract VVFileInfo getRootFolder() throws VVEXcption;

	/**
	 * Returns a File or Folder object for the specified path, or null if no
	 * such path exists
	 * 
	 * @return
	 * @throws VVEXcption
	 */
	public abstract VVFileInfo getFileInfoAt(String path) throws VVEXcption;

	/**
	 * Returns the files and folders within a given path (only first level
	 * descendants)
	 * 
	 * @param path
	 * @return
	 * @throws VVEXcption
	 */
	public abstract List<VVFileInfo> getChildrenOf(String path)
			throws VVEXcption;

	/**
	 * Creates a new folder on the server, also creating the entire folder chain
	 * from the root up to this folder
	 * 
	 * @param fullpath
	 *            the full path to create
	 * @throws VVEXcption
	 */
	public abstract void createFolder(String fullpath) throws VVEXcption;

	/**
	 * Deletes a file or folder from the server.
	 * 
	 * @param fullpath
	 *            the full path to delete if the path is a folder, this method
	 *            deletes the entire sub-tree
	 * @throws VVEXcption
	 */
	public abstract void delete(String fullpath) throws VVEXcption;

	/**
	 * Returns a {@link FileTransferManager} object that will handle file
	 * transfer to/from the server. operations on the FileTransferManager will
	 * interact automatically with the UI
	 * 
	 * @return
	 */
	public FileTransferManager getTransferManager();

	/**
	 * Returns the URL to use for the Web Login
	 * 
	 * @return
	 */
	public abstract String getWebLoginUrl();

	/**
	 * A callback handler used for background tasks to report ongoing progress.
	 * Used by {@link FileTransferManager} to report progress
	 * 
	 * @author lior
	 * 
	 */
	public interface ProgressHandler {
		/**
		 * reports the current progress
		 * 
		 * @param percent
		 */
		public void updateProgress(int percent);
	}
}
