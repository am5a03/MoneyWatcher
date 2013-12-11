package com.MoneyWatcher.interfaces;

import java.util.ArrayList;
import java.util.Date;

import com.MoneyWatcher.frm.obj.CashFlow;

public interface ICashFlowFunctions {
	public ArrayList<CashFlow> getCashFlowByDate(Date from, Date to);
	public ArrayList<CashFlow> getCashFlowByDate(Date date, boolean isBefore);
	public ArrayList<CashFlow> getCashFlowByNote(String note);
	public ArrayList<CashFlow> getCashFlowByAmount(double amount, boolean isGreaterThan);
	public ArrayList<CashFlow> getCashFlowFromNativeSQL(String sql);
	public double getTotalSpendingByDate(Date from, Date to);
	public double getTotalIncomeByDate(Date from, Date to);
	public double getNetCashFlowByDate(Date from, Date to);
	public double getNetCashFlowByDateAndCategory(Date from, Date to, String category);
	public CashFlow getCashFlowById(String id);
}
