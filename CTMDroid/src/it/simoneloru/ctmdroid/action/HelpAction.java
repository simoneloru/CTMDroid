package it.simoneloru.ctmdroid.action;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;

public class HelpAction extends AbstractAction {

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
