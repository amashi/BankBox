package org.cto.VVS3Box.api;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.cto.VVS3Box.api.AndroidClientAPI.ProgressHandler;

/**
 * A FileTransferManager handles all file transfers between the client and the
 * server. Implementation is guaranteed to be "UI safe" and will run in a
 * background thread. <br/>
 * Transfer progress can be reported back to the UI via a
 * {@link ProgressHandler} callback interface.
 * 
 * @author lior
 * 
 */
public interface FileTransferManager {
	/**
	 * Uploads a file to the server, creating it if the file doesn't exist. The
	 * user must be logged in for this operation to begin.<br />
	 * This method will be called on a background thread and can be considered
	 * "UI safe". the supplied {@link ProgressHandler} can be used as a callback
	 * to report the ongoing transfer progress to the UI.
	 * 
	 * @param fileToUpload
	 *            an open {@link InputStream} to read file data from. This
	 *            method should close the stream when finished (?)
	 * @param pathToSave
	 *            the full pathname to save the data on the server
	 * @param progressHandler
	 *            a {@link ProgressHandler} callback for reporting progress
	 *            (call {@link ProgressHandler.updateProgress})
	 * @throws VVEXcption
	 */
	public void upload(File fileToUpload, String pathToSave,
			ProgressHandler progressHandler) throws VVEXcption;

	/**
	 * Downloads a file from the server. The user must be logged in for this
	 * operation to begin.<br />
	 * This method will be called on a background thread and can be considered
	 * "UI safe". the supplied {@link ProgressHandler} can be used as a callback
	 * to report the ongoing transfer progress to the UI.
	 * 
	 * @param pathToDownload
	 *            the full pathname of the file on the server
	 * 
	 * @param fileToSave
	 *            an open {@link OutputStream} to write file data into. This
	 *            method should close the stream when finished (?)
	 * @param progressHandler
	 *            a {@link ProgressHandler} callback for reporting progress
	 *            (call {@link ProgressHandler.updateProgress})
	 * @throws VVEXcption
	 */
	public void download(String pathToDownload, OutputStream fileToSave,
			ProgressHandler progressHandler) throws VVEXcption;

}