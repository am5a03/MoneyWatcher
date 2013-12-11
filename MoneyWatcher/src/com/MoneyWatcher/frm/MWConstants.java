package com.MoneyWatcher.frm;

public interface MWConstants {
	public static final String T_CASHFLOW = "tblCashFlow";
	public static final String T_CONFIG = "tblConfig";
	public static final String T_RECUR = "tblRecur";
	
	public static final String ADDCAT = "CATEGORY";
	public static final String ADDTAG = "TAG";
	
	public static final int SPENDING = 0x0;
	public static final int INCOME = 0x1;
	
	public static final String REQUESTCODE = "requestCode";
	public static final String CASHFLOWDATE = "cashFlowDate";
	
	public static final String FROMDATE = "fromDate";
	public static final String TODATE = "toDate";
	
	public static final String CHARTTYPE = "chartType";
	public static final String RENDERMETHOD = "renderMethod"; //By date or by month
	
	public static final String SQLITEDATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String[] CASHFLOWCOLS = {
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

	public static final String[] CONFIGCOLS = {
			"_id", 
			"types",
			"vals"
	};
		
	public static final String[] RECURCOLS = {
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
}
