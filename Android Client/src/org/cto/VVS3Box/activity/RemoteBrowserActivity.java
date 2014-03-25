package org.cto.VVS3Box.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.cto.VVS3Box.FileTransferTask;
import org.cto.VVS3Box.FileTransferTask.FileTransferEventListener;
import org.cto.VVS3Box.api.VVEXcption;
import org.cto.VVS3Box.api.VVFileInfo;
import org.lior.VVS3Box.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * The main browsing UI for remote files and folders this activity allows for
 * browsing, download/upload, file management and sharing
 * 
 * @author lior
 * 
 */
public class RemoteBrowserActivity extends BaseActivity {

	private static final int SELECT_LOCAL_FILE = 0;

	private ListView listView;

	private VVFileInfo currentFolder;

	private RemoteFilesArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_browser);
		// set the title to be the user's full name
		try {
			currentFolder = getAPI().getRootFolder();
			setTitle(getAPI().getUserInfo().getFullname());
		} catch (VVEXcption e) {
			showAlertDialog("API Error",
					"Error getting information from the server!");
			e.printStackTrace();
		}
		listView = (ListView) findViewById(R.id.remoteFileListView);
		refreshList();
		registerForContextMenu(listView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.remoteFileListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			VVFileInfo file = adapter.getItem(info.position);
			if (file.equals(VVFileInfo.DIRECTORY_UP))
				return;
			menu.setHeaderTitle(file.getFileName());
			String[] menuItems = getResources().getStringArray(
					file.isFolder() ? R.array.remoteFolderContextMenu
							: R.array.remoteFileContextMenu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}

	}

	/**
	 * handle the context menu on a file/folder
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		VVFileInfo file = adapter.getItem(info.position);
		String title = item.getTitle().toString();
		if ("Download".equalsIgnoreCase(title) && !file.isFolder()) {
			downloadFile(file, false);
		} else if ("Delete".equalsIgnoreCase(title)) {
			deleteFile(file);
		} else if (title.contains("Share") && !file.isFolder()) {
			downloadFile(file, true);
		}
		return true;
	}

	/**
	 * handle the upload and refresh buttons
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_uploadFile:
			// Select a file using an external program:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			startActivityForResult(intent, SELECT_LOCAL_FILE);

			return true;

		case R.id.action_refresh:
			refreshList();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refresh the list
	 */
	public void refreshList() {
		populateList(currentFolder);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_LOCAL_FILE
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			String localFile = uri.getPath();
			uploadFile(new File(localFile));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Delete a file or folder
	 * 
	 */
	private void deleteFile(final VVFileInfo file) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete " + file.getFileName() + "?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				try {
					getAPI().delete(file.getFullPath());
					Toast.makeText(RemoteBrowserActivity.this,
							"Deleted successfull!", Toast.LENGTH_SHORT).show();
					// refresh the view
					refreshList();

				} catch (VVEXcption e) {
					showAlertDialog("Error", "error deleting file!");
				}
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * Download a file
	 * 
	 * @param file
	 */
	private void downloadFile(final VVFileInfo file, final boolean share) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Download this file?\n" + file.getFileName());
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// download the file
				File downloadsFolder = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				// create the file
				final File localfile = new File(downloadsFolder, file.getFileName());
				FileTransferTask transferTask = new FileTransferTask(
						RemoteBrowserActivity.this, FileTransferTask.DOWNLOAD,
						localfile.getAbsolutePath(), file.getFullPath(), getAPI()
								.getTransferManager());
				// share on completion
				if (share)
				{
					transferTask.setFileTransferEventListener(new FileTransferEventListener() {
						
						@Override
						public void onTransferComplete() {
							// start a share intent
							Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
							shareIntent.setType("*/*");
							shareIntent.putExtra(Intent.EXTRA_STREAM,
									Uri.fromFile(localfile));

							startActivity(shareIntent);
						}
						
						@Override
						public void onProgressUpdate(int percent) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				transferTask.execute();
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * populate the list view with items. currently shows a flat list (no
	 * directory structure)
	 */
	private void populateList(VVFileInfo baseFolder) {
		currentFolder = baseFolder;
		ArrayList<VVFileInfo> items = new ArrayList<VVFileInfo>(
				baseFolder.getSubfolders());
		Collections.sort(items);
		ArrayList<VVFileInfo> files = new ArrayList<VVFileInfo>(
				baseFolder.getFiles());
		Collections.sort(files);
		items.addAll(files);
		if (!baseFolder.isRoot())
			items.add(0, VVFileInfo.DIRECTORY_UP);
		adapter = new RemoteFilesArrayAdapter(RemoteBrowserActivity.this,
				R.layout.list_item_remote_file, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				VVFileInfo file = adapter.getItem(position);
				if (file.equals(VVFileInfo.DIRECTORY_UP)) {
					VVFileInfo parentFolder = currentFolder.getParentFolder();
					if (parentFolder != null) {
						populateList(parentFolder);
					}
				} else if (file.isFolder())
					populateList(file);

				else
					onFileClick(file);
			}

			private void onFileClick(VVFileInfo file) {
				downloadFile(file, false);
			}

		});

	}

	/**
	 * Upload a file to the current folder
	 */
	private void uploadFile(final File localFile) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Upload this file?\n" + localFile.getName());
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				FileTransferTask transferTask = new FileTransferTask(
						RemoteBrowserActivity.this, FileTransferTask.UPLOAD,
						localFile.getAbsolutePath(), currentFolder
								.getFullPath() + localFile.getName(), getAPI()
								.getTransferManager());
				transferTask.setFileTransferEventListener(new FileTransferEventListener() {
					
					@Override
					public void onTransferComplete() {
						// refresh the list when the upload completes
						RemoteBrowserActivity.this.refreshList();
					}
					
					@Override
					public void onProgressUpdate(int percent) {
						// TODO Auto-generated method stub
						
					}
				});
				transferTask.execute();
				// refresh the view
				refreshList();

			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

}
