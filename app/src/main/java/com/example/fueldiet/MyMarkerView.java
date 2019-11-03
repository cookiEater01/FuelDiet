package com.example.fueldiet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView label;
    private final TextView value;
    private List<String> labels;
    private String unit;

    public MyMarkerView(Context context, int layoutResource, List<String> lables, String unit) {
        super(context, layoutResource);
        this.labels = lables;
        this.unit = unit;
        label = findViewById(R.id.marker_label);
        value = findViewById(R.id.marker_value);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            label.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else if (e instanceof BarEntry) {
            label.setText(labels.get((int) e.getX()));
            value.setText(e.getY() + unit);
        } else if (e instanceof Entry) {
            label.setText(labels.get((int) e.getX()));
            value.setText(e.getY() + unit);
        } else {
            label.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
