package it.simoneloru.ctmdroid;

import it.simoneloru.ctmdroid.util.CTMDroidUtilities;

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

		String road = cursor.getString(cursor.getColumnIndex(CTMDroidDatabase.KEY_ROAD));
		String line = cursor.getString(cursor.getColumnIndex(CTMDroidDatabase.KEY_LINE));

		tvRoad.setText(road);
		tvLine.setText(line);
		
		String favSettings = settings.getString("fav", "");
		Log.i("fav", "favsetting: "+favSettings);
		CheckBox cb = (CheckBox)view.findViewById(R.id.icon);
		long idLong = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		String rowId = Long.toString(idLong);
		cb.setTag(rowId);
		List<String> favList;
		if(!"".equals(favSettings)){
    		favList = CTMDroidUtilities.CsvToArray(favSettings);
    		if(favList.contains(cb.getTag())){
    			cb.setChecked(true);
    		}
    	}
		cb.setFocusable(false);
	    cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        public void onCheckedChanged(CompoundButton cb, boolean isChecked) {       
	        	List<String> favList;
	        	String favSettings = settings.getString("fav", "");
	        	Log.i("fav", "favsetting: "+favSettings);
	        	if(!"".equals(favSettings)){
	        		favList = CTMDroidUtilities.CsvToArray(favSettings);
	        	} else {
	        		favList = new ArrayList<String>();
	        	}
	        	String id = cb.getTag().toString();
	        	if(isChecked){
	        		if(!favList.contains(id)){
	        			favList.add(id);
	        		}
	        	}else{
	        		if(favList.contains(id)){
	        			favList.remove(id);
	        		}
	        	}
	        	Editor edit = settings.edit();
				String arrayToCsv = CTMDroidUtilities.ArrayToCsv(favList);
				edit.putString("fav", arrayToCsv);
				Log.i("fav", "arrayToCsv: "+arrayToCsv);
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
