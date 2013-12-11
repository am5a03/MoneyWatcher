package com.MoneyWatcher.Activity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.MoneyWatcher.R;
import com.MoneyWatcher.Activity.Dialog.AddTagCatActivity;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.RequestCode;
import com.MoneyWatcher.frm.db.CashFlowAdapter;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.frm.parcelable.ParcelableCashFlow;
import com.MoneyWatcher.interfaces.ISearchConstants;
import com.actionbarsherlock.app.SherlockActivity;

public class AddActivity extends SherlockActivity implements OnClickListener, ISearchConstants{
	private ConfigAdapter ca = new ConfigAdapter(null, this);
	private String id;
	private String[] categories = ca.getCategories();
	private String[] tags = ca.getTags();
	private boolean[] isCheckedIdx = new boolean[tags.length];
	private boolean[] isCheckedIdxTmp;
	private int checkedItem;
	private Date now = new Date();
	
	private String dateTimeFormat;
	
	private String dateFormat;
	private String timeFormat;
	
	private String _date = "";
	private String _time = "";
	
	private boolean isEditMode = false;
	
	private RadioGroup rgType;
	private Button btnPickDate;
	private Button btnPickTime;
	private TextView lblCategories;
	private TextView lblTags;
	private EditText txtAmount;
	private EditText txtNote;
	
	private Button btnSave;
	private Button btnCancel;
	
	private ParcelableCashFlow pCashFlow;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newitem);
		
		init();		
	}
	
	private void init(){
		setTitle("Add a new transaction[...]");
		String callerActivity ="";
		
		if(this.getCallingActivity() != null){
			callerActivity = this.getCallingActivity().getClassName();
		}
		
		if(callerActivity.equals(MoneyWatcherActivity.class.getName())){
			this.isEditMode = false;
		}else if(callerActivity.equals(ViewCashFlowActivity.class.getName()) && 
					this.getIntent().getIntExtra(MWConstants.REQUESTCODE, -1) != RequestCode.ADD)
		{
			this.isEditMode = true;
			this.pCashFlow = this.getIntent().getParcelableExtra(SINGLECASHFLOW);
			setTitle("Edit transaction[...]");
		}
		
		this.dateTimeFormat = ca.getDateTimeFormat();
		this.dateFormat = ca.getDateFormat();
		this.timeFormat = ca.getTimeFormat();
		
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat(this.timeFormat);
		this._date = sdf.format(this.now);	
		this._time = sdf2.format(this.now);
		
		
		rgType = (RadioGroup) findViewById(R.id.rgType);
		lblCategories = (TextView) findViewById(R.id.lblCategories);
		lblTags = (TextView) findViewById(R.id.lblTags);
		
		btnPickDate = (Button)findViewById(R.id.btnPickDate);
		btnPickTime = (Button)findViewById(R.id.btnPickTime);
		
		txtAmount = (EditText) findViewById(R.id.txtAmount);
		txtNote = (EditText) findViewById(R.id.txtNote);
		
		btnSave = (Button)findViewById(R.id.btnSave);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		
		
		btnPickDate.setOnClickListener(this);
		btnPickTime.setOnClickListener(this);		
		lblTags.setOnClickListener(this);
		lblCategories.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		
		if(!isEditMode){
			btnPickDate.setText(_date);
			btnPickTime.setText(_time);
		}else{
			CashFlowAdapter cfa = new CashFlowAdapter(this);
			
			long idLong = this.getIntent().getLongExtra(ID, 0);
			this.id = ((Long)idLong).toString();
			
			CashFlow cf = cfa.getCashFlowById(id);
			
			double amount = cf.getAmount();
			String absAmount = ((Double)Math.abs(amount)).toString();
			String categories = cf.getCategories();
			String tags = cf.getTags();
			String note = cf.getNote();
			Date createTime = cf.getCreateTime();
			
			this._date = sdf.format(createTime);
			this._time = sdf2.format(createTime);
			
			if(cf.isSpending()){
				rgType.check(R.id.rbSpend);
			}else{
				rgType.check(R.id.rbIncome);
			}
			
			lblCategories.setText(categories);			
			txtAmount.setText(absAmount);			
			lblTags.setText(tags);
			btnPickDate.setText(_date);
			btnPickTime.setText(_time);
			txtNote.setText(note);
			btnSave.setText(R.string.update);
		}		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
			case R.id.btnSave:
				save();
				break;
			case R.id.lblCategories:
				createSelectCategoryDialog();
				break;
			case R.id.lblTags:
				System.out.println("Hello");
				createSelectTagsDialog();
				break;
			case R.id.btnPickDate:
//				System.out.println("Hello");
				createDateTimePicker(v);
				break;
			case R.id.btnPickTime:
//				System.out.println("Time");
				createDateTimePicker(v);
				break;
			case R.id.btnCancel:
				finish();
			default:
				break;
		}
	}
	
	private void save(){
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateTimeFormat);
		
		Date date = new Date();
		try {
			date = sdf.parse(btnPickDate.getText().toString() + " " + btnPickTime.getText().toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int type = rgType.getCheckedRadioButtonId();
		
		String _categories = lblCategories.getText().toString();
		_categories = (_categories.equals("") || _categories.equals(this.getResources().getText(R.string.selectCategory))) 
						? this.getResources().getText(R.string.uncategorized).toString() 
						: _categories;
		String _tags = lblTags.getText().toString();
		_tags = (_tags.equals(this.getResources().getText(R.string.selectTag))) ? "" : _tags;
		String _note = txtNote.getText().toString();
		
		
		
		double _amount = 0.0;
		try{
			_amount = Double.parseDouble(txtAmount.getText().toString());
			_amount = Math.round(_amount*100.0)/100.0;
		}catch(NumberFormatException ne){
			_amount = 0.0;
		}
		
		
		if(type == R.id.rbSpend){
			_amount = -_amount;
		}
		
		CashFlow cf = new CashFlow(_amount, _categories, _tags, _note);
		
		
		
		cf.setCreateTime(new Timestamp(date.getTime()));
		cf.setRecurItem(false);
		CashFlowAdapter cfa = new CashFlowAdapter(cf, this);		
		System.out.println(AddActivity.class.getCanonicalName());
		
		if(!isEditMode){
			if(cfa.persist()){
				Intent viewItemIntent = new Intent(getApplicationContext(), ViewCashFlowActivity.class);
				viewItemIntent.putExtra(MWConstants.CASHFLOWDATE, cf.getCreateTime().getTime());
				this.startActivityForResult(viewItemIntent, RequestCode.OK);
//				if(this.pCashFlow != null){
//					viewItemIntent.putExtra(SINGLECASHFLOW, this.pCashFlow);
//				}
				setResult(RESULT_OK, viewItemIntent);
			};
		}else{
			if(cfa.update(new String[] {this.id})){
				Intent viewItemIntent = new Intent(getApplicationContext(), ViewCashFlowActivity.class);
//				this.startActivityForResult(viewItemIntent, RequestCode.OK);
				//Update the cash flow object
				cf.setId(Long.parseLong(this.id));
				this.pCashFlow = new ParcelableCashFlow(cf);
				viewItemIntent.putExtra(SINGLECASHFLOW, this.pCashFlow);
				viewItemIntent.putExtra(POS, this.getIntent().getIntExtra(POS, -1));
				setResult(RESULT_OK, viewItemIntent);
			};
		}
		finish();
	}
	
	
	private void createSelectTagsDialog(){
//		System.out.println("Hello");
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		this.tags = this.ca.getTags();
		this.isCheckedIdxTmp = new boolean[this.tags.length];
		System.arraycopy(isCheckedIdx, 0, isCheckedIdxTmp, 0, this.isCheckedIdx.length);
		this.isCheckedIdx = this.isCheckedIdxTmp;
		System.arraycopy(isCheckedIdxTmp, 0, isCheckedIdx, 0, this.tags.length);
		
		dialog.setTitle(R.string.selectTag);
		
		dialog.setMultiChoiceItems(tags, isCheckedIdx, new OnMultiChoiceClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					isCheckedIdx[which] = true;
				}else{
					isCheckedIdx[which] = false;
				}
			}
		});
		
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicks OK button
	        	   TextView lblTags = (TextView) findViewById(R.id.lblTags);
	        	   ListAdapter dialogLst = ((AlertDialog)dialog).getListView().getAdapter();
	        	   
	        	   String prefix = "";
	        	   StringBuffer tagStr = new StringBuffer();
	        	   for(int i = 0; i < tags.length; i++){
	        		   if(isCheckedIdx[i]){
	        			   tagStr.append(prefix);
		        		   prefix = ",";
		        		   tagStr.append(dialogLst.getItem(i));
	        		   }
	        	   }
	        	   
	        	   lblTags.setText(tagStr);
	           }
	    });
		
		dialog.setNeutralButton(R.string.add, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked ADD button
	        	   Intent addTagIntent = new Intent(getApplicationContext(), AddTagCatActivity.class);
	        	   addTagIntent.putExtra("TYPE", MWConstants.ADDTAG);
	        	   startActivity(addTagIntent);
	           }
	    });
		
		dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked CANCEL button
	        	   dialog.dismiss();
	           }
	    });
		
		dialog.show();
		
	}
	
	private void createSelectCategoryDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		
		dialog.setTitle(R.string.selectCategory);
		
		this.categories = ca.getCategories();
		dialog.setSingleChoiceItems(categories, checkedItem, new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				checkedItem = which;
				TextView lblCategories = (TextView) findViewById(R.id.lblCategories);
				ListAdapter dialogLst = ((AlertDialog)dialog).getListView().getAdapter();
				lblCategories.setText(dialogLst.getItem(which).toString());
				
				dialog.dismiss();
			}
		});	
		
		
		dialog.setNeutralButton(R.string.add, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked ADD button
	        	   Intent addCatIntent = new Intent(getApplicationContext(), AddTagCatActivity.class);
	        	   addCatIntent.putExtra("TYPE", MWConstants.ADDCAT);
	        	   startActivity(addCatIntent);
	           }
	    });
		
		dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked CANCEL button
	        	   dialog.dismiss();
	           }
	    });
		
		dialog.show();
	}
	
	private void createDateTimePicker(View v){
		
		if(v.getId() == R.id.btnPickDate){
			Button btnPickDate = (Button) findViewById(R.id.btnPickDate);
			SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
			
			int cYear;
			int cMonth;
			int cDate;
			
			try {
				Date d = sdf.parse(btnPickDate.getText().toString());
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				
				cYear = c.get(Calendar.YEAR);
				cMonth = c.get(Calendar.MONTH);
				cDate = c.get(Calendar.DATE);
				
				DatePickerDialog dpd = new DatePickerDialog(this, dateListener, cYear, cMonth, cDate);
				
				dpd.show();
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}else if(v.getId() == R.id.btnPickTime){
			Button btnPickTime = (Button) findViewById(R.id.btnPickTime);
			SimpleDateFormat sdf = new SimpleDateFormat(this.timeFormat);
			
			int cHour;
			int cMinute;
			
			try{
				Date d = sdf.parse(btnPickTime.getText().toString());
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				
				cHour = c.get(Calendar.HOUR_OF_DAY);
				cMinute = c.get(Calendar.MINUTE);
				
				TimePickerDialog tpd = new TimePickerDialog(this, timeListener, cHour, cMinute, true);
				
				tpd.show();
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == RequestCode.ADD){
//			if(resultCode == RESULT_OK){
//				
//			}
//		}
//	}
	
	private OnDateSetListener dateListener = new OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);
			
			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			
			((Button)findViewById(R.id.btnPickDate)).setText(sdf.format(date));
			
		}
		
	};
	
	private OnTimeSetListener timeListener = new OnTimeSetListener(){

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			
			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
			
			((Button)findViewById(R.id.btnPickTime)).setText(sdf.format(date));
		}
		
	};
}
