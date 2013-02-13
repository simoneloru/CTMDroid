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

package it.simoneloru.ctmdroid.actions;

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
