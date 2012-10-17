package it.simoneloru.ctmdroid.action;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;

public class SearchAction extends AbstractAction {

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