package com.MoneyWatcher.interfaces;

import android.database.Cursor;

public interface IDBAction {
	public Cursor selectAll();
	public Cursor select(String[] key);
	public boolean persist();
	public boolean update(String[] key);
	public boolean delete(String[] key);
	
}
