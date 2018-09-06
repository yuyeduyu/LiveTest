
package com.ascend.wangfeng.locationbyhand.view.myview;

import android.content.Context;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.ascend.wangfeng.locationbyhand.view.fragment.LineFragment;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private TextView tvContent;

    private DecimalFormat format;

    private int style;//0 折线图  1 柱状图(柱状图 y轴经过处理，与折线图数据不一致，所以要区分)

    public XYMarkerView(Context context, int style) {
        super(context, R.layout.custom_marker_view);

        this.style = style;
        tvContent = (TextView) findViewById(R.id.tvContent);
        format = new DecimalFormat("###");
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (style == 0){
            tvContent.setText("时间: " + TimeUtil.formatToHour((long) e.getX())
                    + "\n信号强度: " + format.format(e.getY()));
        }
        else if (style == 1){
            tvContent.setText("时间: " + TimeUtil.formatToHour((long) e.getX()*LineFragment.Multiple)
                    + "\n信号强度: " + format.format(e.getY() - 100));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
