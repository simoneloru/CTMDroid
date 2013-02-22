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

import it.simoneloru.ctmdroid.utils.CTMDroidUtil;
import it.simoneloru.ctmdroid.utils.CTMDroidDatabaseHelper;

import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class CTMDroidDatabase {

	public static final String KEY_CODE = "code";
	public static final String KEY_ROAD = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String KEY_LINE = SearchManager.SUGGEST_COLUMN_TEXT_2;

	public static final String DATABASE_NAME = "CTMDroid";
	public static final String FTS_VIRTUAL_TABLE = "FTSCTMDroid";

	private SQLiteDatabase readableDatabase;

	private final CTMDroidDatabaseHelper mDatabaseOpenHelper;

	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	public CTMDroidDatabase(Context context) {
		mDatabaseOpenHelper = new CTMDroidDatabaseHelper(context);
		readableDatabase = mDatabaseOpenHelper.getReadableDatabase();
	}

	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KEY_CODE, KEY_CODE);
		map.put(KEY_ROAD, KEY_ROAD);
		map.put(KEY_LINE, KEY_LINE);
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS "
				+ SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		return map;
	}

	public Cursor getRoad(String rowId, String[] columns) {
		String selection = "rowid = ?";
		String[] selectionArgs = new String[] { rowId };

		return query(selection, selectionArgs, columns);
	}

	public Cursor getRoadMatches(String query, String[] columns) {
		try {
			Integer.parseInt(query);
			String selection = KEY_CODE + " LIKE ?";
			query = CTMDroidUtil.fillQueryWithZeros(query);
			String[] selectionArgs = new String[] { "__" + query };
			return query(selection, selectionArgs, columns);
		} catch (Exception e) {
			String selection = KEY_ROAD + " MATCH ?";
			String[] selectionArgs = new String[] { query + "*" };
			return query(selection, selectionArgs, columns);
		}
	}

	public Cursor getRoadIn(List<String> list) {
		String selection = BaseColumns._ID + " IN (";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				selection = selection + "?";
			}
			selection = selection + ", ?";
		}
		selection = selection + ")";
		String[] arrayString = list.toArray(new String[list.size()]);
		return query(selection, arrayString, null);
	}

	private Cursor query(String selection, String[] selectionArgs,
			String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(FTS_VIRTUAL_TABLE);
		builder.setProjectionMap(mColumnMap);

		Cursor cursor = builder.query(readableDatabase, columns, selection,
				selectionArgs, null, null, null);

		if (cursor == null) {
			Log.e("error", "cursor=null");
			return null;
		} else if (!cursor.moveToFirst()) {
			Log.e("error", "ora chiudo il cursor");
			cursor.close();
			return null;
		}
		return cursor;
	}

	public void close() {
		if (mDatabaseOpenHelper != null) {
			mDatabaseOpenHelper.close();
		}
	}

}
