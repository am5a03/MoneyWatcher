package com.MoneyWatcher.frm.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.obj.MConfig;
import com.MoneyWatcher.interfaces.IConfigFunctions;
import com.MoneyWatcher.interfaces.IConfigTypes;

public class ConfigAdapter extends DBAdapter implements IConfigTypes, IConfigFunctions{

	private MConfig mConfig;
	
	private static final String c_ID = MWConstants.CONFIGCOLS[0];
	private static final String cTYPES = MWConstants.CONFIGCOLS[1];
	private static final String cVALS = MWConstants.CONFIGCOLS[2];
	
	private static final int c_IDIDX = 0x0;
	private static final int cTYPESIDX = 0x1;
	private static final int cVALIDX = 0x2;
	
	private SQLiteDatabase db;
	
	public ConfigAdapter(MConfig mConfig, Context ctx){
		super(MWConstants.T_CONFIG, ctx);
		this.mConfig = mConfig;
	}
	
	public ConfigAdapter(Context ctx){
		super(MWConstants.T_CONFIG, ctx);
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
		
		
		String whereClause = cTYPES + "= ?";
		String[] whereArgs = key;
		
		Cursor c = db.query(getTableName(), null, whereClause, whereArgs, null, null, cVALS + " COLLATE NOCASE");
		return c;
	}

	@Override
	public boolean persist() {
		// TODO Auto-generated method stub
		db = DBAdapter.open(getContext());
		
		String types = mConfig.getTypes();
		String _vals = mConfig.getVals();
		
		String[] vals = {types, _vals};
		
		String sql = generateInsertStmt(MWConstants.CONFIGCOLS, vals);
		
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
	
	@Override
	public String[] getCategories(){
		Cursor c = this.select(new String[]{IConfigTypes.CATEGORY});
		ArrayList<String> category = new ArrayList<String>();
		String[] _categories;
		
		while(c.moveToNext()){
//			System.out.println(c.getString(cVALIDX));
			category.add(c.getString(cVALIDX));
		}
		
		_categories = category.toArray(new String[category.size()]);
		
		c.close();
		return _categories;
		
	}
	

	@Override
	public String getDateFormat() {
		// TODO Auto-generated method stub
		String dateTimeFormat = getDateTimeFormat();
		
		return dateTimeFormat.split(" ")[0];
	}
	
	@Override
	public String getDateTimeFormat() {
		// TODO Auto-generated method stub
		
		Cursor c = this.select(new String[]{IConfigTypes.DATE_FORMAT});
		String dateFormat = "";
		
		if(c.moveToNext()){
			dateFormat = c.getString(cVALIDX);
		}
		
		c.close();
		
		return dateFormat;
	}

	@Override
	public String getTimeFormat() {
		// TODO Auto-generated method stub
		String dateTimeFormat = getDateTimeFormat();
		
		return dateTimeFormat.split(" ")[1];
	}

	@Override
	public double getMonthlyTarget() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWeeklyTarget() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDailyTarget() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getTags() {
		// TODO Auto-generated method stub
		Cursor c = this.select(new String[]{IConfigTypes.TAG});
		ArrayList<String> tag = new ArrayList<String>();
		String[] _tags;
		
		while(c.moveToNext()){
//			System.out.println(c.getString(cVALIDX));
			tag.add(c.getString(cVALIDX));
		}
		c.close();
		_tags = tag.toArray(new String[tag.size()]);
		return _tags;
	}

	

}
