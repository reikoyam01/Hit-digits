package jp.android.hitdigits;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Setting extends PreferenceActivity {    
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.xml.preference);  
    }  
  
    //public static boolean isSound(Context con){  
    //    return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("check1", false);  
    //}  
    //public static boolean isVib(Context con){  
    //    return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("check2", false);  
    //}  
}  