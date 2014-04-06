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
import it.simoneloru.ctmdroid.actions.RefreshAction;
import it.simoneloru.ctmdroid.databaseUtils.CTMDroidDatabase;
import it.simoneloru.ctmdroid.utils.CTMDroidUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class TimetableActivity extends Activity {

	private static final String UKHE = "ukhe";
	private static final String NODATA = "no_data";
	private static final String URI = "***";
	private CTMDroidDatabase ctmDb;
	private WebView resultWebView;
	private ProgressBar pb;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		ctmDb = new CTMDroidDatabase(getApplicationContext());
		pb = (ProgressBar) findViewById(R.id.progressBarResult);
		if (savedInstanceState != null) {
			resultWebView = (WebView) findViewById(R.id.webview);
			pb.setVisibility(View.GONE);
			resultWebView.setVisibility(View.VISIBLE);
		} else {
			resultWebView = (WebView) findViewById(R.id.webview);
			HttpCallTask htct = new HttpCallTask();
			htct.execute();
		}
		actionBarManage();

	}

	private String callCtmSite() throws Exception {
		HttpResponse execute = null;
		HttpClient defaultHttpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost();
		request.setURI(new URI(URI));
		request.addHeader("Referer", URI);

		Cursor codeCursor = ctmDb.getRoad(
				Long.toString(getIntent().getLongExtra(
						getPackageName() + ".busStopCodeId", -1)),
				new String[] { CTMDroidDatabase.KEY_CODE });
		String code = codeCursor.getString(0);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("mode", "1"));
		nameValuePairs.add(new BasicNameValuePair("stopcode", "-1"));
		nameValuePairs.add(new BasicNameValuePair("stopdescr", code));
		nameValuePairs.add(new BasicNameValuePair("linenames", "-1"));
		nameValuePairs.add(new BasicNameValuePair("ptsearch", "Ricerca"));
		Calendar now = Calendar.getInstance();
		nameValuePairs.add(new BasicNameValuePair("pt_day", Integer
				.toString(now.get(Calendar.DAY_OF_MONTH))));
		nameValuePairs.add(new BasicNameValuePair("pt_month", Integer
				.toString(now.get(Calendar.MONTH) + 1)));
		nameValuePairs.add(new BasicNameValuePair("pt_year", Integer
				.toString(now.get(Calendar.YEAR))));

		nameValuePairs.add(new BasicNameValuePair("PT_HH_FROM", Integer
				.toString(now.get(Calendar.HOUR_OF_DAY))));
		Calendar later = new GregorianCalendar();
		later.setTime(now.getTime());
		later.add(Calendar.HOUR_OF_DAY, 1);
		nameValuePairs.add(new BasicNameValuePair("PT_HH_TO", Integer
				.toString(later.get(Calendar.HOUR_OF_DAY))));
		nameValuePairs.add(new BasicNameValuePair("PT_MM_FROM", Integer
				.toString(now.get(Calendar.MINUTE))));
		nameValuePairs.add(new BasicNameValuePair("PT_MM_TO", Integer
				.toString(later.get(Calendar.MINUTE))));
		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
				nameValuePairs);
		request.setEntity(urlEncodedFormEntity);
		execute = defaultHttpClient.execute(request);
		resultWebView = (WebView) findViewById(R.id.webview);
		BasicResponseHandler basicResponseHandler = new BasicResponseHandler();
		String handleResponse = basicResponseHandler.handleResponse(execute);
		if (handleResponse.contains("Dati non disponibili")) {
			return NODATA;
		} else {
			String body = handleResponse.substring(
					handleResponse.indexOf("<body"),
					handleResponse.lastIndexOf("</body>"));
			String homehtm = CTMDroidUtil.LoadFile("home.htm", getResources());
			Locale locale = this.getResources().getConfiguration().locale;
			if(!locale.equals(Locale.ITALY)){
				body = CTMDroidUtil.bodyInEnglish(body);
			}
			homehtm = homehtm.replace("$placeholder$", body);
			Log.i("CTM", homehtm);
			resultWebView.loadDataWithBaseURL("file:///android-asset/",
					homehtm, "text/html", "utf-8", null);
			resultWebView.setTag(homehtm);
			return null;
		}
	}

	private void actionBarManage() {
		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this, CTMDroidUtil.FAV_ACTION),
				R.drawable.ic_title_home_default));
		// final Action shareAction = new ShareAction(this,
		// android.R.drawable.ic_menu_share);
		// actionBar.addAction(shareAction);
		final Action refreshAction = new RefreshAction(this,
				R.drawable.ic_action_refresh);
		actionBar.addAction(refreshAction);
		actionBar.setTitle(R.string.app_name);
	}

	private class HttpCallTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				return callCtmSite();
			} catch (Exception e2) {
				e2.printStackTrace();
				return UKHE;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (UKHE.equals(result)) {
				pb.setVisibility(View.GONE);
				resultWebView.setVisibility(View.GONE);
				showDialog(0);
			} else if (NODATA.equals(result)) {
				RelativeLayout rl = (RelativeLayout) findViewById(R.id.unaviable_data);
				rl.setVisibility(View.VISIBLE);
				resultWebView.setVisibility(View.GONE);
			} else {
				pb.setVisibility(View.GONE);
				resultWebView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setMessage(R.string.unknowHost);
		alertBuilder.setTitle(android.R.string.dialog_alert_title);
		alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		alertBuilder.setPositiveButton(android.R.string.ok, null);
		AlertDialog eulaMsg = alertBuilder.create();
		return eulaMsg;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ctmDb != null) {
			ctmDb.close();
		}
	}

}