package com.MoneyWatcher.frm.db;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.obj.CashFlowStats;
import com.MoneyWatcher.frm.utils.Utils;

public class StatsAdapter extends DBAdapter{
	
	private CashFlowStats cashFlowStats;
	
	private static final int CATEGORY = 1;
	private static final int SUMAMOUNT = 0; 
	private static final int TIME = 2;
	
	public static final int PIE = 1;
	public static final int LINE = 2;
	public static final int BAR_CAT = 3;
	public static final int BAR_DATE = 4;
	
	private final int BY_DATE = 4;
	private final int BY_MONTH = 5;
	
	private final String mByMonthTimeStr = "'%Y-%m-%d'";
	private final String mByYearTimeStr = "'%Y-%m'";
	
	private int mRenderType;

	public StatsAdapter(String tableName, Context ctx) {
		super(MWConstants.T_CASHFLOW, ctx);
		// TODO Auto-generated constructor stub
	}
	
	public StatsAdapter(Context ctx){
		super(MWConstants.T_CASHFLOW, ctx);
	}

	@Override
	public Cursor selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor select(String[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean persist() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(String[] key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String[] key) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Get stats from DB, group by categories, used by Pie chart and Bar chart
	 * @param isSpending To search for spending or income
	 * @param fromDate From date
	 * @param toDate To Date
	 */
	private void getPIEBARCashFlowStatsFromDB(boolean isSpending, Timestamp fromDate, Timestamp toDate){
		SQLiteDatabase db = DBAdapter.open(getContext());
		StringBuffer sql = new StringBuffer();
		int numOfCategories = 0;
		
		numOfCategories = getNumOfCategories(isSpending, fromDate, toDate);
		
		sql.append("SELECT SUM(" + CashFlowAdapter.cAMOUNT + "), " );
		sql.append(CashFlowAdapter.cCATEGORIES);
		sql.append(" FROM " + getTableName() + " WHERE ");
		if(isSpending){
			sql.append(CashFlowAdapter.cAMOUNT + " <= 0 ");
		}else{
			sql.append(CashFlowAdapter.cAMOUNT + " > 0 ");
		}
		
		if(fromDate != null && toDate != null){
			sql.append(" AND (" + CashFlowAdapter.cCREATETIME);
			sql.append(" BETWEEN '" + fromDate.toString());
			sql.append( "' AND '" + toDate.toString() + "') ");
		}
		
		sql.append( " GROUP BY " + CashFlowAdapter.cCATEGORIES);
		sql.append( "" );
		
		System.out.println(sql.toString());
		
		String[] categories = new String[numOfCategories];
		double[] amount = new double[numOfCategories];
		
		Cursor c1 = db.rawQuery(sql.toString(), null);
		int i = 0;
		
		while(c1.moveToNext()){
			categories[i] = c1.getString(CATEGORY);
			amount[i] = c1.getDouble(SUMAMOUNT);
			i++;
		}
		
		this.cashFlowStats = new CashFlowStats(categories, amount);
		this.cashFlowStats.setNumOfCategories(numOfCategories);
	
	}
	
	private void getLINEBARCashFlowStatsFromDB(boolean isSpending, Timestamp fromDate, Timestamp toDate, int renderType){
		SQLiteDatabase db = DBAdapter.open(getContext());
		StringBuffer sql = new StringBuffer();		
		
		sql.append("SELECT SUM(" + CashFlowAdapter.cAMOUNT + "), ");
		sql.append(CashFlowAdapter.cCATEGORIES + ", ");
		if(renderType == this.BY_DATE){
			sql.append("strftime(").append(this.mByMonthTimeStr).append(",").append(CashFlowAdapter.cCREATETIME).append(") AS DATE");
		}else if(renderType == this.BY_MONTH){
			sql.append("strftime(").append(this.mByYearTimeStr).append(",").append(CashFlowAdapter.cCREATETIME).append(") AS DATE");
		}
		sql.append(" FROM " + getTableName() + " WHERE ");
		
		if(isSpending){
			sql.append(CashFlowAdapter.cAMOUNT + " <= 0 ");
		}else{
			sql.append(CashFlowAdapter.cAMOUNT + " > 0 ");
		}
		
		if(fromDate != null && toDate == null){
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			sql.append(" AND ");
			sql.append(" DATE BETWEEN ");
			if(renderType == this.BY_DATE){
				sql.append(" date('").append(c.get(Calendar.YEAR)).append("-");
				sql.append((c.get(Calendar.MONTH) +1 < 10) ? "0" + (c.get(Calendar.MONTH) + 1) :  c.get(Calendar.MONTH) +1);
				sql.append("-01') ");
				sql.append(" AND ");
				sql.append(" date('").append(c.get(Calendar.YEAR)).append("-");
				sql.append((c.get(Calendar.MONTH) +1 < 10) ? "0" + (c.get(Calendar.MONTH) + 1) :  c.get(Calendar.MONTH) +1);
				sql.append("-").append(c.getActualMaximum(Calendar.DAY_OF_MONTH));
				sql.append("')");
			}else if(renderType == this.BY_MONTH){
				sql.append(" date('").append(c.get(Calendar.YEAR)).append("-01-01') ");
				sql.append(" AND ");
				sql.append(" date('").append(c.get(Calendar.YEAR)).append("-12-31') ");
			}
		}else if(fromDate != null && toDate != null){
			sql.append(" AND " + CashFlowAdapter.cCREATETIME);
			sql.append(" BETWEEN ").append("'" + fromDate.toString() + "' AND ");
			sql.append("'" + toDate.toString() + "'");
		}
		
		sql.append(" GROUP BY DATE, ");
		sql.append(CashFlowAdapter.cCATEGORIES);
//		sql.append(" ORDER BY DATE " + "," + CashFlowAdapter.cCATEGORIES);
		
		ArrayList<Double> _amount = new ArrayList<Double>();
		ArrayList<String> _categories = new ArrayList<String>();
		ArrayList<Long> _time = new ArrayList<Long>();
		
		Cursor c = db.rawQuery(sql.toString(), null);
		int numOfRows = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(this.mRenderType == BY_MONTH){
			sdf = new SimpleDateFormat("yyyy-MM");
		}
		
		while(c.moveToNext()){
			try{
				_amount.add(c.getDouble(SUMAMOUNT));
				_categories.add(c.getString(CATEGORY));
				Date d = sdf.parse(c.getString(TIME));
				_time.add(d.getTime());
				numOfRows++;
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		double[] amount = new double[_amount.size()];
		String[] categories = _categories.toArray(new String[_categories.size()]);
		long[] time = new long[_time.size()];
		
		for(int i = 0; i < numOfRows; i++){
			amount[i] = _amount.get(i).doubleValue();
			time[i] = _time.get(i).longValue();
		}
		
//		HashSet<String> cset = new HashSet<String>();
//		cset.addAll(_categories);
//		categories = cset.toArray(new String[_categories.size()]);
//		
//		int numOfCat = cset.size();
		
		//TODO: Testing code
		this.cashFlowStats = new CashFlowStats(categories, amount, time);
		this.cashFlowStats.setNumOfRow(numOfRows);
		
		if(this.mRenderType == BY_DATE){
			this.cashFlowStats.setFromDate(fromDate.getTime());
			this.cashFlowStats.setToDate(toDate.getTime());
		}else if(this.mRenderType == BY_MONTH){
			this.cashFlowStats.setFromDate(fromDate.getTime());
			this.cashFlowStats.setToDate(toDate.getTime());
		}else{
			Calendar _c = Calendar.getInstance();
			_c = Utils.setFromToDate(_c, true);
			this.cashFlowStats.setFromDate(_c.getTimeInMillis());
			System.out.println(new Date(this.cashFlowStats.getFromDate()));
			
			_c.set(Calendar.DATE, _c.getActualMaximum(Calendar.DAY_OF_MONTH));
			_c = Utils.setFromToDate(_c, false);
			this.cashFlowStats.setToDate(_c.getTimeInMillis());
//			System.out.println(new Date(time[0]));
//			this.cashFlowStats.setFromDate(time[0]);
//			Calendar _c = Calendar.getInstance();
//			
//			_c.set(Calendar.YEAR, 2013);
//			_c.set(Calendar.MONTH, Calendar.MARCH);
//			_c.set(Calendar.DATE, 31);
//			
//			this.cashFlowStats.setToDate(_c.getTimeInMillis());
			
		}
//		
//		this.cashFlowStats.setFromDate(time[0]);
//		Calendar _c = Calendar.getInstance();
//		
//		_c.set(Calendar.YEAR, 2013);
//		_c.set(Calendar.MONTH, Calendar.MARCH);
//		_c.set(Calendar.DATE, 31);
//		
//		this.cashFlowStats.setToDate(_c.getTimeInMillis());
//		this.cashFlowStats.setNumOfCategories(numOfCat);
	}
	
	//Get Bar chart stats, group by categories
	private void getBARCashFlowStatsFromDB(boolean isSpending, Timestamp fromDate, Timestamp toDate){
		
		
	}
	
	
	//Count number of categoreis, used by Pie chart and Bar chart
	private int getNumOfCategories(boolean isSpending, Timestamp fromDate, Timestamp toDate){
		SQLiteDatabase db = DBAdapter.open(getContext());
		StringBuffer sql = new StringBuffer();
		int numOfCategories = 0;
		StringBuffer getNumOfCategorySQL = new StringBuffer();
		
		getNumOfCategorySQL.append("SELECT COUNT(1) FROM ");
		getNumOfCategorySQL.append("(SELECT COUNT(1) FROM " + MWConstants.T_CASHFLOW + " WHERE " + CashFlowAdapter.cAMOUNT);
		if(isSpending){
			getNumOfCategorySQL.append(" <= 0 ");
		}else{
			getNumOfCategorySQL.append(" > 0 ");
		}
		
		if(fromDate != null && toDate != null){
			getNumOfCategorySQL.append(" AND (" + CashFlowAdapter.cCREATETIME);
			getNumOfCategorySQL.append(" BETWEEN '" + fromDate.toString());
			getNumOfCategorySQL.append("' AND '" + toDate.toString() + "') ");
		}
		
		getNumOfCategorySQL.append(" GROUP BY ");
		getNumOfCategorySQL.append(CashFlowAdapter.cCATEGORIES + ")");
		
		
		Cursor c = db.rawQuery(getNumOfCategorySQL.toString(), null);
		if(c.moveToFirst()){
			numOfCategories = c.getInt(0);
		}
		
		return numOfCategories;
	}
	
	/**
	 * 
	 * @param isSpending Boolean value to get distribution for either spending or income
	 */
	public CashFlowStats getCashFlowStats(boolean isSpending, int chartType){
		if(chartType == PIE || chartType == BAR_CAT){
			getPIEBARCashFlowStatsFromDB(isSpending, null, null);
		}		
		return this.cashFlowStats;
	}
	
	/**
	 * 
	 * @param isSpending Boolean value to get distribution for either spending or income
	 * @param fromDate Beginning date
	 * @param toDate End date
	 * @return
	 */
	public CashFlowStats getCashFlowStats(boolean isSpending, Timestamp fromDate, Timestamp toDate, int chartType){
		if(fromDate.compareTo(toDate) > 0){
			Timestamp tmp = toDate;
			toDate = fromDate;
			fromDate = tmp;
		}
		if(chartType == PIE){
			getPIEBARCashFlowStatsFromDB(isSpending, fromDate, toDate);
		}else if(chartType == LINE){
			getLINEBARCashFlowStatsFromDB(isSpending, fromDate, toDate, mRenderType);
		}else if(chartType == BAR_CAT){
			getPIEBARCashFlowStatsFromDB(isSpending, fromDate, toDate);
		}else if(chartType == BAR_DATE){
			getLINEBARCashFlowStatsFromDB(isSpending, fromDate, toDate, mRenderType);
		}
		
		return this.cashFlowStats;
	}
	
	public CashFlowStats getCashFlowStats(){
		return this.cashFlowStats;
	}
	
	public void setCashFlowStats(CashFlowStats cashFlowStats){
		this.cashFlowStats = cashFlowStats;
	}
	
	/**
	 * Set how statistics are collected, by date or by month
	 * @param mRenderType BY_MONTH/BY_DATE
	 */
	public void setRenderType(int mRenderType){
		this.mRenderType = mRenderType;
	}
	
	public int getRenderType(){
		return this.mRenderType;
	}

}
