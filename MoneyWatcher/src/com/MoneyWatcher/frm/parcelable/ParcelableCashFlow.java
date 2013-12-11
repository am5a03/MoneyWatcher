package com.MoneyWatcher.frm.parcelable;

import java.sql.Timestamp;

import android.os.Parcel;
import android.os.Parcelable;

import com.MoneyWatcher.frm.obj.CashFlow;

public class ParcelableCashFlow implements Parcelable {
	
	private CashFlow cf;
	
	public ParcelableCashFlow(CashFlow cf){
		this.cf = cf;
	}
	
	public ParcelableCashFlow(Parcel in){
		long _id = in.readLong();
		double amount = in.readDouble();
		String categories = in.readString();
		String tags = in.readString();
		String note = in.readString();
		long createTime = in.readLong();
		long updateTime = in.readLong();
		int _isRecurItem = in.readInt();
		boolean isRecurItem = (_isRecurItem == 1) ? true:false;
		long recurId = in.readLong();
		
		
		this.cf = new CashFlow(_id, amount, categories, tags, 
					note, new Timestamp (createTime), new Timestamp (updateTime), isRecurItem, recurId);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(this.cf.getId());
		dest.writeDouble(this.cf.getAmount());
		dest.writeString(this.cf.getCategories());
		dest.writeString(this.cf.getTags());
		dest.writeString(this.cf.getNote());
		dest.writeLong(this.cf.getCreateTime().getTime());
		dest.writeLong(this.cf.getUpdateTime().getTime());
		int _isRecurItem = (this.cf.isRecurItem()) ? 1 : 0;
		dest.writeInt(_isRecurItem);
		dest.writeLong(this.cf.getRecurId());
	}
	
	public CashFlow getCashFlow(){
		return this.cf;
	}
	
	public static final Parcelable.Creator<ParcelableCashFlow> CREATOR = 
			new Parcelable.Creator<ParcelableCashFlow>() {

				@Override
				public ParcelableCashFlow createFromParcel(Parcel source) {
					// TODO Auto-generated method stub
					return new ParcelableCashFlow(source);
				}

				@Override
				public ParcelableCashFlow[] newArray(int size) {
					// TODO Auto-generated method stub
					return new ParcelableCashFlow[size];
				}
			};

}
