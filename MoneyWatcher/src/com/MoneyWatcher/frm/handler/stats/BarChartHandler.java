package com.MoneyWatcher.frm.handler.stats;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import com.MoneyWatcher.R;
import com.MoneyWatcher.frm.utils.Utils;

public class BarChartHandler extends ChartHandler {

	private XYSeriesRenderer[] mRenderers;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset dataSet;
	private XYSeries[] mSeries;

	private final int SINGLE = 1; // Used in fill data by category

	private int mRenderType;
	private final int BY_DATE = 4; // In view by date mode
	private final int BY_MONTH = 5; // In view by month mode
	private final int VIEW_BYCAT = 6;// View by Category
	private final int VIEW_BYDATE = 7; // View by Date

	private double YMAX = 0; // Maximum point in graph
	private final int YMAX_BUF = 2; // Set buffer after setting max point

	private final int XMIN = -1;
	private final int XMAX_BUFF = 0;
	private int XMAX;

	private HashMap<String, Integer> mCategories;
	private HashMap<Long, Integer> mActualDates;

	private double[][] mCatDateMatrix; // Category and date matrix

	private double[] total; // Total sum of the cash flow
	private long[] mDates;// Date for x-axis
	private String[] myCategories; // Categories for y-axis
	
	private GraphicalView gView;
	private int BAR_CHART_VIEW_TYPE = VIEW_BYCAT;
	

	public BarChartHandler(Context ctx) {
		super(ctx);
		this.mCategories = new HashMap<String, Integer>();
		this.mActualDates = new HashMap<Long, Integer>();
	}

	// Init the key in hashmap
	private void initMap() {
		mRenderType = getStatsAdapter().getRenderType();
		Calendar c = Calendar.getInstance();
		// c.setTimeInMillis(milliseconds);
		int counter = 0;
		for (int i = 0; i < getCashFlowStats().getNumOfRow(); i++) {
			String categories = getCashFlowStats().getCategory(i);
			if (!mCategories.containsKey(categories)) {
				mCategories.put(categories, counter++);
			}
		}

		if (mRenderType == BY_DATE) {
			long fromDate = getCashFlowStats().getFromDate();
			long toDate = getCashFlowStats().getToDate();
			int diff = (int) Utils.dateDiff(fromDate, toDate);
			c.setTimeInMillis(fromDate);
			System.out.println(c.getTime().toString());
			// Date difference
			this.mDates = new long[diff + 1];
			this.myCategories = new String[mCategories.size()];
			total = new double[diff + 1];

			for (int i = 0; i <= diff; i++) {
				mActualDates.put(c.getTimeInMillis(), i);
				this.mDates[i] = c.getTimeInMillis();
				System.out.println(c.getTime().toString());

				// add 1 day to calendar
				c.add(Calendar.DATE, 1);
			}
		} else if (mRenderType == BY_MONTH) {
			long fromDate = getCashFlowStats().getFromDate();
			long toDate = getCashFlowStats().getToDate();
			c.setTimeInMillis(fromDate);
			int monthDiff = (int) Utils.monthDiff(fromDate, toDate);
			this.mDates = new long[monthDiff];
			this.myCategories = new String[mCategories.size()];
			total = new double[monthDiff];

			for (int i = 0; i < monthDiff; i++) {
				mActualDates.put(c.getTimeInMillis(), i);
				this.mDates[i] = c.getTimeInMillis();
				System.out.println(c.getTime().toString());

				// add 1 day to calendar
				c.add(Calendar.MONTH, 1);
			}

		}

		for (String s : mCategories.keySet()) {
			this.myCategories[mCategories.get(s)] = s;
		}

		Arrays.sort(this.myCategories);
		for (int i = 0; i < this.myCategories.length; i++) {
			mCategories.put(myCategories[i], i);
		}

		System.out.println(mCategories);
		mCatDateMatrix = new double[mCategories.size()][mActualDates.size()];
	}

	// Testing for bar chart, group by categories;
	private void fillStatsByCategory() {
		int numOfCategories = getCashFlowStats().getNumOfCategories();
		this.dataSet = new XYMultipleSeriesDataset();
		this.mRenderers = new XYSeriesRenderer[SINGLE];
		this.mSeries = new XYSeries[SINGLE];
		this.mRenderers = new XYSeriesRenderer[SINGLE];
		this.mRenderer = getRenderer();

		mSeries[0] = new XYSeries(ctx.getResources().getText(R.string.category)
				.toString());
		mRenderers[0] = new XYSeriesRenderer();

		for (int i = 0; i < numOfCategories; i++) {
			double amount = Math.abs(getCashFlowStats().getAmount(i));

			if (amount > YMAX) {
				YMAX = amount;
			}

			mSeries[0].add(i, amount);
		}
		this.XMAX = numOfCategories;
		for (int i = 0; i < numOfCategories; i++) {
			mRenderer.addXTextLabel(i, getCashFlowStats().getCategory(i));
		}

		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		int color = Utils.randomColorGenerator();
		r.setColor(color);
		mRenderer.addSeriesRenderer(r);
		this.dataSet.addSeries(mSeries[0]);
		setRendererAfterWork(mRenderer);
	}

	private void fillStatsByDate() {
		this.YMAX = 0;
		for (int i = 0; i < this.mCatDateMatrix.length; i++) {
			int numOfDays = this.mCatDateMatrix[i].length;
			for (int j = 0; j < numOfDays; j++) {
				this.mCatDateMatrix[i][j] = 0.0;
				total[j] = 0.0;
			}
		}

		for (int i = 0; i < getCashFlowStats().getNumOfRow(); i++) {
			String categories = getCashFlowStats().getCategory(i);
			long date = getCashFlowStats().getDate(i);

			System.out.println(new Date(date));

			Integer actualCatPos = mCategories.get(categories);
			Integer actualDatePos = mActualDates.get((Long) date);

			if (actualDatePos != null) {
				mCatDateMatrix[actualCatPos][actualDatePos] = Math
						.abs(getCashFlowStats().getAmount(i));
				total[actualDatePos] += mCatDateMatrix[actualCatPos][actualDatePos];
				if(total[actualDatePos] > this.YMAX){
					this.YMAX = total[actualDatePos];
				}
			}
		}
	}

	private XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer _mRenderer = new XYMultipleSeriesRenderer();
		
		_mRenderer.setClickEnabled(true);
		
		_mRenderer.setXLabels(0);
		_mRenderer.setBarSpacing(0.5);
		
		_mRenderer.setAntialiasing(true);
		_mRenderer.setChartTitleTextSize(20.0f);
		_mRenderer.setLabelsColor(Color.BLACK);
		_mRenderer.setLabelsColor(Color.BLACK);
		_mRenderer.setXLabelsColor(Color.BLACK);
		_mRenderer.setYLabelsColor(0, Color.BLACK);
		_mRenderer.setMargins(new int[] { 0, 0, 0, 0 });
		_mRenderer.setMarginsColor(Color.WHITE);
		_mRenderer.setShowGridX(true);
		_mRenderer.setGridColor(Color.GRAY);
		_mRenderer.setXRoundedLabels(false);
		_mRenderer.setYLabelsAlign(Align.LEFT);
		_mRenderer.setXLabelsAngle(-25);

		return _mRenderer;
	}

	// Set renderer attribute after filling of stats
	private void setRendererAfterWork(XYMultipleSeriesRenderer renderer) {
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(this.YMAX * this.YMAX_BUF);

		renderer.setXAxisMax(this.XMAX + this.XMAX_BUFF);
		renderer.setXAxisMin(this.XMIN);
	}

	@Override
	public GraphicalView getView() {
		
		if(this.BAR_CHART_VIEW_TYPE == VIEW_BYCAT){
			fillStatsByCategory();
		}else{
			genViewByDate();
		}
		GraphicalView gView = ChartFactory.getBarChartView(ctx, dataSet,
				mRenderer, Type.DEFAULT);
		return gView;
	}
	
	private void genViewByDate(){
		initMap();
		fillStatsByDate();
		this.YMAX = 0;
		int numOfCategories = mCategories.size();
		int numOfDays = 0;
		XYSeries[] series = new XYSeries[numOfCategories];

		for (int i = 0; i < this.mCatDateMatrix.length; i++) {
			numOfDays = this.mCatDateMatrix[i].length;
			series[i] = new XYSeries(this.myCategories[i]);
			for (int j = 0; j < numOfDays; j++) {
				double amount =  this.mCatDateMatrix[i][j];
				if(amount > this.YMAX){
					this.YMAX = amount;
				}
				series[i].add(j, amount);
			}
		}
		this.mSeries = series;
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		// 1 renderer, 1 series
		XYSeriesRenderer[] renderers = new XYSeriesRenderer[series.length];
		XYMultipleSeriesRenderer mRenderer = getRenderer();
		
		
		for (int i = 0; i < series.length; i++) {
			dataSet.addSeries(series[i]);
			int color = Utils.randomColorGenerator();
			renderers[i] = new XYSeriesRenderer();
			renderers[i].setColor(color);
			renderers[i].setLineWidth(3);
			renderers[i].setDisplayChartValues(true);
			mRenderer.addSeriesRenderer(renderers[i]);
//			mRenderer.addXTextLabel(i, sdf.format(mDates[i]));
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd");
		
		if(this.mRenderType == BY_MONTH){
			sdf = new SimpleDateFormat("MMM");
		}
		
		for(int i = 0; i < numOfDays; i++){
			mRenderer.addXTextLabel(i, sdf.format(mDates[i]));
		}

		mRenderer.setChartTitle(getChartTitle());
		this.dataSet = dataSet;
		this.mRenderer = mRenderer;
		this.mRenderers = renderers;
//		setRendererAfterWork(mRenderer);
	}
	
	public int getViewType(){
		return this.BAR_CHART_VIEW_TYPE;
	}
	
	public void setViewType(int viewType){
		this.BAR_CHART_VIEW_TYPE = viewType;
	}
	
	public int getRenderType() {
		return mRenderType;
	}

	/**
	 * Set rendering type for line chart
	 * 
	 * @param mRenderType
	 */
	public void setRenderType(int mRenderType) {
		this.mRenderType = mRenderType;
	}
}
