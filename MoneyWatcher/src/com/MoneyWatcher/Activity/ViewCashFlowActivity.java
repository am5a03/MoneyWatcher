package com.MoneyWatcher.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.MWConstants;
import com.MoneyWatcher.frm.RequestCode;
import com.MoneyWatcher.frm.db.CashFlowAdapter;
import com.MoneyWatcher.frm.db.ConfigAdapter;
import com.MoneyWatcher.frm.obj.CashFlow;
import com.MoneyWatcher.frm.parcelable.ParcelableCashFlow;
import com.MoneyWatcher.gui.CashFlowListAdapter;
import com.MoneyWatcher.handler.activity.DeleteHandler;
import com.MoneyWatcher.interfaces.ISearchConstants;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ViewCashFlowActivity extends SherlockActivity implements
		ISearchConstants {

	private ArrayList<CashFlow> cfs;
	private ArrayList<ParcelableCashFlow> pcfs;
	private ArrayList<CashFlow> mSelectedCashFlow;
	
	private ConfigAdapter ca;
	private CashFlowListAdapter cfla;
	private ListView lvTransactions;
	private ActionMode mLongClickActionMode;
	
	private EditText txtFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashflowlist);
		setTheme(R.style.MW_ActionTheme);
		init();

	}

	private void init() {
		String callerActivity = "";
		if (this.getCallingActivity() != null) {
			callerActivity = this.getCallingActivity().getClassName();
		}

		lvTransactions = (ListView) findViewById(R.id.lvTransactions);
		lvTransactions.setItemsCanFocus(true);
		CashFlowAdapter cfa = new CashFlowAdapter(this);
		this.ca = new ConfigAdapter(this);
		this.cfs = new ArrayList<CashFlow>();
		this.mSelectedCashFlow = new ArrayList<CashFlow>();

		if (callerActivity.equals(MoneyWatcherActivity.class.getName())) {

		} else if (callerActivity.equals(AddActivity.class.getName())) {
			long d = this.getIntent().getLongExtra(MWConstants.CASHFLOWDATE, -1);
			Calendar fromC = Calendar.getInstance();
			Calendar toC = Calendar.getInstance();
			
			fromC.setTimeInMillis(d);
			fromC.set(Calendar.HOUR_OF_DAY, 0);
			fromC.set(Calendar.MINUTE, 0);
			fromC.set(Calendar.SECOND, 0);
			fromC.set(Calendar.MILLISECOND, 0);
			
			toC.setTimeInMillis(d);
			toC.set(Calendar.HOUR_OF_DAY, 23);
			toC.set(Calendar.MINUTE, 59);
			toC.set(Calendar.SECOND, 59);
			toC.set(Calendar.MILLISECOND, 999);
			
//			Date truncatedNow = new Date();
//			Calendar c = Calendar.getInstance();
//			c.setTime(truncatedNow);
//			c.set(Calendar.HOUR_OF_DAY, 0);
//			c.set(Calendar.MINUTE, 0);
//			c.set(Calendar.SECOND, 0);
//			c.set(Calendar.MILLISECOND, 0);
//
//			truncatedNow = new Date(c.getTimeInMillis());

			this.cfs = cfa.getCashFlowByDate(new Date(fromC.getTimeInMillis()), new Date(toC.getTimeInMillis()));
			fillParcelableArray(this.cfs);
			this.cfla = new CashFlowListAdapter(this.cfs, this);
			lvTransactions.setAdapter(this.cfla);

		} else if (callerActivity.equals(SearchActivity.class.getName())) {
			handleParcel(this.getIntent().getExtras());
		} else {
			new GetCashFlow().execute(cfa);
		}
		lvTransactions.setLongClickable(true);
		lvTransactions
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(
							AdapterView<?> parentAdapter, View v, int position,
							long id) {
						CashFlowListAdapter _cfla = (CashFlowListAdapter) parentAdapter
								.getAdapter();
						if (_cfla.getCheckBoxVisibility() == View.VISIBLE) {
							_cfla.setCheckBoxVisibility(View.INVISIBLE);
							mLongClickActionMode.finish();
						} else {
							_cfla.setCheckBoxVisibility(View.VISIBLE);
							if(!mSelectedCashFlow.contains(_cfla.getItem(position))){
								_cfla.setSelected(position);
								mSelectedCashFlow.add((CashFlow)_cfla.getItem(position));
							}
							mLongClickActionMode = startActionMode(new DeleteActionMode());
							mLongClickActionMode.setTitle(((Integer)_cfla.getNumOfSelected()).toString() + " selected[...]");
						}
						return true;
					}

				});
		lvTransactions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CashFlowListAdapter _cfla = (CashFlowListAdapter) parent
						.getAdapter();

				if (_cfla.getCheckBoxVisibility() == View.INVISIBLE) {
					CashFlow cf = (CashFlow) parent.getAdapter().getItem(
							position);
					createEditActivity(cf, position);
				} else {
					if (_cfla.isSelected(position)) {
						mSelectedCashFlow.remove((CashFlow)_cfla.getItem(position));
						System.out.println(mSelectedCashFlow);
						_cfla.setDeselected(position);
					} else {
						mSelectedCashFlow.add((CashFlow)_cfla.getItem(position));
						_cfla.setSelected(position);
					}
					mLongClickActionMode.setTitle(((Integer)_cfla.getNumOfSelected()).toString() + " selected[...]");
				}

				// finish();
				// if(cfla.getCheckBoxVisibility() == View.VISIBLE){
				// cfla.setCheckBoxVisibility(View.INVISIBLE);
				// }else{
				// cfla.setCheckBoxVisibility(View.VISIBLE);
				// }

				// if (swipeDetector.swipeDetected()){
				// // do the onSwipe action
				// if(cfla.getCheckBoxVisibility() == View.VISIBLE){
				// cfla.setCheckBoxVisibility(View.INVISIBLE);
				// }else{
				// cfla.setCheckBoxVisibility(View.VISIBLE);
				// }
				// } else {
				// // do the onItemClick action
				// CashFlow cf = (CashFlow)
				// parent.getAdapter().getItem(position);
				// createEditActivity(cf, position);
				// }
			}

		});

		// Register context menu
		// registerForContextMenu(lvTransactions);
	}

	private void createEditActivity(CashFlow cf, int lvPosition) {
		ParcelableCashFlow pcf = new ParcelableCashFlow(cf);
		Intent editIntent = new Intent(getApplicationContext(),
				AddActivity.class);

		// editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Pass cash flow ID, item position to edit intent
		editIntent.putExtra(ID, cf.getId());
		editIntent.putExtra(POS, lvPosition);
		// TODO pass list item position into it when start a new activity.
		// When in edit mode, obtain the position id, which is corresponding the
		// the id in arraylist.
		editIntent.putExtra(SINGLECASHFLOW, pcf);
		startActivityForResult(editIntent, RequestCode.OK);
	}

	private void delete(CashFlow cf, int lvPosition) {
		this.cfs.remove(cf);
		this.cfla.refresh(this.cfs, lvPosition, true);

		CashFlowAdapter cfa = new CashFlowAdapter(this);
		boolean success = cfa.delete(new String[] { ((Long) cf.getId())
				.toString() });

		if (success) {
			Toast.makeText(this, "Deleted successfully[...]",
					Toast.LENGTH_SHORT).show();
		}
	}

	// Handel Parcelable cash flow returned
	private void handleParcel(Bundle bundle) {
		this.pcfs = bundle.getParcelableArrayList(CASHFLOW);
		this.cfs = new ArrayList<CashFlow>();
		for (ParcelableCashFlow pcf : pcfs) {
			this.cfs.add(pcf.getCashFlow());
		}
		this.cfla = new CashFlowListAdapter(this.cfs, this);
		lvTransactions.setAdapter(this.cfla);
	}

	// Fill up the parcelable array
	private void fillParcelableArray(ArrayList<CashFlow> cashFlow) {
		this.cfs = cashFlow;
		this.pcfs = new ArrayList<ParcelableCashFlow>();
		for (CashFlow cf : cashFlow) {
			this.pcfs.add(new ParcelableCashFlow(cf));
		}
	}

	// Handle cash flow from async task
	private void handleCashFlowFromAsyncTask(ArrayList<CashFlow> cfs) {
		this.cfs = cfs;
		this.cfla = new CashFlowListAdapter(this.cfs, this);
		lvTransactions.setAdapter(this.cfla);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// //Used to put dark icons on light action bar
	// boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
	//
	// menu.add("Search")
	// .setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
	// .setActionView(R.layout.collapsible_edittext)
	// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS |
	// MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	//
	// return true;
	// }

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		if (v.getId() == R.id.lvTransactions) {
//			final int EDIT = 0;
//			final int DELETE = 1;
//			String[] editDelete = getResources().getStringArray(
//					R.array.editdelete);
//
//			menu.setHeaderTitle("Action[...]");
//			menu.add(0, EDIT, 0, editDelete[EDIT]);
//			menu.add(0, DELETE, 1, editDelete[DELETE]);
//		}
//	}

//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		final int EDIT = 0;
//		final int DELETE = 1;
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
//				.getMenuInfo();
//
//		if (item.getItemId() == EDIT) {
//			CashFlow cf = (CashFlow) lvTransactions.getAdapter().getItem(
//					info.position);
//			createEditActivity(cf, info.position);
//		} else if (item.getItemId() == DELETE) {
//			CashFlow cf = (CashFlow) lvTransactions.getAdapter().getItem(
//					info.position);
//			delete(cf, info.position);
//			System.out.println("DELETE");
//		}
//		return true;
//	}

	// Handle the cash flow return from edit intent
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RequestCode.OK) {
			if (resultCode == RESULT_OK) {
				ParcelableCashFlow pCashFlow = data
						.getParcelableExtra(SINGLECASHFLOW);
				int position = data.getIntExtra(POS, -1);
				if (pCashFlow != null && position != -1) {
					// this.cfs = cfla.getSeparatedCashFlow();
					// fillParcelableArray(this.cfs);
					boolean isDateUpdated = false;
					if (!pCashFlow
							.getCashFlow()
							.getCreateTime()
							.equals(this.cfs.get(cfla.getItemIds(position))
									.getCreateTime())) {
						isDateUpdated = true;
					}
					this.pcfs.set(cfla.getItemIds(position), pCashFlow);
					this.cfs.set(cfla.getItemIds(position),
							pCashFlow.getCashFlow());
					this.cfla.refresh(cfs, position, isDateUpdated);

				}else{
					//Finish the previous ViewCashFlowActivity
					finish();
				}
			}
		}
	}

	// TODO: AsyncTask test
	private class GetCashFlow extends
			AsyncTask<CashFlowAdapter, Void, ArrayList<CashFlow>> {

		ProgressDialog dialog;

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(ViewCashFlowActivity.this);
			dialog.setTitle("Please wait[...]");
			dialog.setMessage("Searching[...]");
			dialog.show();
			// Toast.makeText(getApplicationContext(), "Searching...",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		protected ArrayList<CashFlow> doInBackground(CashFlowAdapter... cfa) {
			// TODO Auto-generated method stub
			ArrayList<CashFlow> _cfs = new ArrayList<CashFlow>();
			_cfs = cfa[0].getAllCashFlow();
			return _cfs;
		}

		@Override
		protected void onPostExecute(ArrayList<CashFlow> cfs) {
			fillParcelableArray(cfs);
			handleCashFlowFromAsyncTask(cfs);
			dialog.dismiss();
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        menu.add("Search[...]").setActionView(R.layout.collapsible_edittext)
        .setIcon(R.drawable.action_search)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
        menu.add(getResources().getText(R.string.add))
        .setIcon(R.drawable.content_new)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        
//        this.txtFilter.addTextChangedListener(filterListener);

        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle() == getResources().getText(R.string.add)){
	        Intent addItemIntent = new Intent(getApplicationContext(), AddActivity.class);
	        addItemIntent.putExtra(MWConstants.REQUESTCODE, RequestCode.ADD);
	        startActivityForResult(addItemIntent, RequestCode.OK);
//	        finish();
		}else{
			this.txtFilter = (EditText)item.getActionView().findViewById(R.id.txtFilter);
			this.txtFilter.addTextChangedListener(filterListener);
		}
        return true;
    }
	
	private TextWatcher filterListener = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			cfla.getFilter().filter(s);
		}
		
	};
	
	/**
	 * Delete action mode
	 * @author Raymond
	 *
	 */
	private final class DeleteActionMode implements ActionMode.Callback {
		private final int GROUPID = 0;
		private final int DELETE = 1;
		private final int SELECTALL = 2;
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			// boolean isLight = SampleList.THEME ==
			// R.style.Theme_Sherlock_Light;
			menu.add(GROUPID, DELETE, 0, "Delete[...]")
				.setIcon(R.drawable.content_discard)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(GROUPID, SELECTALL, 0, "Select All[...]")
				.setIcon(R.drawable.navigation_accept)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode,
				com.actionbarsherlock.view.MenuItem item) {
			// TODO Auto-generated method stub
			System.out.println(item.getItemId());
			
			if(item.getItemId() == SELECTALL){
				if(!cfla.isAllSelected()){
					cfla.setAll(true);
					mSelectedCashFlow.clear();
					mSelectedCashFlow.addAll(cfs);
				}else{
					mSelectedCashFlow.clear();
					cfla.setAll(false);
				}
				mode.setTitle(cfla.getNumOfSelected() + " selected[...]");
			//If mode == DELETE
			}else{
				System.out.println(cfla.getSelectedPosition());
				long[] _ids = new long[mSelectedCashFlow.size()];
				//Get all selected position
				for(int i = 0; i < mSelectedCashFlow.size(); i++){
					//Remove from the list view for selected cash flow
					cfs.remove(mSelectedCashFlow.get(i));
					_ids[i] = mSelectedCashFlow.get(i).getId();
				}
				cfla.refresh(cfs, -1, true);
				DeleteHandler dh = new DeleteHandler(new CashFlowAdapter(ViewCashFlowActivity.this));
				dh.setIds(_ids);
				dh.delete();
				cfla.resetCounter();
				mode.finish();
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			cfla.setCheckBoxVisibility(View.INVISIBLE);

		}

	}

	// @Override
	// public ListView getListView() {
	// // TODO Auto-generated method stub
	// return this.lvTransactions;
	// }
	//
	// @Override
	// public void getSwipeItem(boolean isRight, int position) {
	// // TODO Auto-generated method stub
	// if(isRight){
	// if(cfla.getCheckBoxVisibility() == View.VISIBLE){
	// cfla.setCheckBoxVisibility(View.INVISIBLE);
	// }else{
	// cfla.setCheckBoxVisibility(View.VISIBLE);
	// cfla.setSelected(position);
	// }
	// }
	//
	// }
	//
	// @Override
	// public void onItemClickListener(ListAdapter adapter, int position) {
	// // TODO Auto-generated method stub
	// CashFlow cf = (CashFlow) adapter.getItem(position);
	// if(adapter.isEnabled(position)){
	// createEditActivity(cf, position);
	// }
	// }
}
