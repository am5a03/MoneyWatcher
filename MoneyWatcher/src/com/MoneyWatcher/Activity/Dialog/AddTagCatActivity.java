package com.MoneyWatcher.Activity.Dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.obj.MConfig;

public class AddTagCatActivity extends Activity implements OnClickListener{
	private MConfig c;
	private ConfigAdapter ca;
	private String type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		
		if(extras != null){
			this.type = extras.getString("TYPE");
			if(type.equals(MWConstants.ADDCAT)){
				setTitle("Add a new category");
			}else if(type.equals(MWConstants.ADDTAG)){
				setTitle("Add a new tag");
			}
		}
		setContentView(R.layout.newtagcat);
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		Button btnTagCatSave = (Button)findViewById(R.id.btnTagCatSave);
		Button btnTagCatCancel = (Button) findViewById(R.id.btnTagCatCancel);
		
		btnTagCatSave.setOnClickListener(this);
		btnTagCatCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.btnTagCatSave:
			save();
			break;
		case R.id.btnTagCatCancel:
			finish();
			break;
		}
	}
	
	private void save(){
		EditText txtTagCat = (EditText)findViewById(R.id.txtTagCat);
		String tagAndCat = txtTagCat.getText().toString();
		
		if(!tagAndCat.equals("") && tagAndCat != null){
			if(this.type.equals(MWConstants.ADDCAT)){
				this.c = new MConfig(MWConstants.ADDCAT, tagAndCat);
			}else if(this.type.equals(MWConstants.ADDTAG)){
				this.c = new MConfig(MWConstants.ADDTAG, tagAndCat);
			}
			this.ca = new ConfigAdapter(this.c, getApplicationContext());
			this.ca.persist();
			finish();
		}
	}
}
