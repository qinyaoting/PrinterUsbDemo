package com.hnj.as_usbxpdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


public class SharedPreferencesUtil {

	public final static String PID = "PID";
	public final static String VID = "VID";

	public static SharedPreferences preferences;
	public static void initPreferences(Context context) {
		if(preferences==null)
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}

	public static int getPID() {
		int mpid = preferences.getInt(PID, 22336);
		return mpid;
	}


	public static void savePID(int mpid) {
		Editor editor = preferences.edit();
		editor.putInt(PID, mpid);
		editor.commit();
	}	


	public static int getVID() {
		int mvid = preferences.getInt(VID, 1155);
		return mvid;
	}


	public static void saveVID(int mvid) {
		Editor editor = preferences.edit();
		editor.putInt(VID, mvid);
		editor.commit();
	}
}
