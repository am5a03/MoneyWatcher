package com.MoneyWatcher.frm.obj;


public class RecurItem extends CashFlow{
	private String frequency;
	private boolean isEnabled;
	
	public RecurItem(double amount, String categories, String tags, String note, String frequency) {
		super(amount, categories, tags, note);
		this.frequency = frequency;
	}
	
	
	public String getFrequency() {
		return frequency;
	}
	
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}


	public boolean isEnabled() {
		return isEnabled;
	}


	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
