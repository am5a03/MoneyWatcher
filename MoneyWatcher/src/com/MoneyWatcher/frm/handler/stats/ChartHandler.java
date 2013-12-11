package com.MoneyWatcher.frm.handler.stats;

import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.MoneyWatcher.Activity.StatsTableActivity;
import com.MoneyWatcher.Activity.ViewCashFlowActivity;
import com.MoneyWatcher.frm.db.StatsAdapter;
import com.MoneyWatcher.frm.obj.CashFlowStats;

public abstract class ChartHandler implements OnClickListener{
	Context ctx;
	DefaultRenderer renderer;

	private StatsAdapter mSA;

	private CashFlowStats mCashFlowStats;
	
	private String chartTitle;

	public ChartHandler(Context ctx) {
		this.ctx = ctx;
		this.renderer = new DefaultRenderer();
	}

	public void setStatsAdpater(StatsAdapter mSA){
		this.mSA = mSA;
	}
	
	protected StatsAdapter getStatsAdapter() {
		return this.mSA;
	}

	public CashFlowStats getCashFlowStats() {
		return mCashFlowStats;
	}

	/**
	 * Set up cash flow stats, to be filled by handler
	 * @param mCashFlowStats
	 */
	public void setCashFlowStats(CashFlowStats mCashFlowStats) {
		this.mCashFlowStats = mCashFlowStats;
	}

	public GraphicalView getView() {
		return null;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
		this.renderer.setChartTitle(chartTitle);
	}
	
	@Override
	public void onClick(View v) {
		Intent statsTableIntent = new Intent(v.getContext(), StatsTableActivity.class);
		ctx.startActivity(statsTableIntent);
	}
}
