package org.cto.VVS3Box.activity;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cto.VVS3Box.Item;
import org.lior.VVS3Box.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LocalFileBrowserActivity extends BaseActivity {

	private File currentDir;
	private LocalFilesArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_browser);
		currentDir = Environment.getExternalStorageDirectory();
		fill(currentDir);
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Item> dir = new ArrayList<Item>();
		List<Item> fls = new ArrayList<Item>();
		try {
			for (File ff : dirs) {
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if (ff.isDirectory()) {

					File[] fbuf = ff.listFiles();
					int buf = 0;
					if (fbuf != null) {
						buf = fbuf.length;
					} else
						buf = 0;
					String num_item = String.valueOf(buf);
					if (buf == 0)
						num_item = num_item + " item";
					else
						num_item = num_item + " items";

					// String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(), num_item, date_modify, ff
							.getAbsolutePath(), "directory_icon"));
				} else {

					fls.add(new Item(ff.getName(), ff.length() + " Byte",
							date_modify, ff.getAbsolutePath(), "file_icon"));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.equals(Environment.getExternalStorageDirectory()))
			dir.add(0, new Item("..", "Parent Directory", "", f.getParent(),
					"directory_up"));
		adapter = new LocalFilesArrayAdapter(LocalFileBrowserActivity.this,
				R.layout.list_item_local_file, dir);
		ListView listView = (ListView) findViewById(R.id.localFilesListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Item o = adapter.getItem(position);
				if (o.getImage().equalsIgnoreCase("directory_icon")
						|| o.getImage().equalsIgnoreCase("directory_up")) {
					currentDir = new File(o.getPath());
					fill(currentDir);
				} else {
					onFileClick(o);
				}
			}

			private void onFileClick(Item o) {
				// Toast.makeText(this, "Folder Clicked: "+ currentDir,
				// Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra("GetPath", currentDir.toString());
				intent.putExtra("GetFileName", o.getName());
				setResult(RESULT_OK, intent);
				finish();
			}

		});
	}
}
