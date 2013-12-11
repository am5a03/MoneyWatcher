package com.MoneyWatcher.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.MoneyWatcher.R;

public class SettingActivity extends PreferenceActivity{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
