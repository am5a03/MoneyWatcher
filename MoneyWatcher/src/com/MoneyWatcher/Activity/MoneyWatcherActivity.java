package com.MoneyWatcher.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.db.DBAdapter;
import com.actionbarsherlock.app.SherlockActivity;

public class MoneyWatcherActivity extends SherlockActivity implements OnClickListener{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
		Button btnStats = (Button) findViewById(R.id.btnStats);
		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		Button btnSettings = (Button) findViewById(R.id.btnSetting);
		
		btnAddItem.setOnClickListener(this);
		btnStats.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnSettings.setOnClickListener(this);
		
		DBAdapter.open(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.btnAddItem:
//			System.err.println("Hello");
			Intent addNewItemIntent = new Intent(v.getContext(), AddActivity.class);
			this.startActivityForResult(addNewItemIntent, Activity.DEFAULT_KEYS_DISABLE);
			break;
		case R.id.btnStats:
			Intent viewItem = new Intent(v.getContext(), StatisticsActivity.class);
//			Intent viewItem = new Intent(v.getContext(), TestPrefActivity.class);
			this.startActivityForResult(viewItem, Activity.DEFAULT_KEYS_DISABLE);
			break;
		case R.id.btnSearch:
			Intent searchItem = new Intent(v.getContext(), SearchActivity.class);
			this.startActivityForResult(searchItem, Activity.DEFAULT_KEYS_DISABLE);
			break;
		case R.id.btnSetting:
			Intent settingIntent = new Intent(v.getContext(), SettingActivity.class);
			this.startActivity(settingIntent);
			break;
		default:
			break;
		}
	}
	
	public void b1Action(View v){
		Intent settingIntent = new Intent(v.getContext(), ViewCashFlowActivity.class);
		this.startActivity(settingIntent);
	}
	
	public void b2Action(View v){
		Intent settingIntent = new Intent(v.getContext(), ViewCashFlowActivity.class);
		this.startActivity(settingIntent);
	}
}