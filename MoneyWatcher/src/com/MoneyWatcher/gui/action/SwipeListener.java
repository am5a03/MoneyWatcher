package com.MoneyWatcher.gui.action;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ListView;

public class SwipeListener extends SimpleOnGestureListener {
	private ListView list;
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	
	public void setListView(ListView list){
		this.list = list;
	}
	
	
}
