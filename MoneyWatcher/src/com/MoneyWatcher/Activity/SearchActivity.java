package com.MoneyWatcher.Activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.RequestCode;
import com.MoneyWatcher.frm.db.CashFlowAdapter;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.frm.parcelable.ParcelableCashFlow;
import com.MoneyWatcher.frm.utils.Utils;
import com.MoneyWatcher.interfaces.ISearchConstants;
import com.actionbarsherlock.app.SherlockActivity;

public class SearchActivity extends SherlockActivity implements OnClickListener,
		ISearchConstants {

	private Button btnSearchFromDate;
	private Button btnSearchToDate;
	private TextView lblSearchCategory;
	private TextView lblSearchTag;
	private Button btnSearchType;
	private Button btnSearchAmountRange;
	private EditText txtSearchAmount;
	private EditText txtSearchNote;
	private Button btnSearchSearch;
	private Button btnSearchCancel;

	private ConfigAdapter ca = new ConfigAdapter(this);
	private String dateFormat;

	private String[] categories = this.ca.getCategories();
	private String[] tags = this.ca.getTags();
	private boolean[] checkedCatIdx = new boolean[categories.length];
	private boolean[] checkedTagIdx = new boolean[tags.length];

	private static final String[] SEARCHTYPE = { SEARCHALL, SEARCHSPENDING,
			SEARCHINCOME };
	private static final String[] SEARCHRANGE = { GE, SE, EQ };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchitem);

		init();
	}

	private void init() {
		setTitle("Search");
		btnSearchFromDate = (Button) findViewById(R.id.btnSearchFromDate);
		btnSearchToDate = (Button) findViewById(R.id.btnSearchToDate);
		lblSearchCategory = (TextView) findViewById(R.id.lblSearchCategory);
		lblSearchTag = (TextView) findViewById(R.id.lblSearchTag);
		btnSearchType = (Button) findViewById(R.id.btnSearchType);
		btnSearchAmountRange = (Button) findViewById(R.id.btnSearchAmountRange);
		txtSearchAmount = (EditText) findViewById(R.id.txtSearchAmount);
		txtSearchNote = (EditText) findViewById(R.id.txtSearchNote);
		btnSearchSearch = (Button) findViewById(R.id.btnSearchSearch);
		btnSearchCancel = (Button) findViewById(R.id.btnSearchCancel);

		btnSearchFromDate.setOnClickListener(this);
		btnSearchToDate.setOnClickListener(this);
		lblSearchCategory.setOnClickListener(this);
		lblSearchTag.setOnClickListener(this);
		btnSearchType.setOnClickListener(this);
		btnSearchAmountRange.setOnClickListener(this);
		btnSearchSearch.setOnClickListener(this);
		btnSearchCancel.setOnClickListener(this);

		this.ca = new ConfigAdapter(this);
		this.dateFormat = this.ca.getDateFormat();

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);

		btnSearchFromDate.setText(sdf.format(date));
		btnSearchToDate.setText(sdf.format(date));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSearchFromDate:
			createDatePicker(v);
			break;
		case R.id.btnSearchToDate:
			createDatePicker(v);
			break;
		case R.id.lblSearchCategory:
			createCatTagDialog(v);
			break;
		case R.id.lblSearchTag:
			createCatTagDialog(v);
			break;
		case R.id.btnSearchType:
			createAmountTypeDialog(v);
			break;
		case R.id.btnSearchAmountRange:
			createAmountTypeDialog(v);
			break;
		case R.id.btnSearchSearch:
			search();
			break;
		case R.id.btnSearchCancel:
			finish();
			break;
		default:
			break;
		}
	}

	private void createDatePicker(View v) {
		int cYear = 0;
		int cMonth = 0;
		int cDate = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(this.dateFormat);
		Date d;

		if (v.getId() == R.id.btnSearchFromDate) {
			try {
				d = sdf.parse(btnSearchFromDate.getText().toString());
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

		} else if (v.getId() == R.id.btnSearchToDate) {
			try {
				d = sdf.parse(btnSearchToDate.getText().toString());
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

	private OnDateSetListener fromDateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);

			Date date = c.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

			btnSearchFromDate.setText(sdf.format(date));
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

			btnSearchToDate.setText(sdf.format(date));
		}

	};

	private void createCatTagDialog(View v) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		if (v.getId() == R.id.lblSearchCategory) {
			dialog.setTitle(R.string.selectCategory);

			dialog.setMultiChoiceItems(categories, checkedCatIdx,
					new OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								checkedCatIdx[which] = true;
							} else {
								checkedCatIdx[which] = false;
							}
						}
					});

			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							ListAdapter dialogLst = ((AlertDialog) dialog)
									.getListView().getAdapter();

							String prefix = "";
							StringBuffer categoryStr = new StringBuffer();
							for (int i = 0; i < categories.length; i++) {
								if (checkedCatIdx[i]) {
									categoryStr.append(prefix);
									prefix = ",";
									categoryStr.append(dialogLst.getItem(i));
								}
							}

							lblSearchCategory.setText(categoryStr);
						}
					});

			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked CANCEL button
							dialog.dismiss();
						}
					});

			dialog.show();

		} else if (v.getId() == R.id.lblSearchTag) {
			dialog.setTitle(R.string.selectTag);

			dialog.setMultiChoiceItems(tags, checkedTagIdx,
					new OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								checkedTagIdx[which] = true;
							} else {
								checkedTagIdx[which] = false;
							}
						}
					});

			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							ListAdapter dialogLst = ((AlertDialog) dialog)
									.getListView().getAdapter();

							String prefix = "";
							StringBuffer tagStr = new StringBuffer();
							for (int i = 0; i < tags.length; i++) {
								if (checkedTagIdx[i]) {
									tagStr.append(prefix);
									prefix = ",";
									tagStr.append(dialogLst.getItem(i));
								}
							}

							lblSearchTag.setText(tagStr);
						}
					});

			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked CANCEL button
							dialog.dismiss();
						}
					});

			dialog.show();

		}
	}

	private void createAmountTypeDialog(View v) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		if (v.getId() == R.id.btnSearchType) {
			dialog.setItems(SEARCHTYPE,
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							ListAdapter dialogLst = ((AlertDialog) dialog)
									.getListView().getAdapter();
							btnSearchType.setText(dialogLst.getItem(which)
									.toString());

							dialog.dismiss();
						}

					});

			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked CANCEL button
							dialog.dismiss();
						}
					});

			dialog.show();

		} else if (v.getId() == R.id.btnSearchAmountRange) {
			dialog.setItems(SEARCHRANGE,
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							ListAdapter dialogLst = ((AlertDialog) dialog)
									.getListView().getAdapter();
							btnSearchAmountRange.setText(dialogLst.getItem(
									which).toString());

							dialog.dismiss();
						}

					});

			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked CANCEL button
							dialog.dismiss();
						}
					});

			dialog.show();
		}
	}

	// Handle Bundle from SearchActivity
	private String genSQL() {
		String fromDate = btnSearchFromDate.getText().toString();
		String toDate = btnSearchToDate.getText().toString();
		String categories = lblSearchCategory.getText().toString();
		String tags = lblSearchTag.getText().toString();
		String type = btnSearchType.getText().toString();
		String range = btnSearchAmountRange.getText().toString();
		String amountStr = txtSearchAmount.getText().toString();
		String note = txtSearchNote.getText().toString();

		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat(this.ca.getDateFormat());
		SimpleDateFormat sdfSql = new SimpleDateFormat(
				MWConstants.SQLITEDATEFORMAT);

		try {
			Date _fromDate = sdf.parse(fromDate);
			Calendar c = Calendar.getInstance();
			Date _toDate = sdf.parse(toDate);
			c.setTime(_toDate);
			// Set the last create time to 23:59:59
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			_toDate = c.getTime();

			fromDate = sdfSql.format(_fromDate);
			toDate = sdfSql.format(_toDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sql.append("SELECT * FROM " + MWConstants.T_CASHFLOW + " WHERE (");
		sql.append(CashFlowAdapter.cCREATETIME + " >= '" + fromDate + "'");
		sql.append(" AND ");
		sql.append(CashFlowAdapter.cCREATETIME + " <= '" + toDate + "')");

		if (!categories.equals("") && !categories.equals(this.getResources().getText(R.string.selectCategories))) {
			sql.append(" AND (");
			String[] _categories = Utils.CSVHandler(categories);
			String or = "";

			for (String category : _categories) {
				sql.append(or);
				sql.append(CashFlowAdapter.cCATEGORIES + "='" + category + "' ");
				or = " OR ";
			}

			sql.append(")");
		}

		if (!tags.equals("") && !tags.equals(this.getResources().getText(R.string.selectTag))) {
			sql.append(" AND (");
			String[] _tags = Utils.CSVHandler(tags);
			String or = "";

			for (String tag : _tags) {
				sql.append(or);
				sql.append(CashFlowAdapter.cTAGS + " LIKE '%" + tag + "%' ");
				or = " OR ";
			}

			sql.append(")");
		}

		if (!amountStr.equals("")) {
			sql.append(" AND (");
			if (type.equals(SEARCHALL)) {
				if (range.equals(GE)) {
					sql.append("abs(" + CashFlowAdapter.cAMOUNT + ")" + " >= "
							+ "abs(" + amountStr + ")");
				} else if (range.equals(EQ)) {
					sql.append("abs(" + CashFlowAdapter.cAMOUNT + ")" + " = "
							+ "abs(" + amountStr + ")");
				} else if (range.equals(SE)) {
					sql.append("abs(" + CashFlowAdapter.cAMOUNT + ")" + " <= "
							+ "abs(" + amountStr + ")");
				}

			} else if (type.equals(SEARCHSPENDING)) {
				sql.append(CashFlowAdapter.cAMOUNT + "< 0 AND ");
				if (range.equals(GE)) {
					sql.append("-" + CashFlowAdapter.cAMOUNT + " >= "
							+ amountStr);
				} else if (range.equals(EQ)) {
					sql.append("-" + CashFlowAdapter.cAMOUNT + " = "
							+ amountStr);
				} else if (range.equals(SE)) {
					sql.append("-" + CashFlowAdapter.cAMOUNT + " <= "
							+ amountStr);
				}

			} else if (type.equals(SEARCHINCOME)) {
				sql.append(CashFlowAdapter.cAMOUNT + "> 0 AND ");
				if (range.equals(GE)) {
					sql.append(CashFlowAdapter.cAMOUNT + " >= " + amountStr);
				} else if (range.equals(EQ)) {
					sql.append(CashFlowAdapter.cAMOUNT + " = " + amountStr);
				} else if (range.equals(SE)) {
					sql.append(CashFlowAdapter.cAMOUNT + " <= " + amountStr);
				}
			}

			sql.append(")");
		}

		if (!note.equals("")) {
			sql.append("AND (");
			sql.append(CashFlowAdapter.cNOTE + " LIKE '%" + note + "%'");
			sql.append(")");
		}
		
		sql.append(" ORDER BY ");
		sql.append(CashFlowAdapter.cCREATETIME);
		sql.append(" DESC ");
		
		return sql.toString();
	}
	
	private void handleCashFlowFromAsyncTask(ArrayList<CashFlow> cfs){
		Bundle bundle = new Bundle();
		
		ArrayList<ParcelableCashFlow> pcfs = new ArrayList<ParcelableCashFlow>();
		
		for(CashFlow cf : cfs){
			pcfs.add(new ParcelableCashFlow(cf));
		}		
		bundle.putParcelableArrayList(CASHFLOW, pcfs);
		Intent searchIntent = new Intent(this, ViewCashFlowActivity.class);
		searchIntent.putExtras(bundle);
		startActivityForResult(searchIntent, RequestCode.OK);
	}
	
	private void search(){
		new SearchTask().execute(new CashFlowAdapter(this));
	}
	
	private class SearchTask extends AsyncTask<CashFlowAdapter, Void, ArrayList<CashFlow>>{
		
		ProgressDialog dialog;
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SearchActivity.this);
			dialog.setTitle("Please wait[...]");
			dialog.setMessage("Searching[...]...");
			dialog.show();
		}

		@Override
		protected ArrayList<CashFlow> doInBackground(CashFlowAdapter... cfas) {
			// TODO Auto-generated method stub
			ArrayList<CashFlow> cfs = cfas[0].getCashFlowFromNativeSQL(genSQL());			
			return cfs;
		}
		
		@Override
		protected void onPostExecute(ArrayList<CashFlow> cfs){
			handleCashFlowFromAsyncTask(cfs);
			dialog.dismiss();
		}
		
	}
}
