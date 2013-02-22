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
