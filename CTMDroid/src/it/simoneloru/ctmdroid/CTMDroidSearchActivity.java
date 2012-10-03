package it.simoneloru.ctmdroid;

import it.simoneloru.ctmdroid.util.CTMDroidUtilities;
import android.app.Activity;
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
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class CTMDroidSearchActivity extends ListActivity {

	private static final String PREF_DISC = "disc";
	private Intent intent;
	private CTMDroidDatabase ctmDb;
	public static final String FAV_ACTION = "favAction";
	private Toast t;

	private static final int DIALOG_DISCLAIMER = 0;
	private static final int DIALOG_HELP = 1;

	private SharedPreferences settings = null;

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		ctmDb = new CTMDroidDatabase(getApplicationContext());
		intent = new Intent(getApplicationContext(), CTMDroidResultActivity.class);
		lv = getListView();
		intent = getIntent();
		t = Toast.makeText(getApplicationContext(), R.string.empty_fav, Toast.LENGTH_LONG);
		String stringSetting = settings.getString("fav", "");
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			String rowId = intent.getData().getLastPathSegment();
			Cursor bus = ctmDb.getRoad(rowId, null);
			CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(getApplicationContext(), bus, true);
			lv.setAdapter(roadAdapter);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		} else if (FAV_ACTION.equals(intent.getAction()) || Intent.ACTION_MAIN.equals(intent.getAction())) {
			if ("".equals(stringSetting)) {
				if (!settings.getBoolean(PREF_DISC, false) && Intent.ACTION_MAIN.equals(intent.getAction())) {
					showDialog(DIALOG_DISCLAIMER);
				} else {
					t.show();
				}
			} else {
				Cursor roadInCursor = ctmDb.getRoadIn(CTMDroidUtilities.CsvToArray(stringSetting));
				CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(getApplicationContext(), roadInCursor, true);
				lv.setAdapter(roadAdapter);
			}
		}
		lv.setTextFilterEnabled(true);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				intent = new Intent(getApplicationContext(), CTMDroidResultActivity.class);
				intent.putExtra(getPackageName() + ".busStopCodeId", id);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		actionBarManage();

	}

	private void showResults(String query) {
		Cursor cursor = managedQuery(CTMDroidProvider.CONTENT_URI, null, null, new String[] { query }, null);
		if (cursor == null) {
		} else {
			CTMDroidCursorAdapter roadAdapter = new CTMDroidCursorAdapter(getApplicationContext(), cursor, true);
			lv.setAdapter(roadAdapter);
		}
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, CTMDroidSearchActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}

	public static Intent createIntent(Context context, String action) {
		Intent i = new Intent(context, CTMDroidSearchActivity.class);
		i.setAction(action);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return i;
	}

	private void actionBarManage() {
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(R.string.app_name);
		actionBar.setHomeAction(new HelpAction(this, android.R.drawable.ic_menu_help));
		final Action favAction = new IntentAction(getApplicationContext(), createIntent(getApplicationContext(), FAV_ACTION), R.drawable.ic_action_star);
		actionBar.addAction(favAction);
		final Action searchAction = new SearchAction(this, R.drawable.ic_action_search);
		actionBar.addAction(searchAction);
	}

	public static class SearchAction extends AbstractAction {

		private Activity mHost;

		public SearchAction(Activity host, int drawable) {
			super(drawable);
			mHost = host;
		}

		@Override
		public void performAction(View view) {
			try {
				mHost.onSearchRequested();
			} catch (Exception mnfe) {
				Toast.makeText(mHost, "error", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static class HelpAction extends AbstractAction {

		private Activity mHost;

		public HelpAction(Activity host, int drawable) {
			super(drawable);
			mHost = host;
		}

		@Override
		public void performAction(View view) {
			try {
				mHost.showDialog(DIALOG_HELP);
			} catch (Exception mnfe) {
				Toast.makeText(mHost, "error", Toast.LENGTH_SHORT).show();
			}
		}
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
		case DIALOG_DISCLAIMER:
			doDisclaimerDialog(alertBuilder);
			break;
		case DIALOG_HELP:
			doHelpDialog(alertBuilder);
			break;
		}
		AlertDialog alertMsg = alertBuilder.create();
		return alertMsg;
	}

	private void doDisclaimerDialog(AlertDialog.Builder alertBuilder) {
		alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				t.show();
			}
		});
		String appName = getResources().getString(R.string.app_name);
		String appVersion = getResources().getString(R.string.app_version);
		alertBuilder.setTitle(appName + " " + appVersion);
		LayoutInflater alertInflater = LayoutInflater.from(this);
		View disclaimerLayout = alertInflater.inflate(R.layout.disclaimer, null);
		alertBuilder.setView(disclaimerLayout);
		CheckBox dontShowAgain = (CheckBox) disclaimerLayout.findViewById(R.id.dontShowAgain);
		dontShowAgain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor settingEditor = settings.edit();
				if (isChecked) {
					settingEditor.putBoolean(PREF_DISC, true);
					settingEditor.commit();
				} else if (!isChecked) {
					settingEditor.putBoolean(PREF_DISC, false);
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
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		String emailAddress = getResources().getString(R.string.email);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailAddress });
		startActivity(Intent.createChooser(emailIntent, "Invia email..."));
	}

}
