package com.MoneyWatcher.frm.obj;

import java.sql.Timestamp;
import java.util.Date;

public class CashFlow {
	private long _id;
	private double amount;
	private String categories;
	private String tags;
	private String note;	
	private Timestamp createTime;
	private Timestamp updateTime;
	private boolean isRecurItem;
	private long recurId;
	
	@Deprecated
	public CashFlow(double amount, String categories, String note){
		this.amount = amount;
		this.categories = categories;
		this.note = note;
		this.createTime = new Timestamp((new Date()).getTime());
		this.updateTime = new Timestamp((new Date()).getTime());
	}
	
	public CashFlow(double amount, String categories, String tags, String note){
		this.amount = amount;
		this.categories = categories;
		this.tags = tags;
		this.note = note;
		this.createTime = new Timestamp((new Date()).getTime());
		this.updateTime = new Timestamp((new Date()).getTime());
	}
	@Deprecated
	public CashFlow(double amount, String categories, String note, Timestamp createTime, Timestamp updateTime){
		this.amount = amount;
		this.categories = categories;
		this.note = note;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	public CashFlow(double amount, String categories, String tags, String note, Timestamp createTime, Timestamp updateTime){
		this.amount = amount;
		this.categories = categories;
		this.tags = tags;
		this.note = note;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	/**
	 * Constructor for marsahlling and unmarshalling
	 * @param _id
	 * @param amount
	 * @param categories
	 * @param tags
	 * @param note
	 * @param createTime
	 * @param updateTime
	 * @param isRecurItem
	 */
	public CashFlow(long _id, double amount, String categories, String tags, String note, Timestamp createTime, Timestamp updateTime, boolean isRecurItem, long recurId){
		this._id = _id;
		this.amount = amount;
		this.categories = categories;
		this.tags = tags;
		this.note = note;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.isRecurItem = isRecurItem;
		this.recurId = recurId;
	}
	
	public double getAmount(){
		return this.amount;
	}
	
	public String getCategories(){
		return this.categories;
	}
	
	public Timestamp getCreateTime(){
		return this.createTime;
	}
	
	public Timestamp getUpdateTime(){
		return this.updateTime;
	}
	
	public String getNote() {
		return note;
	}
	
	public boolean isSpending(){
		return (amount < 0) ? true : false;
	}
	
	public void setAmount(double amount){
		this.amount = amount;
	}
	
	public void setCategories(String categories){
		this.categories = categories;
	}
	
	public void setCreateTime(Timestamp createTime){
		this.createTime = createTime;
	}
	
	public void setUpdateTime(Timestamp updateTime){
		this.updateTime = updateTime;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public long getId() {
		return _id;
	}

	public void setId(long _id) {
		this._id = _id;
	}

	public boolean isRecurItem() {
		return isRecurItem;
	}

	public void setRecurItem(boolean isRecurItem) {
		this.isRecurItem = isRecurItem;
	}

	public long getRecurId() {
		return recurId;
	}

	public void setRecurId(long recurId) {
		this.recurId = recurId;
	}
	
	
}
