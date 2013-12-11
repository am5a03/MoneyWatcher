package com.MoneyWatcher.frm.handler.stats;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import com.MoneyWatcher.frm.utils.Utils;

public class LineChartHandler extends ChartHandler {

	private GraphicalView gView;
	private XYSeriesRenderer[] renderers;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset dataSet;
	private TimeSeries[] mTimeSeries;

	private HashMap<String, Integer> mCategories;
	private HashMap<Long, Integer> mActualDates;

	private double[][] mCatDateMatrix; // Category and date matrix

	private double[] total; // Total sum of the cash flow
	private long[] mDates;// Date for x-axis
	private String[] myCategories; // Categories for y-axis

	private int mRenderType;
	private final int BY_DATE = 4;
	private final int BY_MONTH = 5;
	
	private double mMAXpt = 0; //Maximum point
	private final int DAY_IN_MILLISECOND = 81300000;
	private final double BUFFER = 1.5; //Buffer for pan y-axis pan limit

	public LineChartHandler(Context ctx) {
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

	private void fillStats() {
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
				if(total[actualDatePos] > this.mMAXpt){
					this.mMAXpt = total[actualDatePos];
				}
			}
		}
	}

	private XYMultipleSeriesRenderer getRenderer() {
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		
		mRenderer.setClickEnabled(true);
		
		mRenderer.setXAxisMin(this.mDates[0]);
		mRenderer.setXAxisMax(this.mDates[mDates.length - 1]);
		double[] limits = { this.mDates[0] - 3 * DAY_IN_MILLISECOND,
				this.mDates[mDates.length - 1] + 3 * DAY_IN_MILLISECOND, 0, this.mMAXpt * BUFFER };
		mRenderer.setPanLimits(limits);

		mRenderer.setXRoundedLabels(false);
		mRenderer.setAntialiasing(true);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setMargins(new int[] { 0, 0, 0, 0 });
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);
		mRenderer.setYAxisMin(0, 0);
		mRenderer.setShowGrid(true);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setPointSize(4);
		mRenderer.setYLabelsAlign(Align.LEFT);
		mRenderer.setXLabelsAngle(-25);
		mRenderer.setGridColor(Color.GRAY);

		mRenderer.setChartTitleTextSize(20.0f);
		mRenderer.setLabelsTextSize(15.0f);
		this.renderer = mRenderer;

		return mRenderer;
	}

	@Override
	public GraphicalView getView() {
		initMap();
		fillStats();
		int numOfCategories = mCategories.size();
		TimeSeries[] series = new TimeSeries[numOfCategories + 1];

		for (int i = 0; i < this.mCatDateMatrix.length; i++) {
			int numOfDays = this.mCatDateMatrix[i].length;
			series[i] = new TimeSeries(this.myCategories[i]);
			for (int j = 0; j < numOfDays; j++) {
				series[i].add(new Date(mDates[j]), this.mCatDateMatrix[i][j]);
			}
		}
		// Add series for total sum
		series[series.length - 1] = new TimeSeries("Total[...]");
		for (int i = 0; i < this.mDates.length; i++) {
			series[series.length - 1].add(new Date(mDates[i]), total[i]);
		}
		this.mTimeSeries = series;
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		// 1 renderer, 1 series
		XYSeriesRenderer[] renderers = new XYSeriesRenderer[series.length];
		XYMultipleSeriesRenderer mRenderer = getRenderer();
		Random rnd = new Random();

		for (int i = 0; i < series.length; i++) {
			dataSet.addSeries(series[i]);
			int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
			renderers[i] = new XYSeriesRenderer();
			renderers[i].setColor(color);
			renderers[i].setPointStyle(PointStyle.CIRCLE);
			renderers[i].setFillPoints(true);
			renderers[i].setLineWidth(3);
			renderers[i].setDisplayChartValues(true);
			mRenderer.addSeriesRenderer(renderers[i]);
		}

		mRenderer.setChartTitle(getChartTitle());
		this.dataSet = dataSet;
		this.mRenderer = mRenderer;
		this.renderers = renderers;

		// for(int i = 0; i < mDates.length; i++){
		// System.out.println(new Date(mDates[i]).toString());
		// }

		// hardcoded
		if (getStatsAdapter().getRenderType() == BY_DATE) {
			this.gView = ChartFactory.getTimeChartView(ctx, dataSet, mRenderer,
					"MMM-dd");
		} else {
			this.gView = ChartFactory.getTimeChartView(ctx, dataSet, mRenderer,
					"MMM");
		}

		return gView;
	}

	/**
	 * Remove series
	 * 
	 * @param chosenIdx
	 *            A boolean array representing which categories are filtered
	 *            out.
	 */
	public void filterSeries(boolean[] chosenIdx) {
		for (int i = 0; i < chosenIdx.length; i++) {
			if (chosenIdx[i] == false) {
				// this.dataSet.removeSeries(this.mTimeSeries[i]);
				// this.mRenderer.removeSeriesRenderer(this.renderers[i]);
				this.renderers[i].setColor(Color.TRANSPARENT);
			} else {
				// if(this.dataSet.getSeriesAt(i) != this.mTimeSeries[i]){
				// this.dataSet.addSeries(i, this.mTimeSeries[i]);
				// this.mRenderer.addSeriesRenderer(i, this.renderers[i]);
				// }
				this.renderers[i].setColor(Utils.randomColorGenerator());
			}
		}
		this.gView.repaint();
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
	// public void testRemoveSeries(){
	// this.dataSet.removeSeries(0);
	// this.mRenderer.removeSeriesRenderer(this.renderers[0]);
	// this.dataSet.removeSeries(2);
	// this.mRenderer.removeSeriesRenderer(this.renderers[2]);
	// this.gView.repaint();
	// }
}
