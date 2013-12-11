package com.MoneyWatcher.Activity;

import com.MoneyWatcher.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TestPrefActivity extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        addPreferencesFromResource(R.xml.preference);
        this.addPreferencesFromResource(R.xml.preferences);
//        setContentView(R.layout.main);
    }
}
