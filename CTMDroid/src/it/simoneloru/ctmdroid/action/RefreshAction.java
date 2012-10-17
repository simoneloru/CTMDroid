package it.simoneloru.ctmdroid.action;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;

public class RefreshAction extends AbstractAction {

	private Activity mHost;

	public RefreshAction(Activity host, int drawable) {
		super(drawable);
		mHost = host;
	}

	@Override
	public void performAction(View view) {
		try {
			reload();
		} catch (Exception mnfe) {
			Toast.makeText(mHost, "error", Toast.LENGTH_SHORT).show();
		}
	}

	public void reload() {

		Intent intent = mHost.getIntent();
		mHost.overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		mHost.finish();

		mHost.overridePendingTransition(0, 0);
		mHost.startActivity(intent);
	}
}
