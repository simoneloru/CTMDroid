package it.simoneloru.ctmdroid.action;

import it.simoneloru.ctmdroid.R;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar.AbstractAction;

public class ShareAction extends AbstractAction {
	
	private Activity mHost;
	
	public ShareAction(Activity host, int drawable) {
		super(drawable);
		mHost = host;
	}
	
	@Override
	public void performAction(View view) {
		try {
			shareItem();
		} catch (Exception mnfe) {
			Toast.makeText(mHost, "error", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void shareItem() {
		final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		View wv = mHost.findViewById(R.id.webview);
		if(wv.getTag() != null && wv.getTag() instanceof String){
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, (String) wv.getTag());
			mHost.startActivity(Intent.createChooser(shareIntent, "Condividi"));
		}
	}
}
