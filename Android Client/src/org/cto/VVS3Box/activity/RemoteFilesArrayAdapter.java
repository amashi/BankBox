package org.cto.VVS3Box.activity;

import java.util.List;

import org.cto.VVS3Box.api.VVFileInfo;
import org.lior.VVS3Box.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RemoteFilesArrayAdapter extends ArrayAdapter<VVFileInfo> {

	private Context c;
	private int id;
	private List<VVFileInfo> items;

	public RemoteFilesArrayAdapter(Context context, int textViewResourceId,
			List<VVFileInfo> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}

	public VVFileInfo getItem(int i) {
		return items.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}

		/* create a new view of my layout and inflate it in the row */
		// convertView = ( RelativeLayout ) inflater.inflate( resource, null );

		final VVFileInfo file = items.get(position);
		if (file != null) {
			TextView txtFilename = (TextView) v
					.findViewById(R.id.layout_remoteFileItem_filename);
			TextView txtFileInfo = (TextView) v
					.findViewById(R.id.layout_remoteFileItem_info);
			TextView txtFileDate = (TextView) v
					.findViewById(R.id.layout_remoteFileItem_date);
			ImageView icon = (ImageView) v
					.findViewById(R.id.layout_remoteFileItem_icon);
			if (file.equals(VVFileInfo.DIRECTORY_UP))
			{
				icon.setImageResource(R.drawable.directory_up);
				if (txtFilename != null)
					txtFilename.setText("..");
				if (txtFileInfo != null)
					txtFileInfo.setText("");
				if (txtFileDate != null)
					txtFileDate.setText("");
			}
			else
			{
				icon.setImageResource( file.isFolder() ? R.drawable.vv_folder_icon
						: R.drawable.vv_file_icon);
				if (txtFilename != null)
					txtFilename.setText(file.getFileName());
				if (txtFileInfo != null)
					txtFileInfo.setText(VVFileInfo.humanReadableByteCount(file
							.getSize()));
				if (txtFileDate != null)
					txtFileDate.setText(DateFormat.format("yyyy-MM-dd hh:mm",
							file.getLastModified()));
			}

		}
		return v;
	}

}
