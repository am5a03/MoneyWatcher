package com.MoneyWatcher.frm.db;

import java.sql.Timestamp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.frm.obj.RecurItem;

public class RecurItemAdapter extends CashFlowAdapter {
	
	private RecurItem ri;
	
	public RecurItemAdapter(CashFlow cf, Context ctx) {
		super(cf, ctx);
		this.setTableName(MWConstants.T_RECUR);
		this.ri = (RecurItem) cf;
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
		
		SQLiteDatabase db = DBAdapter.open(getContext());
		double amount = ri.getAmount();
		String categories = ri.getCategories();
		String tags = ri.getTags();
		String note = ri.getNote();
		boolean isEnabled = ri.isEnabled();
		String freq = ri.getFrequency();
		Timestamp createTime = ri.getCreateTime();
		Timestamp updateTime = ri.getUpdateTime();		
		
		String[] vals = {((Double)amount).toString(), categories, tags, note, freq, createTime.toString(), updateTime.toString()};
		String sql = generateInsertStmt(MWConstants.RECURCOLS, vals);
		
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
		return false;
	}

	@Override
	public boolean delete(String[] key) {
		// TODO Auto-generated method stub
		return false;
	}

}
