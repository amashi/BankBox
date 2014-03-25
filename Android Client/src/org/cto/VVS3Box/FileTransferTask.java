package org.cto.VVS3Box;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.cto.VVS3Box.api.AndroidClientAPI.ProgressHandler;
import org.cto.VVS3Box.api.FileTransferManager;
import org.cto.VVS3Box.api.VVEXcption;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * A class to perform actual file upload/download to the server on a background
 * thread, showing a progress bar. It extends the {@link AsyncTask} class which
 * handles background thread and UI interchange. <br/>
 * Internally, this class uses a {@link FileTransferManager} to perform the
 * actual file transfers
 * 
 * @see AsyncTask
 * @author lior
 * 
 */
public class FileTransferTask extends AsyncTask<Void, Integer, Long> implements
		ProgressHandler {

	/**
	 * listener for handling file transfer events on the UI thread
	 * 
	 * @author lior
	 * 
	 */
	public interface FileTransferEventListener {
		abstract void onTransferComplete();

		abstract void onProgressUpdate(int percent);
	}

	public static final int UPLOAD = 1;
	public static final int DOWNLOAD = 2;

	private ProgressDialog progressDlg;

	public int operation;
	public String remotePathname;
	public String localPathname;
	public FileTransferManager transferMgr;
	private Context context;
	private FileTransferEventListener eventListener;

	public void setFileTransferEventListener(
			FileTransferEventListener eventListener) {
		this.eventListener = eventListener;
	}

	public FileTransferTask(Context context, int operation,
			String localPathname, String remotePathname,
			FileTransferManager transferMgr) {
		this.operation = operation;
		this.remotePathname = remotePathname;
		this.localPathname = localPathname;
		this.transferMgr = transferMgr;
		this.context = context;

		progressDlg = new ProgressDialog(context);
		progressDlg.setTitle(operation == DOWNLOAD ? "Downloading..."
				: "Uploading");
		progressDlg.setMessage(new File(localPathname).getName());
		progressDlg.setCancelable(false);
		progressDlg.setIndeterminate(false);
		progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDlg.setMax(100);

	}

	@Override
	protected void onPostExecute(Long result) {
		if (progressDlg != null)
			progressDlg.dismiss();
		Toast.makeText(
				context,
				(operation == DOWNLOAD ? "Download " : "Upload ")
						+ "completed successfully!", Toast.LENGTH_SHORT).show();
		if (eventListener != null)
			eventListener.onTransferComplete();
	}

	@Override
	protected void onPreExecute() {
		// show the progress dialog
		progressDlg.show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		progressDlg.setProgress(values[0]);
		if (eventListener != null)
			eventListener.onProgressUpdate(values[0]);

	}

	/**
	 * called by {@link FileTransferManager} on the 'doInBackground' thread to
	 * report progress
	 */
	@Override
	public void updateProgress(int percent) {
		publishProgress(percent);
	}

	@Override
	protected Long doInBackground(Void... params) {
		try {
			if (operation == DOWNLOAD) {
				FileOutputStream fos;
				fos = new FileOutputStream(localPathname);
				transferMgr.download(remotePathname, fos, this);
			} else if (operation == UPLOAD) {
				File file = new File(localPathname);
				transferMgr.upload(file, remotePathname, this);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VVEXcption e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0L;
	}
}