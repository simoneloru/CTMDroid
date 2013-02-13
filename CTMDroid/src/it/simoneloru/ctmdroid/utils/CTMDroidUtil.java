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

package it.simoneloru.ctmdroid.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

public class CTMDroidUtil {
	
	private static int cTheme;

	public final static int THEME_DEFAULT = 0;
	public final static int THEME_WHITE = 1;
	public final static int THEME_BLACK = 2;
	
	public static final String PREF_DISC = "disc";
	public static final String FAV_ACTION = "favAction";
	public static final int DIALOG_DISCLAIMER = 0;
	public static final int DIALOG_HELP = 1;

	public static void changeToTheme(Activity activity, int theme)
	{
		cTheme = theme;
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	/** Set the theme of the activity, according to the configuration. */
	public static void onCreateSetTheme(Activity activity)
	{
		switch (cTheme)
		{
		default:
		case THEME_DEFAULT:
			break;
		case THEME_WHITE:
			activity.setTheme(android.R.style.Theme_Light_NoTitleBar);
			break;
		case THEME_BLACK:
			activity.setTheme(android.R.style.Theme_Black_NoTitleBar);
			break;
		}
	}

	public static String LoadFile(String fileName, Resources resources) throws IOException  
	{  
		//Create a InputStream to read the file into  
		InputStream iS;  
		//get the file as a stream  
		iS = resources.getAssets().open(fileName);  
		//create a buffer that has the same size as the InputStream  
		byte[] buffer = new byte[iS.available()];  
		//read the text file as a stream, into the buffer  
		iS.read(buffer);  
		//create a output stream to write the buffer into  
		ByteArrayOutputStream oS = new ByteArrayOutputStream();  
		//write this buffer to the output stream  
		oS.write(buffer);  
		//Close the Input and Output streams  
		oS.close();  
		iS.close();  
		//return the output stream as a String  
		return oS.toString();  
	}  
	
	
	public static String ArrayToCsv(List<String> array){
		String csv = "";
		for (String string : array) {
			csv = csv + string + ",";
		}
		return csv;
	}
	
	public static List<String> CsvToArray(String csv){
		List<String> array = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(csv, ",");
		while(st.hasMoreTokens()){
			array.add(st.nextToken());
		}
		return array;
	}
	
	public static String fillQueryWithZeros(String query){
		String zero = "0";
		while(query.length()<4){
			query = zero+query;
		}
			return query;
	}

}
