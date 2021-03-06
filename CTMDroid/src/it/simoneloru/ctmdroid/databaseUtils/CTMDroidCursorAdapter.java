/**
 * This file is part of C.T.M.Droid.
 *
 * C.T.M.Droid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * C.T.M.Droid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with C.T.M.Droid.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package it.simoneloru.ctmdroid.databaseUtils;

import it.simoneloru.ctmdroid.R;
import it.simoneloru.ctmdroid.utils.CTMDroidUtil;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CTMDroidCursorAdapter extends CursorAdapter {

	private final LayoutInflater mInflater;
	private SharedPreferences settings;

	public CTMDroidCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = LayoutInflater.from(context);
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public CTMDroidCursorAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		settings = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvRoad = (TextView) view.findViewById(R.id.firstLine);
		TextView tvLine = (TextView) view.findViewById(R.id.secondLine);

		String road = cursor.getString(cursor
				.getColumnIndex(CTMDroidDatabase.KEY_ROAD));
		String line = cursor.getString(cursor
				.getColumnIndex(CTMDroidDatabase.KEY_LINE));

		tvRoad.setText(road);
		tvLine.setText(line);

		String favSettings = settings.getString("fav", "");
		Log.i("fav", "favsetting: " + favSettings);
		CheckBox cb = (CheckBox) view.findViewById(R.id.icon);
		long idLong = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		String rowId = Long.toString(idLong);
		cb.setTag(rowId);
		List<String> favList;
		if (!"".equals(favSettings)) {
			favList = CTMDroidUtil.CsvToArray(favSettings);
			if (favList.contains(cb.getTag())) {
				cb.setChecked(true);
			}
		}
		cb.setFocusable(false);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
				List<String> favList;
				String favSettings = settings.getString("fav", "");
				Log.i("fav", "favsetting: " + favSettings);
				if (!"".equals(favSettings)) {
					favList = CTMDroidUtil.CsvToArray(favSettings);
				} else {
					favList = new ArrayList<String>();
				}
				String id = cb.getTag().toString();
				if (isChecked) {
					if (!favList.contains(id)) {
						favList.add(id);
					}
				} else {
					if (favList.contains(id)) {
						favList.remove(id);
					}
				}
				Editor edit = settings.edit();
				String arrayToCsv = CTMDroidUtil.ArrayToCsv(favList);
				edit.putString("fav", arrayToCsv);
				Log.i("fav", "arrayToCsv: " + arrayToCsv);
				edit.commit();
			}
		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.list_item, parent, false);
		return view;
	}

}
