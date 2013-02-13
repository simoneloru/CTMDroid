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

package it.simoneloru.ctmdroid.activities;

import it.simoneloru.ctmdroid.R;
import it.simoneloru.ctmdroid.actions.HelpAction;
import it.simoneloru.ctmdroid.actions.SearchAction;
import it.simoneloru.ctmdroid.databaseUtils.CTMDroidCursorAdapter;
import it.simoneloru.ctmdroid.databaseUtils.CTMDroidDatabase;
import it.simoneloru.ctmdroid.providers.CTMDroidProvider;
import it.simoneloru.ctmdroid.utils.CTMDroidUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class MainActivity extends ListActivity {

	private Intent intent;
	private CTMDroidDatabase ctmDb;
	private Toast t;

	private SharedPreferences settings = null;

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		settings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		ctmDb = new CTMDroidDatabase(getApplicationContext());
		intent = new Intent(getApplicationContext(),
				TimetableActivity.class);
		lv = getListView();
		intent = getIntent();
		t = Toast.makeText(getApplicationContext(), R.string.empty_fav,
				Toast.LENGTH_LONG);
		String stringSetting = settings.getString("fav", "");
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			String rowId = intent.getData().getLastPathSegment();
			Cursor bus = ctmDb.getRoad(rowId, null);
			CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(
					getApplicationContext(), bus, true);
			lv.setAdapter(roadAdapter);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		} else if (CTMDroidUtil.FAV_ACTION.equals(intent.getAction())
				|| Intent.ACTION_MAIN.equals(intent.getAction())) {
			if ("".equals(stringSetting)) {
				if (!settings.getBoolean(CTMDroidUtil.PREF_DISC, false)
						&& Intent.ACTION_MAIN.equals(intent.getAction())) {
					showDialog(CTMDroidUtil.DIALOG_DISCLAIMER);
				} else {
					t.show();
				}
			} else {
				Cursor roadInCursor = ctmDb.getRoadIn(CTMDroidUtil
						.CsvToArray(stringSetting));
				CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(
						getApplicationContext(), roadInCursor, true);
				lv.setAdapter(roadAdapter);
			}
		}
		lv.setTextFilterEnabled(true);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				intent = new Intent(getApplicationContext(),
						TimetableActivity.class);
				intent.putExtra(getPackageName() + ".busStopCodeId", id);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		actionBarManage();

	}

	private void showResults(String query) {
		Cursor cursor = managedQuery(CTMDroidProvider.CONTENT_URI, null, null,
				new String[] { query }, null);
		if (cursor == null) {
		} else {
			CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(
					getApplicationContext(), cursor, true);
			lv.setAdapter(roadAdapter);
		}
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}

	public static Intent createIntent(Context context, String action) {
		Intent i = new Intent(context, MainActivity.class);
		i.setAction(action);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}

	private void actionBarManage() {
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(R.string.app_name);
		actionBar.setHomeAction(new HelpAction(this,
				android.R.drawable.ic_menu_help));
		final Action favAction = new IntentAction(getApplicationContext(),
				createIntent(getApplicationContext(), CTMDroidUtil.FAV_ACTION),
				R.drawable.ic_action_star);
		actionBar.addAction(favAction);
		final Action searchAction = new SearchAction(this,
				R.drawable.ic_action_search);
		actionBar.addAction(searchAction);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ctmDb != null) {
			ctmDb.close();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		switch (id) {
		case CTMDroidUtil.DIALOG_DISCLAIMER:
			doDisclaimerDialog(alertBuilder);
			break;
		case CTMDroidUtil.DIALOG_HELP:
			doHelpDialog(alertBuilder);
			break;
		}
		AlertDialog alertMsg = alertBuilder.create();
		return alertMsg;
	}

	private void doDisclaimerDialog(AlertDialog.Builder alertBuilder) {
		alertBuilder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						t.show();
					}
				});
		String appName = getResources().getString(R.string.app_name);
		String appVersion = getResources().getString(R.string.app_version);
		alertBuilder.setTitle(appName + " " + appVersion);
		LayoutInflater alertInflater = LayoutInflater.from(this);
		View disclaimerLayout = alertInflater
				.inflate(R.layout.disclaimer, null);
		alertBuilder.setView(disclaimerLayout);
		CheckBox dontShowAgain = (CheckBox) disclaimerLayout
				.findViewById(R.id.dontShowAgain);
		dontShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Editor settingEditor = settings.edit();
				if (isChecked) {
					settingEditor.putBoolean(CTMDroidUtil.PREF_DISC, true);
					settingEditor.commit();
				} else if (!isChecked) {
					settingEditor.putBoolean(CTMDroidUtil.PREF_DISC, false);
					settingEditor.commit();
				}
			}
		});
	}

	private void doHelpDialog(AlertDialog.Builder alertBuilder) {
		LayoutInflater alertInflater = LayoutInflater.from(this);
		View helpLayout = alertInflater.inflate(R.layout.help, null);
		alertBuilder.setView(helpLayout);
		alertBuilder.setPositiveButton(R.string.btn_chiudi, null);
		alertBuilder.setIcon(android.R.drawable.ic_dialog_info);
		alertBuilder.setTitle(R.string.help_title);

	}

	public void sendEmail(View view) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		String emailAddress = getResources().getString(R.string.email);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { emailAddress });
		startActivity(Intent.createChooser(emailIntent, "Invia email..."));
	}

}
