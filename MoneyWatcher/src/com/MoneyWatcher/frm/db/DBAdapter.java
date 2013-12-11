package com.MoneyWatcher.frm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.MoneyWatcher.interfaces.IDBAction;

public abstract class DBAdapter implements IDBAction {
	private String tableName;
	private static SQLiteDatabase DB;
	private Context ctx;
	
	protected DBAdapter(String tableName, Context ctx){
		this.tableName = tableName;
		this.ctx = ctx;
	}
	
	protected DBAdapter(String tableName){
		this.tableName = tableName;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public Context getContext(){
		return this.ctx;
	}
	
	public void setTableName(String tableName){
		this.tableName = tableName;
	}
	
	
	public synchronized static SQLiteDatabase open(Context ctx){
		DB = DatabaseHelper.getInstance(ctx).getWritableDatabase();
		return DB; 
	}
	
	public static void close(){
		DB.close();
	}
	
	public String generateInsertStmt(String[] cols, String[] vals){
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO " + tableName + " (");
		
		String prefix = "";
		for(String col : cols){
			sb.append(prefix);
			prefix = ",";
			sb.append(col);
		}
		
		sb.append(") VALUES (null,");
		
		prefix = "";
		for(String val : vals){
			sb.append(prefix);
			prefix = ",";
			sb.append("'" + val + "'");
		}
		
		sb.substring(0, sb.length() - 1);
		sb.append(")");
		
		return sb.toString();
	}
	
}
