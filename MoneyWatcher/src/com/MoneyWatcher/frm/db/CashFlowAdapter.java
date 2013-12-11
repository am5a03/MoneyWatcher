package com.MoneyWatcher.frm.db;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.interfaces.ICashFlowFunctions;

public class CashFlowAdapter extends DBAdapter implements ICashFlowFunctions{
	public static final String c_ID = MWConstants.CASHFLOWCOLS[0];
	public static final String cAMOUNT = MWConstants.CASHFLOWCOLS[1];
	public static final String cCATEGORIES = MWConstants.CASHFLOWCOLS[2];
	public static final String cTAGS = MWConstants.CASHFLOWCOLS[3];
	public static final String cNOTE = MWConstants.CASHFLOWCOLS[4];	
	public static final String cCREATETIME = MWConstants.CASHFLOWCOLS[5];
	public static final String cUPDATETIME = MWConstants.CASHFLOWCOLS[6];
	public static final String cISRECURITEM = MWConstants.CASHFLOWCOLS[7];
	public static final String cRECURITEMID = MWConstants.CASHFLOWCOLS[8];
	
	private static final int c_IDIDX = 0x0;
	private static final int cAMOUNTIDX = 0x1;
	private static final int cCATEGORIESIDX = 0x2;
	private static final int cTAGSIDX = 0x3;
	private static final int cNOTEIDX = 0x4;
	private static final int cCRATETIMEIDX = 0x5;
	private static final int c_UPDATETIMEIDX = 0x6;
	private static final int c_ISRECURITEMIDX = 0x7;
	private static final int c_RECURITEMID = 0x8;
	
	
	private CashFlow cf;
	
	private SQLiteDatabase db;

	public CashFlowAdapter(CashFlow cf, Context ctx){
		super(MWConstants.T_CASHFLOW, ctx);
		this.cf = cf;
	}
	
	public CashFlowAdapter(Context ctx){
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
		db = DBAdapter.open(getContext());
		
		String whereClause = c_ID + " = ?";
		
		Cursor c = db.query(getTableName(), null, whereClause, key, null, null, null);
		return c;
	}

	@Override
	public boolean persist() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = DBAdapter.open(getContext());
		double amount = cf.getAmount();
		String categories = cf.getCategories();
		String tags = cf.getTags();
		String note = cf.getNote();
		Timestamp createTime = cf.getCreateTime();
		Timestamp updateTime = cf.getUpdateTime();		
		boolean isRecurItem = cf.isRecurItem();
		
		int _isRecurItem = (isRecurItem) ? 1: 0;
		long recurId = cf.getRecurId();
		
		
		String[] vals = {((Double)amount).toString(), categories, 
							tags, note, createTime.toString(), updateTime.toString(), 
							((Integer)_isRecurItem).toString(), ((Long)recurId).toString()};
		String sql = generateInsertStmt(MWConstants.CASHFLOWCOLS, vals);
		
		try{
			db.execSQL(sql);
			return true;
		}catch(SQLException e){
			return false;
		}		
	}

	@Override
	public boolean update(String[] key) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = DBAdapter.open(getContext());
		double amount = cf.getAmount();
		String categories = cf.getCategories();
		String tags = cf.getTags();
		String note = cf.getNote();
		Timestamp createTime = cf.getCreateTime();
		Timestamp updateTime = cf.getUpdateTime();		
		boolean isRecurItem = cf.isRecurItem();
		
		int _isRecurItem = (isRecurItem) ? 1: 0;
		
		ContentValues cv = new ContentValues();
		
		cv.put(cAMOUNT, amount);
		cv.put(cCATEGORIES, categories);
		cv.put(cTAGS, tags);
		cv.put(cNOTE, note);
		cv.put(cCREATETIME, createTime.toString());
		cv.put(cUPDATETIME, updateTime.toString());
		cv.put(cISRECURITEM, _isRecurItem);
		
		int rowAffected = db.update(getTableName(), cv, c_ID + " = ?", key);
		
		if(rowAffected > 0){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean delete(String[] key) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = DBAdapter.open(getContext());
		String whereClause = "_id = ?";		
		if (db.delete(getTableName(), whereClause, key) != 0){
			return true;
		}
		return false;
	}
	
	private ArrayList<CashFlow> getCashFlowsFromCursor(Cursor c){
		ArrayList<CashFlow> cfs = new ArrayList<CashFlow>();
		while(c.moveToNext()){
			long _id = c.getLong(c_IDIDX);
			double amount = c.getDouble(cAMOUNTIDX);
			String categories = c.getString(cCATEGORIESIDX);
			String tags = c.getString(cTAGSIDX);
			String note = c.getString(cNOTEIDX);
			int _isReucrItem = c.getInt(c_ISRECURITEMIDX);
			boolean isRecurItem = (_isReucrItem == 0) ? false : true;
			
			long recurItemId = c.getLong(c_RECURITEMID);
			
			
			
			Timestamp createTime = Timestamp.valueOf(c.getString(cCRATETIMEIDX));
			Timestamp updateTime = Timestamp.valueOf(c.getString(c_UPDATETIMEIDX));
			CashFlow cf = new CashFlow(_id, amount, categories, tags, note, createTime, updateTime, isRecurItem, recurItemId);
			
			cf.setId(c.getLong(c_IDIDX));
			
			cfs.add(cf);
		}
		c.close();
		return cfs;
	}

	@Override
	public ArrayList<CashFlow> getCashFlowByDate(Date from, Date to) {
		// TODO Auto-generated method stub
		
		db = DBAdapter.open(getContext());
		
		String whereClause = cCREATETIME + " BETWEEN ? AND ?";
		SimpleDateFormat sdf = new SimpleDateFormat(MWConstants.SQLITEDATEFORMAT);
		String _from = sdf.format(from);
		String _to = sdf.format(to);
		
		
		String[] whereArgs = {_from, _to};
		
		Cursor c = db.query(getTableName(), null, whereClause, whereArgs, null, null, cCREATETIME + " DESC");
		
		return getCashFlowsFromCursor(c);
	}

	@Override
	public ArrayList<CashFlow> getCashFlowByDate(Date date, boolean isBefore) {
		// TODO Auto-generated method stub
		db = DBAdapter.open(getContext());
		
		String whereClause = "";
		SimpleDateFormat sdf = new SimpleDateFormat(MWConstants.SQLITEDATEFORMAT);
		String _date = sdf.format(date);
		String[] whereArgs = {_date}; 
		if(isBefore){
			whereClause = cCREATETIME + " <= ?";
		}else{
			whereClause = cCREATETIME + " >= ?";
		}
		
		Cursor c = db.query(getTableName(), null, whereClause, whereArgs, null, null, cCREATETIME + " DESC");
		
		return getCashFlowsFromCursor(c);
	}

	@Override
	public ArrayList<CashFlow> getCashFlowByNote(String note) {
		// TODO Auto-generated method stub
		db = DBAdapter.open(getContext());
		
		String whereClause = cNOTE + " LIKE ?";
		String[] whereArgs = {note + "%"};
		
		Cursor c = db.query(getTableName(), null, whereClause, whereArgs, null, null, cNOTE);
		
		return getCashFlowsFromCursor(c);
	}

	@Override
	public ArrayList<CashFlow> getCashFlowByAmount(double amount, boolean isGreaterThan) {
		// TODO Auto-generated method stub
		db = DBAdapter.open(getContext());
		
		String whereClause = "";
		
		if(isGreaterThan){
			whereClause = cAMOUNT + " >= ?";
		}else{
			whereClause = cAMOUNT + " <= ?";
		}
		
		String[] whereArgs = {((Double)amount).toString()};
		
		Cursor c = db.query(getTableName(), null, whereClause, whereArgs, null, null, cAMOUNT);
		
		return getCashFlowsFromCursor(c);
	}
	
	public ArrayList<CashFlow> getAllCashFlow(){
		db = DBAdapter.open(getContext());
		Cursor c = db.query(getTableName(), null, null, null, null, null, cCREATETIME + " DESC");
		
		return getCashFlowsFromCursor(c);
	}

	@Override
	public double getTotalSpendingByDate(Date from, Date to) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalIncomeByDate(Date from, Date to) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNetCashFlowByDate(Date from, Date to) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNetCashFlowByDateAndCategory(Date from, Date to,
			String category) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<CashFlow> getCashFlowFromNativeSQL(String sql) {
		// TODO Auto-generated method stub
		db = DBAdapter.open(getContext());
		
		Cursor c = db.rawQuery(sql, null);
		return getCashFlowsFromCursor(c);
	}

	@Override
	public CashFlow getCashFlowById(String id) {
		// TODO Auto-generated method stub
		Cursor c = this.select(new String[] {id});
		CashFlow _cf;
		if(c.moveToFirst()){
			SimpleDateFormat sdf = new SimpleDateFormat(MWConstants.SQLITEDATEFORMAT);
			double amount = c.getDouble(cAMOUNTIDX);
			String categories = c.getString(cCATEGORIESIDX);
			String tags = c.getString(cTAGSIDX);
			String note = c.getString(cNOTEIDX);
			String createTime = c.getString(cCRATETIMEIDX);
			
			try {
				Timestamp _createtime = new Timestamp(sdf.parse(createTime).getTime());
				_cf = new CashFlow(amount, categories, tags, note, _createtime, null);
				c.close();
				return _cf;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				c.close();
			}
			
		}
		c.close();
		return new CashFlow(0, null, null);
	}
	
	

}
