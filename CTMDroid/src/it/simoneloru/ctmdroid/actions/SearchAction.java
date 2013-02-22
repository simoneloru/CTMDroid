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