package com.MoneyWatcher.frm.obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CashFlowStats {
	private String[] categories;
	private double[] amount;
	private long[] dates;
	
	private int numOfCat;
	private int numOfRow;
	
	private int mActualNumDates; //Get the actual number of days in that month
	private String[] mActualCategories;
	
	private long mFromDate;
	private long mToDate;
	
	public CashFlowStats(){
		super();
	}
	
	public CashFlowStats(String[] categories, double[] amount){
		super();
		this.categories = categories;
		this.amount = amount;
		
		HashMap<String, Integer> catMap = new HashMap<String, Integer>();
		
		for(int i = 0; i < this.categories.length; i++){
			if(!catMap.containsKey(categories[i])){
				catMap.put(categories[i], i);
			}
		}
		ArrayList<String> tmp = new ArrayList<String>();
		int i = 0;
		this.mActualCategories = new String[catMap.size() + 1];
		for(String s : catMap.keySet()){
//			this.mActualCategories[i++]= s;
			tmp.add(s);
		}
		Collections.sort(tmp);
		tmp.add("Total[...]");		
		this.mActualCategories = tmp.toArray(new String[catMap.size() + 1]);
	}
	
	public CashFlowStats(String[] categories, double[] amount, long[] dates){
		this(categories, amount);
		this.dates = dates;
	}

	public double[] getAmount() {
		return this.amount;
	}

	public void setAmount(double[] amount) {
		this.amount = amount;
	}

	public String[] getCategories() {
		return this.categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	
	public String getCategory(int idx){
		try{
			return this.categories[idx];
		}catch (ArrayIndexOutOfBoundsException aiobe){
			return "";
		}
	}
	
	public double getAmount(int idx){
		try{
			return this.amount[idx];
		}catch (ArrayIndexOutOfBoundsException aiobe){
			return -1.0;
		}
	}
	
	public long[] getDates(){
		return this.dates;
	}
	
	public void setDates(long[] dates){
		this.dates = dates;
	}
	
	public long getDate(int idx){
		try{
			return this.dates[idx];
		}catch (ArrayIndexOutOfBoundsException aiobe){
			return -1;
		}
	}

	public int getNumOfCategories() {
		return numOfCat;
	}

	public void setNumOfCategories(int numOfCat) {
		this.numOfCat = numOfCat;
	}

	public int getNumOfRow() {
		return numOfRow;
	}

	public void setNumOfRow(int numOfRow) {
		this.numOfRow = numOfRow;
	}

	public int getActualNumDates() {
		return mActualNumDates;
	}

	public void setActualNumDates(int mActualNumDates) {
		this.mActualNumDates = mActualNumDates;
	}

	public long getToDate() {
		return mToDate;
	}

	public void setToDate(long mToDate) {
		this.mToDate = mToDate;
	}

	public long getFromDate() {
		return mFromDate;
	}

	public void setFromDate(long mFromDate) {
		this.mFromDate = mFromDate;
	}
	
	/**
	 * 
	 * @return the actual unique categories that is found in the cash flow stats
	 */
	public String[] getActualCategories(){		
		return this.mActualCategories;
	}
	
}
