package com.MoneyWatcher.Activity;

//import org.achartengine.chart.PieChart;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.achartengine.GraphicalView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.MoneyWatcher.R;
import com.MoneyWatcher.Activity.Dialog.FromToDateActivity;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.RequestCode;
import com.MoneyWatcher.frm.db.StatsAdapter;
import com.MoneyWatcher.frm.handler.stats.BarChartHandler;
import com.MoneyWatcher.frm.handler.stats.LineChartHandler;
import com.MoneyWatcher.frm.handler.stats.PieChartHandler;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.frm.obj.CashFlowStats;
import com.MoneyWatcher.frm.utils.Utils;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class StatisticsActivity extends SherlockActivity implements
		ActionBar.TabListener {

	private StatsAdapter sa;
	private CashFlowStats cashFlowStats;
	
	private final int TIME_GROUP = 0;
	private final int ALL = 1;
	private final int TIMERANGE = 2;
	
	private final int TYPE_GROUP = 1;
	private final int SPENDING = 3;
	private final int INCOME = 4;
	private final int FILTER = 5;
	private final int VIEW_BYCAT = 6;
	private final int VIEW_BYDATE = 7;
	
	private int BAR_CHART_VIEW_TYPE = VIEW_BYCAT; //By default, it is view by category in this month
	
	private final int PIE = 0;
	private final int LINE = 1;
	private final int BAR = 2;
	
	private final int BY_DATE = 4;
	private final int BY_MONTH = 5;
	
	private boolean[] isCategorySelected;
	private boolean[] isCategorySelectedTmp;
	
	private PieChartHandler mPieChartHandler;
	private LineChartHandler mLineChartHandler;
	private BarChartHandler mBarChartHandler;
	
	private Date mFromDate;
	private Date mToDate;
	
	private int mCashFlowType;
	private int mChartType;
	private int mRenderType = BY_DATE; //By date or by month
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);
		init();

	}

	private void init() {
		this.mCashFlowType = SPENDING;
		setTabs();
	}

	private void setTabs() {
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab pieTab = getSupportActionBar().newTab();
		ActionBar.Tab lineTab = getSupportActionBar().newTab();
		ActionBar.Tab barTab = getSupportActionBar().newTab();

		pieTab.setText("Pie");
		lineTab.setText("Line");
		barTab.setText("Bar");

		pieTab.setTabListener(this);
		lineTab.setTabListener(this);
		barTab.setTabListener(this);

		getSupportActionBar().addTab(pieTab);
		getSupportActionBar().addTab(lineTab);
		getSupportActionBar().addTab(barTab);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu timeMenu = menu.addSubMenu(getResources().getText(R.string.all));
		timeMenu.add(TIME_GROUP, ALL, 0, getResources().getText(R.string.all));
		timeMenu.add(TIME_GROUP, TIMERANGE, 1, getResources().getText(R.string.selectTime));
		
        SubMenu typeMenu = menu.addSubMenu(getResources().getText(R.string.cfType));
        typeMenu.add(TYPE_GROUP, SPENDING, 0, getResources().getText(R.string.spend));
        typeMenu.add(TYPE_GROUP, INCOME, 1, getResources().getText(R.string.income));
        
        if(getSupportActionBar().getSelectedNavigationIndex() == PIE){
        	this.mChartType = PIE;
		}else if(getSupportActionBar().getSelectedNavigationIndex() == LINE){
			this.mChartType = LINE;
			typeMenu.add(TYPE_GROUP, FILTER, 2, "Filter[...]");
			timeMenu.removeItem(ALL);
			timeMenu.add(TIME_GROUP, ALL, 0, "This month");
		}else if(getSupportActionBar().getSelectedNavigationIndex() == BAR){
			this.mChartType = BAR;
			if(BAR_CHART_VIEW_TYPE == VIEW_BYCAT){
				typeMenu.add(TYPE_GROUP, VIEW_BYDATE, 2, "View By Date[...]");
			}else if(BAR_CHART_VIEW_TYPE == VIEW_BYDATE){
				typeMenu.add(TYPE_GROUP, VIEW_BYCAT, 2, "View By Category[...]");
			}
		}

        MenuItem timeMenuItem = timeMenu.getItem();
        timeMenuItem.setIcon(R.drawable.device_access_time);
        timeMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT|MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
        MenuItem typeMenuItem = typeMenu.getItem();
        typeMenuItem.setIcon(R.drawable.abs__ic_menu_moreoverflow_holo_light);
        typeMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT|MenuItem.SHOW_AS_ACTION_IF_ROOM);
       
        

        return super.onCreateOptionsMenu(menu);
    }
	
	private void fillCatSelectedArray(String[] actualCategories){
		isCategorySelected = new boolean[actualCategories.length];
		Arrays.fill(isCategorySelected, true);
	}
	
	private void genPieChart(boolean isSpending){
		this.sa = new StatsAdapter(StatisticsActivity.this);
		if(this.mFromDate == null || this.mToDate == null){
			this.cashFlowStats = this.sa.getCashFlowStats(isSpending, StatsAdapter.PIE);
		}else{
			this.cashFlowStats 
				= this.sa.getCashFlowStats(isSpending, new Timestamp(mFromDate.getTime()), 
						new Timestamp(mToDate.getTime()), StatsAdapter.PIE);
		}
		
		
//		PieChartHandler pch = new PieChartHandler(StatisticsActivity.this);
//		pch.setCashFlowStats(cashFlowStats);
//		
//		if(isSpending){
//			pch.setChartTitle(getResources().getString(R.string.spend));
//		}else{
//			pch.setChartTitle(getResources().getString(R.string.income));
//		}
//		
//		GraphicalView gView = pch.getView();
//		
//		LinearLayout llChart = (LinearLayout) findViewById(R.id.chartLayout);
//		llChart.removeAllViews();
//		llChart.addView(gView);
//		
//		this.mPieChartHandler = pch;
//		fillCatSelectedArray(cashFlowStats.getActualCategories());
	}
	
	private void postPieChart(boolean isSpending){
		PieChartHandler pch = new PieChartHandler(StatisticsActivity.this);
		pch.setCashFlowStats(cashFlowStats);
		
		if(isSpending){
			pch.setChartTitle(getResources().getString(R.string.spend));
		}else{
			pch.setChartTitle(getResources().getString(R.string.income));
		}
		
		GraphicalView gView = pch.getView();
		
		LinearLayout llChart = (LinearLayout) findViewById(R.id.chartLayout);
		llChart.removeAllViews();
		llChart.addView(gView);
		
		this.mPieChartHandler = pch;
	}
	
	private void genLineChart(boolean isSpending){
		this.sa = new StatsAdapter(this);
		if(this.mFromDate != null && this.mToDate != null){
			if(this.mRenderType == BY_DATE){
				this.sa.setRenderType(mRenderType);
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending, new Timestamp(mFromDate.getTime()),
						new Timestamp(mToDate.getTime()), StatsAdapter.LINE);
			}else if(this.mRenderType == BY_MONTH){
				this.sa.setRenderType(mRenderType);
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending, new Timestamp(mFromDate.getTime()),
						new Timestamp(mToDate.getTime()), StatsAdapter.LINE);
			}else{
//				this.cashFlowStats = this.sa.getCashFlowStats(isSpending, StatsAdapter.LINE);
			}		
		}else{
			Calendar c = Calendar.getInstance();
			c = Utils.setFromToDate(c, true);
			Date fromDate = c.getTime();
			
			c = Utils.setFromToDate(c, false);
			Date toDate = c.getTime();	
			this.sa.setRenderType(BY_DATE);
			this.cashFlowStats = this.sa.getCashFlowStats(isSpending, new Timestamp(fromDate.getTime()), 
					new Timestamp(toDate.getTime()), StatsAdapter.LINE);
			
		}
		
	}
	
	private void postLineChart(boolean isSpending){
		LineChartHandler mLineChart = new LineChartHandler(this);
		mLineChart.setStatsAdpater(sa);
		mLineChart.setCashFlowStats(cashFlowStats);
		
		mLineChart.setChartTitle("This month's spending[...]");
		
		LinearLayout llChart = (LinearLayout) findViewById(R.id.chartLayout);
		llChart.removeAllViews();
		GraphicalView gView = mLineChart.getView();
		llChart.addView(gView);	
		fillCatSelectedArray(cashFlowStats.getActualCategories());
		
		this.mLineChartHandler = mLineChart;
	}
	
	private void genBarChart(boolean isSpending){
		this.sa = new StatsAdapter(this);
		
		if(BAR_CHART_VIEW_TYPE == VIEW_BYCAT){
			if (this.mFromDate == null || this.mToDate == null) {
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending,
						StatsAdapter.BAR_CAT);
			} else {
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending,
						new Timestamp(mFromDate.getTime()), new Timestamp(mToDate.getTime()), StatsAdapter.BAR_CAT);
			}
		}else if(BAR_CHART_VIEW_TYPE == VIEW_BYDATE){
			if (this.mFromDate == null || this.mToDate == null) {
				//Show this month's distribution
				Calendar c = Calendar.getInstance();
				c = Utils.setFromToDate(c, true);
				Date fromDate = c.getTime();
				
				c = Utils.setFromToDate(c, false);
				Date toDate = c.getTime();	
				this.sa.setRenderType(BY_DATE);
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending, new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), StatsAdapter.BAR_DATE);
			} else {
				this.sa.setRenderType(mRenderType);
				this.cashFlowStats = this.sa.getCashFlowStats(isSpending,
						new Timestamp(mFromDate.getTime()), new Timestamp(mToDate.getTime()), StatsAdapter.BAR_DATE);
			}
		}
		

		

//		
//		this.cashFlowStats
//			= this.sa.getCashFlowStats(isSpending, new Timestamp(fromDate.getTime()), 
//					new Timestamp(toDate.getTime()), StatsAdapter.BAR_DATE);
//		
//		BarChartHandler bch = new BarChartHandler(this);
//		bch.setStatsAdpater(sa);
//		bch.setCashFlowStats(cashFlowStats);
//		bch.setViewType(BAR_CHART_VIEW_TYPE);
//		GraphicalView gView = bch.getView();
//		
//		LinearLayout llChart = (LinearLayout) findViewById(R.id.chartLayout);
//		llChart.removeAllViews();
//		llChart.addView(gView);
	}
	
	private void postBarChart(boolean isSpending){
		BarChartHandler bch = new BarChartHandler(this);
		bch.setStatsAdpater(sa);
		bch.setCashFlowStats(cashFlowStats);
		bch.setViewType(BAR_CHART_VIEW_TYPE);
		GraphicalView gView = bch.getView();
		
		LinearLayout llChart = (LinearLayout) findViewById(R.id.chartLayout);
		llChart.removeAllViews();
		llChart.addView(gView);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getGroupId() == TIME_GROUP){
			System.out.println("TIME_GROUP");
			if(item.getItemId() == ALL){
				this.mFromDate = null;
				this.mToDate = null;
				if(getSupportActionBar().getSelectedNavigationIndex() == PIE){
					if(this.mCashFlowType == SPENDING){
						new GenPieChart().execute(true);
//						genPieChart(true);
					}else{
						new GenPieChart().execute(false);
//						genPieChart(false);
					}
				}else if(getSupportActionBar().getSelectedNavigationIndex() == LINE){
					if(this.mCashFlowType == SPENDING){
//						genLineChart(true);
						new GenLineChart().execute(true);
					}else{
//						genLineChart(false);
						new GenLineChart().execute(false);
					}
				}
			}else if(item.getItemId() == TIMERANGE){
				System.out.println("TIMERANGE");
				createFromToDateActivity();
			}
		}else if(item.getGroupId() == TYPE_GROUP){
			System.out.println("TYPE_GROUP");
			int type = getSupportActionBar().getSelectedNavigationIndex();
			if(item.getItemId() == SPENDING){
				this.mCashFlowType = SPENDING;
				if(type == PIE){
//					genPieChart(true);
					new GenPieChart().execute(true);
				}else if(type == LINE){
//					genLineChart(true);
					new GenLineChart().execute(true);
				}else if(type == BAR){
//					genBarChart(true);
					new GenBarChart().execute(true);
				}
			}else if(item.getItemId() == INCOME){
				this.mCashFlowType = INCOME;
				if(type == PIE){
//					genPieChart(false);
					new GenPieChart().execute(false);
				}else if(type == LINE){
//					genLineChart(false);
					new GenLineChart().execute(false);
				}else if(type == BAR){
//					genBarChart(false);
					new GenBarChart().execute(false);
				}
			}else if(item.getItemId() == FILTER){
				createFilterDialog();
			}else if(item.getItemId() == VIEW_BYDATE){
				BAR_CHART_VIEW_TYPE = VIEW_BYDATE;
				invalidateOptionsMenu();
				if(this.mCashFlowType == SPENDING){
//					genBarChart(true);
					new GenBarChart().execute(true);
				}else{
//					genBarChart(false);
					new GenBarChart().execute(false);
				}
			}else if(item.getItemId() == VIEW_BYCAT){
				BAR_CHART_VIEW_TYPE = VIEW_BYCAT;
				invalidateOptionsMenu();
				if(this.mCashFlowType == SPENDING){
					new GenBarChart().execute(true);
				}else{
					new GenBarChart().execute(false);
				}
			}
		}
		return true;
	}
	
	//Show date range dialog
	private void createFromToDateActivity(){
		Intent i = new Intent(this, FromToDateActivity.class);
		i.putExtra(MWConstants.CHARTTYPE, this.mChartType);
		if(this.mFromDate != null && this.mToDate != null){
			i.putExtra(MWConstants.FROMDATE, this.mFromDate.getTime());
			i.putExtra(MWConstants.TODATE, this.mToDate.getTime());
		}
		startActivityForResult(i, RequestCode.DATE);
	}
	
	//Return from FromToDate picker
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RequestCode.DATE) {
			if (resultCode == RESULT_OK) {
				if(getSupportActionBar().getSelectedNavigationIndex() == PIE){
					this.mFromDate = new Date(data.getLongExtra(MWConstants.FROMDATE, -1));
					this.mToDate = new Date(data.getLongExtra(MWConstants.TODATE, -1));
					if(this.mCashFlowType == SPENDING){
//						genPieChart(true);
						new GenPieChart().execute(true);
					}else{
//						genPieChart(false);
						new GenPieChart().execute(false);
					}
				}else if(getSupportActionBar().getSelectedNavigationIndex() == LINE){
					this.mFromDate = new Date(data.getLongExtra(MWConstants.FROMDATE, -1));
					this.mToDate = new Date(data.getLongExtra(MWConstants.TODATE, -1));
					this.mRenderType = data.getIntExtra(MWConstants.RENDERMETHOD, -1);
					if(this.mCashFlowType == SPENDING){
//						genLineChart(true);
						new GenLineChart().execute(true);
					}else{
//						genLineChart(false);
						new GenLineChart().execute(false);
					}
				}else if(getSupportActionBar().getSelectedNavigationIndex() == BAR){
					this.mFromDate = new Date(data.getLongExtra(MWConstants.FROMDATE, -1));
					this.mToDate = new Date(data.getLongExtra(MWConstants.TODATE, -1));
					this.mRenderType = data.getIntExtra(MWConstants.RENDERMETHOD, -1);
					if(this.mCashFlowType == SPENDING){
						new GenBarChart().execute(true);
					}else{
						new GenBarChart().execute(false);
					}
				}
			}
		}
	}
	
	//Filter dialog in filtering the line/Bar
	private void createFilterDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Filter[...]");
		
		String[] actualCategories = this.cashFlowStats.getActualCategories();		
		
		isCategorySelectedTmp = new boolean[actualCategories.length];
		System.arraycopy(isCategorySelected, 0, isCategorySelectedTmp, 0, actualCategories.length);
		this.isCategorySelected = this.isCategorySelectedTmp;
		System.arraycopy(isCategorySelectedTmp, 0, isCategorySelected, 0,actualCategories.length);
		
		dialog.setMultiChoiceItems(actualCategories, isCategorySelected, new OnMultiChoiceClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					isCategorySelected[which] = true;
				}else{
					isCategorySelected[which] = false;
				}
			}
		});
		
		dialog.setPositiveButton(R.string.ok, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(getSupportActionBar().getSelectedNavigationIndex() == LINE){
					mLineChartHandler.filterSeries(isCategorySelected);
				}else if(getSupportActionBar().getSelectedNavigationIndex() == BAR){
					
				}
			}
			
		});
		
		dialog.show();

	}
	
	private class GenPieChart extends AsyncTask<Boolean, Void, Boolean>{

		ProgressDialog dialog;

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(StatisticsActivity.this);
			dialog.setTitle("Please wait[...]");
			dialog.setMessage("Searching[...]");
			dialog.show();
			// Toast.makeText(getApplicationContext(), "Searching...",
			// Toast.LENGTH_SHORT).show();
		}

		
		@Override
		protected Boolean doInBackground(Boolean... isSpending) {
			// TODO Auto-generated method stub
			genPieChart(isSpending[0]);
			return isSpending[0];
		}
		
		@Override
		protected void onPostExecute(Boolean isSpending) {
			postPieChart(isSpending);
			dialog.dismiss();
		}
	}
	
	private class GenLineChart extends AsyncTask<Boolean, Void, Boolean>{

		ProgressDialog dialog;

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(StatisticsActivity.this);
			dialog.setTitle("Please wait[...]");
			dialog.setMessage("Searching[...]");
			dialog.show();
			// Toast.makeText(getApplicationContext(), "Searching...",
			// Toast.LENGTH_SHORT).show();
		}

		
		@Override
		protected Boolean doInBackground(Boolean... isSpending) {
			// TODO Auto-generated method stub
			genLineChart(isSpending[0]);
			return isSpending[0];
		}
		
		@Override
		protected void onPostExecute(Boolean isSpending) {
			postLineChart(isSpending);
			dialog.dismiss();
		}
	}
	
	private class GenBarChart extends AsyncTask<Boolean, Void, Boolean>{

		ProgressDialog dialog;

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(StatisticsActivity.this);
			dialog.setTitle("Please wait[...]");
			dialog.setMessage("Searching[...]");
			dialog.show();
			// Toast.makeText(getApplicationContext(), "Searching...",
			// Toast.LENGTH_SHORT).show();
		}

		
		@Override
		protected Boolean doInBackground(Boolean... isSpending) {
			// TODO Auto-generated method stub
			genBarChart(isSpending[0]);
			return isSpending[0];
		}
		
		@Override
		protected void onPostExecute(Boolean isSpending) {
			postBarChart(isSpending);
			dialog.dismiss();
		}
	}
	
	@Override
	public void onTabSelected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
//		System.out.println(tab.getPosition());
		invalidateOptionsMenu();
		if(tab.getPosition() == PIE){
			this.mChartType = PIE;
			if(this.mCashFlowType == SPENDING){
				new GenPieChart().execute(true);
			}else{
				new GenPieChart().execute(false);
			}
		}else if(tab.getPosition() == LINE){
			this.mChartType = LINE;
			if(this.mCashFlowType == SPENDING){
				new GenLineChart().execute(true);
			}else{
				new GenLineChart().execute(false);
			}
		}else if(tab.getPosition() == BAR){
			this.mChartType = BAR;
			if(this.mCashFlowType == SPENDING){
				new GenBarChart().execute(true);
			}else{
				new GenBarChart().execute(false);
			}
		}

	}

	@Override
	public void onTabUnselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab,
			android.support.v4.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
