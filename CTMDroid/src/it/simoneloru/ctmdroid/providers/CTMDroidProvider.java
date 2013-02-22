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

package it.simoneloru.ctmdroid.providers;

import it.simoneloru.ctmdroid.databaseUtils.CTMDroidDatabase;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class CTMDroidProvider extends ContentProvider {

	private CTMDroidDatabase ctmDb;
	private static final String AUTHORITY = "it.simoneloru.ctmdroid.providers.CTMDroidProvider";
	private static final String CTMDROID_BASE_PATH = "CTMDroid";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CTMDROID_BASE_PATH);

	public static final String CONTENT_LINE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/mt-ctmdroid";
	public static final String CONTENT_ROAD_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/mt-ctmdroid";

	private static final int SEARCH_ROAD = 0;
	private static final int GET_ROAD = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(AUTHORITY, CTMDROID_BASE_PATH, SEARCH_ROAD);
		matcher.addURI(AUTHORITY, CTMDROID_BASE_PATH + "/#", GET_ROAD);
		// to get suggestions...
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		return matcher;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		ctmDb = new CTMDroidDatabase(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		case SEARCH_ROAD:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return search(selectionArgs[0]);
		case GET_ROAD:
			return getRoad(uri);
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}

	}

	private Cursor search(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID, CTMDroidDatabase.KEY_CODE, CTMDroidDatabase.KEY_ROAD, CTMDroidDatabase.KEY_LINE };

		return ctmDb.getRoadMatches(query, columns);
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID, CTMDroidDatabase.KEY_ROAD, CTMDroidDatabase.KEY_LINE, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };
		return ctmDb.getRoadMatches(query, columns);
	}

	private Cursor getRoad(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { CTMDroidDatabase.KEY_CODE, CTMDroidDatabase.KEY_ROAD, CTMDroidDatabase.KEY_LINE };

		return ctmDb.getRoad(rowId, columns);
	}

	private Cursor refreshShortcut(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { BaseColumns._ID, CTMDroidDatabase.KEY_ROAD, CTMDroidDatabase.KEY_LINE, SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return ctmDb.getRoad(rowId, columns);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

}
