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

package it.simoneloru.ctmdroid.utils;

import it.simoneloru.ctmdroid.R;
import it.simoneloru.ctmdroid.databaseUtils.CTMDroidDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class CTMDroidDatabaseHelper extends SQLiteOpenHelper {

	private static String TAG = "DataBaseHelper";

	private static String DB_NAME = "CTMDroidDB";

	public static final String TABLE_CTMDROID = "CTMDroid";
	public static final String ID = "_id";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE "
			+ CTMDroidDatabase.FTS_VIRTUAL_TABLE + " USING fts3 ("
			+ CTMDroidDatabase.KEY_CODE + ", " + CTMDroidDatabase.KEY_ROAD
			+ ", " + CTMDroidDatabase.KEY_LINE + ");";

	public CTMDroidDatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		myDataBase = db;
		myDataBase.execSQL(FTS_TABLE_CREATE);
		loadCTMDroidDB();
	}

	private void loadCTMDroidDB() {
		Log.i(TAG, "loadCTMDroidDB");
		try {
			loadCTMDroidRows();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadCTMDroidRows() throws IOException {
		Log.d(TAG, "Loading words...");
		final Resources resources = myContext.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.database);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] strings = TextUtils.split(line, ";");
				if (strings.length < 3)
					continue;
				long id = addData(strings[0].trim(), strings[1].trim(),
						strings[2].trim());
				if (id < 0) {
					Log.e(TAG, "unable to add word: " + strings[0].trim());
				}
			}
		} finally {
			reader.close();
		}
		Log.d(TAG, "DONE loading words.");
	}

	public long addData(String code, String road, String line) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CTMDroidDatabase.KEY_CODE, code);
		initialValues.put(CTMDroidDatabase.KEY_ROAD, road);
		initialValues.put(CTMDroidDatabase.KEY_LINE, line);

		return myDataBase.insert(CTMDroidDatabase.FTS_VIRTUAL_TABLE, null,
				initialValues);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CTMDroidDatabase.FTS_VIRTUAL_TABLE);
		onCreate(db);
	}

}