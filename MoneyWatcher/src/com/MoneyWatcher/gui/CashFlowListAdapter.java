package com.MoneyWatcher.gui;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.obj.CashFlow;

public class CashFlowListAdapter extends BaseAdapter implements SectionIndexer, Filterable {

	private ArrayList<CashFlow> cfs;
	private ArrayList<CashFlow> dividedCfs;
	private ArrayList<CashFlow> cloneCfs;
	private ArrayList<Timestamp> sections;
	private HashMap<Timestamp, Integer> mCreateTime;
	private final int FORCE_UPDATE_MODE = -1;
	private final int TYPE_SEPARATOR = -1;
	private final int TYPE_MAX_COUNT = 2;
	private final int TYPE_ITEM = 0;
	private String dateTimeFormat;
	private String timeFormat;
	private SimpleDateFormat sdf;
	private SimpleDateFormat sdf2; //time
	//The real Item position, excluding the separator
	private int[] itemIds;
	private boolean[] isSelected;
	//Whether checkbox is visible
	private int isVisible = View.INVISIBLE;
	private int SELECTEDCOUNTER;
	private boolean isAllSelected;
	
	private String[] DAY_OF_WEEK;
	
	private CashFlowFilter cfFilter;
	
	Context ctx;
	
	
	
	public CashFlowListAdapter(ArrayList<CashFlow> cfs, Context ctx){
		this.cfs = cfs;
		this.ctx = ctx;
		ConfigAdapter ca = new ConfigAdapter(ctx);
		this.dateTimeFormat = ca.getDateFormat();
		this.timeFormat = ca.getTimeFormat();
		this.mCreateTime = new HashMap<Timestamp, Integer>();
		this.sections = new ArrayList<Timestamp>();
		this.sdf = new SimpleDateFormat(this.dateTimeFormat);
		this.sdf2 = new SimpleDateFormat(this.timeFormat); 
		init();
		DAY_OF_WEEK = ctx.getResources().getStringArray(R.array.dayofweek);
	}
	
	
	static class ViewHolder{
		TextView lblItemCategories;
		TextView lblItemAmount;
		TextView lblItemTags;
		TextView lblItemNote;
		TextView lblCashFlowDate;
		TextView lblCashFlowDay;
		TextView lblItemTime;
		CheckBox cbxItem;
//		TextView lblItemId;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.cfs.size();
	}

	@Override
	public Object getItem(int idx) {
		// TODO Auto-generated method stub
		return this.cfs.get(idx);
	}

	@Override
	public long getItemId(int idx) {
		// TODO Auto-generated method stub
		return idx;
	}
	
	@Override
	public boolean isEnabled(int position){ 
		return (this.cfs.get(position).getId() == TYPE_SEPARATOR) ? false : true;
	}
	
	@Override
	public int getItemViewType(int position){
		return (this.cfs.get(position).getId() == TYPE_SEPARATOR) ? TYPE_SEPARATOR : TYPE_ITEM;
	}
	
	@Override
	public int getViewTypeCount(){
		return TYPE_MAX_COUNT;
	}
	
	@Override
	public View getView(int idx, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewHolder viewHolder;
		int type = getItemViewType(idx);
		
		if(v == null){
			viewHolder = new ViewHolder();
			switch(type){
			case TYPE_SEPARATOR:
				v = vi.inflate(R.layout.list_cash_flow_sep, null);
				viewHolder.lblCashFlowDate = (TextView)v.findViewById(R.id.lblCashFlowDate);
				viewHolder.lblCashFlowDay = (TextView)v.findViewById(R.id.lblCashFlowDay);
				break;
			case TYPE_ITEM:
				v = vi.inflate(R.layout.list_cash_flow_item, null);
				viewHolder.lblItemCategories = (TextView)v.findViewById(R.id.lblItemCategories);
				viewHolder.lblItemAmount = (TextView)v.findViewById(R.id.lblItemAmount);
				viewHolder.lblItemTags = (TextView)v.findViewById(R.id.lblItemTags);
				viewHolder.lblItemNote = (TextView)v.findViewById(R.id.lblItemNote);
				viewHolder.lblItemTime = (TextView)v.findViewById(R.id.lblItemTime);
				viewHolder.cbxItem = (CheckBox)v.findViewById(R.id.cbxItem);
				viewHolder.cbxItem.setOnCheckedChangeListener(checkBoxListener);
//				viewHolder.lblItemId = (TextView)v.findViewById(R.id.lblItemId);
				break;
			}
			v.setTag(viewHolder);
		}else{
//			int colorPos = idx % colors.length;
//			v.setBackgroundColor(colors[colorPos]);
			viewHolder = (ViewHolder) v.getTag();
		}
		CashFlow cf = this.cfs.get(idx);
		String formattedDate = sdf.format(cf.getCreateTime());
		
		switch(type){
		case TYPE_SEPARATOR:
//			String s = handleDate(cf.getCreateTime());
			Calendar c = Calendar.getInstance();
			c.setTime(cf.getCreateTime());
			viewHolder.lblCashFlowDate.setText(formattedDate);
			viewHolder.lblCashFlowDay.setText(DAY_OF_WEEK[c.get(Calendar.DAY_OF_WEEK) - 1]);
			break;
		case TYPE_ITEM:
		    viewHolder.cbxItem.setTag(idx);
			cf = this.cfs.get(idx);
			String _categories = cf.getCategories();
			boolean isSpending = cf.isSpending();
			String _tags = cf.getTags();
			String _amount = ((Double)cf.getAmount()).toString();
			String _note = cf.getNote();
			String formattedTime = sdf2.format(cf.getCreateTime());
			
			viewHolder.lblItemCategories.setText((_categories == null) ? "" : _categories);
			viewHolder.lblItemTags.setText((_tags == null) ? "" : _tags);
			viewHolder.lblItemAmount.setText((_amount == null) ? "$0.0" : "$" + _amount);
			viewHolder.lblItemTime.setText(formattedTime);
//			viewHolder.lblItemId.setText("DEBUG: " + getItemIds(idx));
			viewHolder.cbxItem.setChecked(isSelected[idx]);
			viewHolder.cbxItem.setVisibility(isVisible);
			if(isSpending){
				viewHolder.lblItemAmount.setTextColor(Color.parseColor("#ff4444"));
			}else{
				viewHolder.lblItemAmount.setTextColor(Color.parseColor("#99cc00"));
			}
			
			viewHolder.lblItemNote.setText((_note == null) ? "" : _note);
		}
		
		return v;
	}
	
	private void init(){
		
		boolean isSeparatorAdded = false;
		this.dividedCfs = new ArrayList<CashFlow>();
		Calendar cfCalendar = Calendar.getInstance();
		Calendar nxtCfCalendar = Calendar.getInstance();
		
		int cfDate;
		int cfMonth;
		int cfYear;
		
		int nxtCfDate;
		int nxtCfMonth;
		int nxtCfYear;
		
		int sectionCount = 0; //Count number of separators created
		
		if(this.cfs.size() == 1){
			this.dividedCfs.add(new CashFlow(TYPE_SEPARATOR, 0, null, null, null, 
					cfs.get(0).getCreateTime(), null, false, TYPE_SEPARATOR));
			this.dividedCfs.add(cfs.get(0));
			this.sections.add(cfs.get(0).getCreateTime());
			this.mCreateTime.put(cfs.get(0).getCreateTime(), 0);
		}else{
			for(int i = 0; i < this.cfs.size(); i++){
				if( i+1 != this.cfs.size()){
					cfCalendar.setTime(cfs.get(i).getCreateTime());
					nxtCfCalendar.setTime(cfs.get(i + 1).getCreateTime());
					
					cfDate = cfCalendar.get(Calendar.DATE);
					cfMonth = cfCalendar.get(Calendar.MONTH);
					cfYear = cfCalendar.get(Calendar.YEAR);
					
					nxtCfDate = nxtCfCalendar.get(Calendar.DATE);
					nxtCfMonth = nxtCfCalendar.get(Calendar.MONTH);
					nxtCfYear = nxtCfCalendar.get(Calendar.YEAR);
					
					if(!isSeparatorAdded){
						this.dividedCfs.add(new CashFlow(TYPE_SEPARATOR, 0, null, null, null, 
								cfs.get(i).getCreateTime(), null, false, TYPE_SEPARATOR));
						isSeparatorAdded = true;
						this.mCreateTime.put(cfs.get(i).getCreateTime(), sectionCount++);
						this.sections.add(this.cfs.get(i).getCreateTime());
					}
					
					this.dividedCfs.add(this.cfs.get(i));
					
					//If current cash flow date != next cash flow date, add a new separator
					if(cfDate != nxtCfDate || cfMonth != nxtCfMonth || cfYear != nxtCfYear){
						isSeparatorAdded = false;
					}
				}else{
					cfCalendar.setTime(cfs.get(i).getCreateTime());
					nxtCfCalendar.setTime(cfs.get(i-1).getCreateTime());
					
					cfDate = cfCalendar.get(Calendar.DATE);
					cfMonth = cfCalendar.get(Calendar.MONTH);
					cfYear = cfCalendar.get(Calendar.YEAR);
					
					
					nxtCfDate = nxtCfCalendar.get(Calendar.DATE);
					nxtCfMonth = nxtCfCalendar.get(Calendar.MONTH);
					nxtCfYear = nxtCfCalendar.get(Calendar.YEAR);
					
					if(!isSeparatorAdded){
						this.dividedCfs.add(new CashFlow(TYPE_SEPARATOR, 0, null, null, null, 
								cfs.get(i).getCreateTime(), null, false, TYPE_SEPARATOR));
						isSeparatorAdded = true;
						this.mCreateTime.put(cfs.get(i).getCreateTime(), sectionCount++);
						this.sections.add(this.cfs.get(i).getCreateTime());
						
					}
					
					this.dividedCfs.add(this.cfs.get(i));
					
					//If current cash flow date != next cash flow date, add a new separator
					if(cfDate != nxtCfDate || cfMonth != nxtCfMonth || cfYear != nxtCfYear){
						isSeparatorAdded = false;
					}
				}
			}
		}
		if(this.cloneCfs == null){
			this.cloneCfs = (ArrayList<CashFlow>) this.cfs.clone();
		}
		this.cfs = this.dividedCfs;
		fillOriginalPosition(this.cfs);
		
//		System.out.println(this.dividedCfs);
	}
	
	//Change to a customized date
	private String handleDate(Date d){
		Calendar c = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		
		String today = this.ctx.getResources().getString(R.string.today);
		String yesterday = this.ctx.getResources().getString(R.string.yesterday);
		
		int cYear = c.get(Calendar.YEAR);
		int cMonth = c.get(Calendar.MONTH);
		int cDate = c.get(Calendar.DATE);
		
		int sepYear, sepMonth, sepDate;
		
		c1.setTime(d);
		
		sepYear = c1.get(Calendar.YEAR);
		sepMonth = c1.get(Calendar.MONTH);
		sepDate = c1.get(Calendar.DATE);
		
		if(sepYear == cYear && sepMonth == cMonth && sepDate == cDate){
			return today;
		}else{
			c.add(Calendar.DAY_OF_YEAR, -1);
			cYear = c.get(Calendar.YEAR);
			cMonth = c.get(Calendar.MONTH);
			cDate = c.get(Calendar.DATE);
			if(sepYear == cYear && sepMonth == cMonth && sepDate == cDate){
				return yesterday;
			}else{
				return "";
			}
		}
	}
	
	/**
	 * 
	 * @return Cash flow list with separator cash flow
	 */
	public ArrayList<CashFlow> getSeparatedCashFlow(){
		return this.cfs;
	}
	
	/**
	 * To refresh the list when there is update in date and deletion
	 * @param rcfs The real cash flow array list
	 * @param position position of the separated cash flow array list, -1 means in delete mode, no need to supply position
	 * @param isDateUpdated whether date is updated
	 */
	public void refresh(ArrayList<CashFlow> rcfs, int position, boolean isDateUpdated){
		if(isDateUpdated && position == -1){
			//If date is updated, sort the list again
			Collections.sort(rcfs, new Comparator<CashFlow>(){
				@Override
				public int compare(CashFlow lhs, CashFlow rhs) {
					// TODO Auto-generated method stub
					if(lhs.getCreateTime()==null || rhs.getCreateTime()==null){
						return 0;
					}
					return -lhs.getCreateTime().compareTo(rhs.getCreateTime());
				}
			});
			this.cfs = rcfs;
			init();
		}else{
			this.cfs.set(position, rcfs.get(this.itemIds[position]));
		}
		this.notifyDataSetChanged();
	}
	
	public void updateCashFlow(int position, CashFlow cf){
		this.cfs.set(position, cf);
		notifyDataSetChanged();
	}
	
	/**
	 * 
	 * @return the real position of the item, excluding separator
	 */
	public int getItemIds(int position){
		return this.itemIds[position];
	}
	
	//Original position in the arraylist, without separator
	private void fillOriginalPosition(ArrayList<CashFlow> cf){
		this.itemIds = new int[cf.size()];
		this.isSelected = new boolean[cf.size()];
		int j = 0;
		for(int i = 0; i < itemIds.length; i++){
			if(cf.get(i).getId() != TYPE_SEPARATOR){
				this.itemIds[i] = j++;
			}else{
				this.itemIds[i] = TYPE_SEPARATOR;
			}
			this.isSelected[i] = false;
		}
	}
	
	public ArrayList<Integer> getSelectedPosition(){
		ArrayList<Integer> selected = new ArrayList<Integer>();
		for(int i = 0; i < this.cfs.size(); i++){
			if(cfs.get(i).getId() != TYPE_SEPARATOR && this.isSelected[i]){
				selected.add(i);
			}
		}
		
		return selected;
	}
	
	public void setCheckBoxVisibility(int isVisible){
		this.isVisible = isVisible;
		notifyDataSetChanged();
	}
	
	public int getCheckBoxVisibility(){
		return this.isVisible;
	}
	
	public void setSelected(int position){
		if(!this.isSelected[position]){
			this.isSelected[position] = true;
			this.SELECTEDCOUNTER++;
			notifyDataSetChanged();
		}
	}
	
	public void setDeselected(int position){
		if(this.isSelected[position]){
			this.isSelected[position] = false;
			this.SELECTEDCOUNTER--;
			notifyDataSetChanged();
		}
	}
	
	public boolean isSelected(int position){
		return this.isSelected[position];
	}
	
	public int getNumOfSelected(){
		return this.SELECTEDCOUNTER;
	}
	
	/**
	 * Set all the checkbox
	 * @param isSelect Whether check all or uncheck all
	 */
	public void setAll(boolean isSelect){
		this.SELECTEDCOUNTER = 0;
		for(int i = 0; i < this.isSelected.length; i++){
			if(isSelect){
				this.isSelected[i] = true;
				if(this.cfs.get(i).getId() != TYPE_SEPARATOR){
					this.SELECTEDCOUNTER++;
				}
			}else{
				this.isSelected[i] = false;
			}
		}
		if(isSelect){
			this.isAllSelected = true;
		}else{
			this.isAllSelected = false;
			this.SELECTEDCOUNTER = 0;
		}
		notifyDataSetChanged();
	}
	
	public boolean isAllSelected(){
		return this.isAllSelected;
	}
	
	/**
	 * Reset the counter for number of selected elements
	 */
	public void resetCounter(){
		this.SELECTEDCOUNTER = 0;
	}
	
	//Checkbox checked change listener
	private OnCheckedChangeListener checkBoxListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton btnView, boolean isChecked) {
			// TODO Auto-generated method stub
			int idx = (Integer)btnView.getTag();
			isSelected[idx] = isChecked;
			btnView.setChecked(isSelected[idx]);
		}
		
	};

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		for(int i = 0; i < this.getCount(); i++){
			if(this.cfs.get(i).getCreateTime() == this.sections.get(section)){
				return i;
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return this.mCreateTime.get(this.cfs.get(position).getCreateTime());
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
//		String[] timeArr = new String[this.sections.size()];
//		for(int i = 0; i < timeArr.length; i++){
//			timeArr[i] = this.sections.get(i).getCreateTime().toString();
//		}
		return this.sections.toArray();
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if(cfFilter == null){
			cfFilter = new CashFlowFilter();
		}
		return cfFilter;
	}
	
	private class CashFlowFilter extends Filter{

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			
			if(constraint != null && constraint.length() > 0){
				ArrayList<CashFlow> filteredList = new ArrayList<CashFlow>();
				for(int i = 0; i < cfs.size(); i++){
					if(cfs.get(i).getId() != TYPE_SEPARATOR){
						CashFlow _cf = cfs.get(i);
						if(_cf.getCategories().toLowerCase().contains(constraint) 
								|| _cf.getTags().toLowerCase().contains(constraint)){
							filteredList.add(_cf);
						}
					}
				}
				result.count = filteredList.size();
				result.values = filteredList;
			}else{
				synchronized(this){
//					for(int i = 0; i < cfs.size(); i++){
//						if(cfs.get(i).getId() == TYPE_SEPARATOR){
//							cfs.remove(i);
//						}
//					}
					result.values = cloneCfs;
					result.count = cloneCfs.size();
				}
			}
			
			
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			cfs = (ArrayList<CashFlow>)results.values;
			refresh(cfs, FORCE_UPDATE_MODE, true);
			
		}
		
	}

}
