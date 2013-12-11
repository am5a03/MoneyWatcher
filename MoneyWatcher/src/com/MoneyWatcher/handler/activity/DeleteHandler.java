package com.MoneyWatcher.handler.activity;

import com.MoneyWatcher.frm.db.CashFlowAdapter;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.db.DBAdapter;

public class DeleteHandler {
	private DBAdapter adapter;
	
	private long[] _id;
	
	public DeleteHandler(DBAdapter adapter){
		this.adapter = adapter;
	}
	
	public void delete(){
		if(adapter instanceof CashFlowAdapter){
			CashFlowAdapter cfa = (CashFlowAdapter)adapter;
			for(int i = 0; i < _id.length; i++){
				cfa.delete(new String[] { ((Long) _id[i]).toString() });
			}
		}else if(adapter instanceof ConfigAdapter){
			
		}
	}

	public long[] getIds() {
		return _id;
	}

	public void setIds(long[] _id) {
		this._id = _id;
	}
	
}
