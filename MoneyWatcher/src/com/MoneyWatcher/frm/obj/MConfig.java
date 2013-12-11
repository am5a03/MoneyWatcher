package com.MoneyWatcher.frm.obj;

public class MConfig {
	private String types;
	private String vals;
	
	public MConfig(String types, String vals){
		this.types = types;
		this.vals = vals;
	}	
	
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getVals() {
		return vals;
	}
	public void setVals(String vals) {
		this.vals = vals;
	}
}
