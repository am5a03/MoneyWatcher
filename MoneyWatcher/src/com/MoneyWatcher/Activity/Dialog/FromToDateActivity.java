package com.MoneyWatcher.Activity.Dialog;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.MoneyWatcher.R;
import com.MoneyWatcher.Activity.StatisticsActivity;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.db.ConfigAdapter;

public class FromToDateActivity extends Activity {
	private Date mFromDate;
	private Date mToDate;

	private Button btnFromDate;
	private Button btnToDate;
	private Button btnFromMonth;
	private Button btnToMonth;

	private RadioGroup rgRangeType;

	private RelativeLayout rlFromToDate;
	private RelativeLayout rlFromToMonth;

	private String dateFormat;
	private final String mMonthDateFormat = "MMM yyyy";
	
	private final int PIE = 0;
	private final int LINE = 1;
	private final int BAR = 2;
	
	private final int BY_DATE = 4;
	private final int BY_MONTH = 5;
	private int mRenderMethod = BY_DATE; //Default is by date
	
	
	private int mChartType;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fromtodate);
		init();
	}

	private void init() {
		setTitle("Select Date Range[...]");
		ConfigAdapter ca = new ConfigAdapter(this);
		this.dateFormat = ca.getDateFormat();
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		SimpleDateFormat sdf2 = new SimpleDateFormat(this.mMonthDateFormat);
		
		this.mChartType = this.getIntent().getIntExtra(MWConstants.CHARTTYPE, -1);
		long _fromDate = this.getIntent().getLongExtra(MWConstants.FROMDATE, -1);
		long _toDate = this.getIntent().getLongExtra(MWConstants.TODATE, -1);
		Date _day;
		String _fromDateStr, _fromDateStr2;
		String _toDateStr, _toDateStr2;
		//Get the from-to date from StatisiticsActivity 
		if(_fromDate != -1 && _toDate != -1){
			_fromDateStr = sdf.format(new Date(_fromDate));
			_toDateStr = sdf.format(new Date(_toDate));
			
			_fromDateStr2 = sdf2.format(_fromDate);
			_toDateStr2 = sdf2.format(_toDate);
		}else{
			Calendar c = Calendar.getInstance();
			_day = c.getTime();
			_fromDateStr = sdf.format(_day);
			_toDateStr = sdf.format(_day);
			
			_fromDateStr2 = sdf2.format(_day);
			_toDateStr2 = sdf2.format(_day);
		}

		// Layout
		this.rlFromToDate = (RelativeLayout) findViewById(R.id.rlFromToDate);
		this.rlFromToMonth = (RelativeLayout) findViewById(R.id.rlFromToMonth);
		this.rlFromToMonth.setVisibility(View.GONE);

		// Date range
		this.btnFromDate = (Button) findViewById(R.id.btnFromDate);
		this.btnToDate = (Button) findViewById(R.id.btnToDate);

		// Month range
		this.btnFromMonth = (Button) findViewById(R.id.btnFromMonth);
		this.btnToMonth = (Button) findViewById(R.id.btnToMonth);

		// Radio group
		this.rgRangeType = (RadioGroup) findViewById(R.id.rgRangeType);
		this.rgRangeType.setOnCheckedChangeListener(rgTypeChangeListener);

		this.btnFromDate.setText(_fromDateStr);
		this.btnToDate.setText(_toDateStr);
		
		this.btnFromMonth.setText(_fromDateStr2);
		this.btnToMonth.setText(_toDateStr2);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		
		if(this.mChartType == PIE){
			this.rgRangeType.setVisibility(View.GONE);
		}
	}

	private void createDatePickDialog(View v) {
		int cYear = 0;
		int cMonth = 0;
		int cDate = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		Date d;

		if (v.getId() == R.id.btnFromDate) {
			try {
				d = sdf.parse(btnFromDate.getText().toString());
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				cDate = c.get(Calendar.DATE);
				cMonth = c.get(Calendar.MONTH);
				cYear = c.get(Calendar.YEAR);
				DatePickerDialog dpd = new DatePickerDialog(this,
						fromDateListener, cYear, cMonth, cDate);
				dpd.show();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (v.getId() == R.id.btnToDate) {
			try {
				d = sdf.parse(btnToDate.getText().toString());
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				cDate = c.get(Calendar.DATE);
				cMonth = c.get(Calendar.MONTH);
				cYear = c.get(Calendar.YEAR);
				DatePickerDialog dpd = new DatePickerDialog(this,
						toDateListener, cYear, cMonth, cDate);
				dpd.show();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private DatePickerDialog MonthPickerDialog(OnDateSetListener odsl, int cYear, int cMonth){
		int cDate = 1;
		DatePickerDialog dpd =  new DatePickerDialog(this, odsl, cYear, cMonth, cDate);
		try {
			Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
			for (Field datePickerDialogField : datePickerDialogFields) {
				if (datePickerDialogField.getName().equals("mDatePicker")) {
					datePickerDialogField.setAccessible(true);
					DatePicker datePicker = (DatePicker) datePickerDialogField
							.get(dpd);
					Field datePickerFields[] = datePickerDialogField.getType()
							.getDeclaredFields();
					for (Field datePickerField : datePickerFields) {
						if ("mDayPicker".equals(datePickerField.getName())) {
							datePickerField.setAccessible(true);
							Object dayPicker = new Object();
							dayPicker = datePickerField.get(datePicker);
							((View) dayPicker).setVisibility(View.GONE);
						}
					}
				}

			}
		} catch (Exception ex) {
		
		}
		
		return dpd;
	}

	private void createMonthPicker(View v) {
		int cYear = 0;
		int cMonth = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(mMonthDateFormat);
		Date d;
		DatePickerDialog dpd;
		
		if(v.getId() == R.id.btnFromMonth){
			Calendar c = Calendar.getInstance();
			try {
				d = sdf.parse(btnFromMonth.getText().toString());
				c.setTime(d);
				cMonth = c.get(Calendar.MONTH);
				cYear = c.get(Calendar.YEAR);
				
				dpd = MonthPickerDialog(this.fromMonthListener, cYear, cMonth);
				dpd.show();
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(v.getId() == R.id.btnToMonth){
			Calendar c = Calendar.getInstance();
			try {
				d = sdf.parse(btnToMonth.getText().toString());
				c.setTime(d);
				cMonth = c.get(Calendar.MONTH);
				cYear = c.get(Calendar.YEAR);
				
				dpd = MonthPickerDialog(this.toMonthListener, cYear, cMonth);
				dpd.show();
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	public void pickFromMonth(View v) {
		createMonthPicker(v);
	}

	public void pickToMonth(View v) {
		createMonthPicker(v);
	}

	public void pickFromDate(View v) {
		createDatePickDialog(v);
	}

	public void pickToDate(View v) {
		createDatePickDialog(v);
	}

	// Click ok button
	public void ok(View v) {
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);

		if(this.rgRangeType.getCheckedRadioButtonId() == R.id.rbByDate){
			sdf = new SimpleDateFormat(this.dateFormat);
		}else{
			sdf = new SimpleDateFormat(this.mMonthDateFormat);
		}
		
		try {
			if(this.rgRangeType.getCheckedRadioButtonId() == R.id.rbByDate){
				mFromDate = sdf.parse(this.btnFromDate.getText().toString());
				mToDate = sdf.parse(this.btnToDate.getText().toString());
			}else{
				mFromDate = sdf.parse(this.btnFromMonth.getText().toString());
				mToDate = sdf.parse(this.btnToMonth.getText().toString());
			}
			
			Calendar c = Calendar.getInstance();
			c.setTime(mToDate);
			if(this.rgRangeType.getCheckedRadioButtonId() == R.id.rbByMonth){
				c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			mToDate = c.getTime();
			
			Intent statIntent = new Intent(getApplicationContext(),
					StatisticsActivity.class);
			statIntent.putExtra(MWConstants.FROMDATE, mFromDate.getTime());
			statIntent.putExtra(MWConstants.TODATE, mToDate.getTime());
			
			if(this.mChartType == LINE){
				statIntent.putExtra(MWConstants.CHARTTYPE, LINE);
				statIntent.putExtra(MWConstants.RENDERMETHOD, this.mRenderMethod);
			}else if(this.mChartType == BAR){
				statIntent.putExtra(MWConstants.CHARTTYPE, BAR);
				statIntent.putExtra(MWConstants.RENDERMETHOD, this.mRenderMethod);
			}

			// Return to statistics activity
			setResult(RESULT_OK, statIntent);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		finish();
	}

	// Click cancel button
	public void cancel(View v) {
		finish();
	}

	private OnDateSetListener fromMonthListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);

			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(mMonthDateFormat);

			btnFromMonth.setText(sdf.format(date));
		}

	};

	private OnDateSetListener toMonthListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, monthOfYear);
			c.set(Calendar.DATE, 1);
//			int a = c.get(Calendar.MONTH);
//			int j = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));

			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(mMonthDateFormat);

			btnToMonth.setText(sdf.format(date));
		}

	};

	private OnDateSetListener fromDateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);

			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			btnFromDate.setText(sdf.format(date));
		}

	};

	private OnDateSetListener toDateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);

			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			btnToDate.setText(sdf.format(date));
		}

	};

	private OnCheckedChangeListener rgTypeChangeListener = new OnCheckedChangeListener() {

		private static final int DATE = R.id.rbByDate;
		private static final int MONTH = R.id.rbByMonth;

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == DATE) {
				rlFromToDate.setVisibility(View.VISIBLE);
				rlFromToMonth.setVisibility(View.GONE);
				mRenderMethod = BY_DATE;
				// group.check(R.id.rbByDate);
			} else {
				rlFromToDate.setVisibility(View.GONE);
				rlFromToMonth.setVisibility(View.VISIBLE);
				mRenderMethod = BY_MONTH;
				// group.check(R.id.rbByMonth);
			}
		}

	};
}
