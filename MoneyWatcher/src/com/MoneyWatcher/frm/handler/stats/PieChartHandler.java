package com.MoneyWatcher.frm.handler.stats;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.MoneyWatcher.frm.utils.Utils;

public class PieChartHandler extends ChartHandler{
		
	public PieChartHandler(Context ctx) {
		super(ctx);
	}
	
	private DefaultRenderer getRenderer(){
		DefaultRenderer renderer = new DefaultRenderer();
		
		renderer.setClickEnabled(true);
		renderer.setPanEnabled(false);
		renderer.setAntialiasing(true);
		renderer.setChartTitleTextSize(20.0f);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setLabelsTextSize(15.0f);
		
		return renderer;
	}
	
	@Override
	public GraphicalView getView() {
		CategorySeries cs = new CategorySeries("");
		DefaultRenderer _renderer = getRenderer();
		this.renderer = _renderer;
		
		for (int i = 0; i < getCashFlowStats().getNumOfCategories(); i++) {
			cs.add(getCashFlowStats().getCategory(i),
					getCashFlowStats().getAmount(i));
		}
		
		for(int i = 0; i < getCashFlowStats().getNumOfCategories(); i++){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			int color = Utils.randomColorGenerator();
			r.setColor(color);
			_renderer.addSeriesRenderer(r);
		}
		
		_renderer.setChartTitle(getChartTitle());
		
		GraphicalView gView = ChartFactory.getPieChartView(ctx, cs, _renderer);
		gView.setOnClickListener(this);
		
		return gView;
	}

	

}
