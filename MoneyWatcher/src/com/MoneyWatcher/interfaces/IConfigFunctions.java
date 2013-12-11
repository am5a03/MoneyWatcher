package com.MoneyWatcher.interfaces;

public interface IConfigFunctions {
	public String[] getCategories();
	public String[] getTags();
	public String getDateFormat();
	public String getDateTimeFormat();
	public String getTimeFormat();
	public double getMonthlyTarget();
	public double getWeeklyTarget();
	public double getDailyTarget();
}
