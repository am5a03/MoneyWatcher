package com.MoneyWatcher.frm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.interfaces.IConfigTypes;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper mInstance = null;

	private static final String DB_NAME = "moneyWatcherDB";
	private static final int DB_VER = 1;
	
	private static final String T_CASHFLOW = "tblCashFlow";
	private static final String T_CONFIG = "tblConfig";
	private static final String T_RECUR = "tblRecur";
	
	private static String DB_CREATE_CF = "";
	private static String DB_CREATE_CONFIG = "";
	private static String DB_CREATE_RECUR = "";
	
	private String[] cashFlowCols = {
		"_id", 
		"amount", 
		"categories", 
		"tags",
		"note",
		"createTime", 
		"updateTime",
		"isRecurItem",
		"recurItemId"
	};
	private String[] cashFlowCs = {
		"integer primary key autoincrement", 
		"integer", 
		"nvarchar(100)",
		"text",
		"nvarchar(300)",
		"timestamp", 
		"timestamp",
		"short integer",
		"integer"
	};
	
	private String[] configCols = {
		"_id", 
		"types",
		"vals"
	};
	private String[] configCs = {
		"integer primary key autoincrement",
		"varchar(10)",
		"varchar(50)"
	};
	
	private String[] recurCols = {
			"_id", 
			"amount", 
			"categories",
			"tags",
			"note",
			"frequency",
			"isEnabled",
			"createTime", 
			"updateTime"
	};
	private String[] recurCs = {
			"integer primary key autoincrement", 
			"integer", 
			"nvarchar(100)",
			"text",
			"nvarchar(300)",
			"text",
			"short integer",
			"timestamp", 
			"timestamp",
	};

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
		// TODO Auto-generated constructor stub
	}

	public synchronized static DatabaseHelper getInstance(Context ctx) {
		if(mInstance == null){
			mInstance = new DatabaseHelper(ctx.getApplicationContext());
		}
		
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		DB_CREATE_CF = generateCreateString(T_CASHFLOW, cashFlowCols, cashFlowCs);
		DB_CREATE_CONFIG = generateCreateString(T_CONFIG, configCols, configCs);
		DB_CREATE_RECUR = generateCreateString(T_RECUR, recurCols, recurCs);
		
		db.execSQL(DB_CREATE_CF);
		db.execSQL(DB_CREATE_CONFIG);
		db.execSQL(DB_CREATE_RECUR);
		
		String[] insertDefaultConfigs = new String[11];
		insertDefaultConfigs[0] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.CATEGORY + "', 'Clothing')");
		insertDefaultConfigs[1] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.CATEGORY + "', 'Dining')");
		insertDefaultConfigs[2] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.CATEGORY + "', 'Transportation')");
		insertDefaultConfigs[3] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.CATEGORY + "', 'Salary')");
		insertDefaultConfigs[4] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.TAG + "', 'Test1')");
		insertDefaultConfigs[5] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.TAG + "', 'Test2')");
		insertDefaultConfigs[6] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.TAG + "', 'Test3')");
		insertDefaultConfigs[7] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.DATE_FORMAT + "', 'dd/MMM/yyyy HH:mm')");
		insertDefaultConfigs[8] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.DAILYTARGET + "', '0')");
		insertDefaultConfigs[9] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.WEEKLYTARGET + "', '0')");
		insertDefaultConfigs[10] = new String("INSERT INTO " + MWConstants.T_CONFIG + " (_id, types, vals) " + " VALUES (null,'" + IConfigTypes.MONTHLYTARGET + "', '0')");
		
		
		for(int i = 0; i < insertDefaultConfigs.length; i++){
			db.execSQL(insertDefaultConfigs[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private String generateCreateString(String tblName, String[] colNames, String[] constraints){
		String _create = "CREATE TABLE IF NOT EXISTS " + tblName + "(";
		
		for(int i = 0; i < colNames.length; i++){
			_create += colNames[i] + " " + constraints[i] + ","; 
		}
		
		_create = _create.substring(0, _create.length() - 1);
		_create += ")";
		
		return _create;
	}
	

}
