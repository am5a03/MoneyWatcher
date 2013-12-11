package com.MoneyWatcher.frm.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.graphics.Color;

public class Utils {
	public static String[] CSVHandler(String s){
		String[] _res;
		try{
			 _res = s.split(",");
			return _res;
		}catch(Exception e){
			_res = new String[1];
			_res[0] = s;
			return _res;
		}
	}
	
	/**
	 * Calculate the difference in dates
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static long dateDiff(long fromDate, long toDate){
		long DAY_IN_MILLISECOND = 86400000;
		return ((toDate - fromDate)/DAY_IN_MILLISECOND);
		
	}
	
	/**
	 * Calculate the difference in months
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static int monthDiff(long fromDate, long toDate){
		Date f = new Date(fromDate);
		Date t = new Date(toDate);
		
		
		int _fromDate = f.getYear() * 12 + f.getMonth();
		int _toDate = t.getYear() * 12 + t.getMonth();
		
		return _toDate - _fromDate + 1;
	}
	
	public static int randomColorGenerator(){
		Random rnd = new Random();
		int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
						rnd.nextInt(256));
		return color;
	}
	
	public static Calendar setFromToDate(Calendar c, boolean isFromDate){		
		if(isFromDate){
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		}else{
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 0);
		}
		return c;
	}
	
//	public static String concatStringWithKey(String[] s, String key){
//		String prefix = "";
//		StringBuffer sb = new StringBuffer();
//		
//		for(String _s : s){
//			sb.append(prefix);
//			sb.append(_s);
//			prefix = key;
//		}
//		
//		return sb.toString();
//	}
}
