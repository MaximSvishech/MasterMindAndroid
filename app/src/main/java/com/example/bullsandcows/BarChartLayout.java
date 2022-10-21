package com.example.bullsandcows;
/* A custom UI element, which extends ViewGroup in order to allow for Bar Chart data representation.
    Holds a list of Bars to be displayed.
 */
import static com.google.android.material.internal.ViewUtils.*;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class BarChartLayout extends ViewGroup {

    public BarChartLayout(Context context) {
        super(context);
    }

    public BarChartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private List<Bar> bars = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    private double barMarginH = dpToPx(this.getContext(), 4);
    @SuppressLint("RestrictedApi")
    private double barMarginV = dpToPx(this.getContext(), 2);

    // Add bar to list
    public void add(Bar bar) {

        this.setLayoutTransition(new LayoutTransition());
        bars.add(bar);

        //Add bar's views as child views
        addChildInternal(bar.getNameView());
        addChildInternal(bar.getBarView());
        addChildInternal(bar.getLabelView());
    }

    private void addChildInternal(View childView) {
        addView(childView);
    }

    @Override
    // calculate our custom view's dimensions according to the layout that holds it
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int captionMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int labelMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        for (Bar bar: bars) {
            bar.getNameView().measure(captionMeasureSpec, captionMeasureSpec);
            bar.getLabelView().measure(labelMeasureSpec, labelMeasureSpec);
        }
        int maxCaptionHeight = bars.stream().mapToInt(bar -> bar.getNameView().getMeasuredHeight()).max().orElse(0);

        setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                maxCaptionHeight * bars.size()
        );
    }

    @Override

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // position the child views horizontally and vertically.
        // each bar's width is calculated by taking into account the maximum value
        // bars are animated when shown
        this.setLayoutTransition(new LayoutTransition());
        if (bars.size() == 0) {
            return;
        }

        int itemHeight = (b - t) / bars.size();
        int nameRight = bars.stream().mapToInt(bar -> bar.getNameView().getMeasuredWidth()).max().orElse(0);
        double maxValue = bars.stream().mapToDouble(Bar::getValue).max().orElse(0.0);
        for (int i = 0; i < bars.size(); i++) {
            Bar bar = bars.get(i);
            int nameLeft = nameRight - bar.getNameView().getMeasuredWidth();
            int nameTop = i * itemHeight;
            int nameBottom = nameTop + itemHeight;
            bar.getNameView().layout(nameLeft,
                    nameTop,
                    nameRight,
                    nameBottom);
            int barWidth = 0;
            if (maxValue > 0.0) {
                barWidth = (int)(bar.getValue() * (r - nameRight - barMarginH * 2) / maxValue);
            }
            int barLeft = nameRight;
            int barTop = i * itemHeight;
            int barRight = (int)(barLeft + barWidth + barMarginH * 2);
            int barBottom = barTop + itemHeight;
            View barView = bar.getBarView();
            barView.layout(
                    barLeft + (int)this.barMarginH,
                    barTop + (int)barMarginV,
                    barRight - (int)this.barMarginH,
                    barBottom - (int)barMarginV
            );
            barView.setPivotX(0);
            barView.setScaleX(0);
            int finalBarWidth = barWidth;
            // after bar width is calculated, we animate it to grow from zero
            // up to the calculated value. after the bar itself is 'grown',
            // label that displays the value is positioned to the right of the bar
            barView.animate().scaleX(1).setDuration(300).withEndAction(() -> {
                int labelWidth = bar.getLabelView().getMeasuredWidth();
                int spaceLeftForLabel = finalBarWidth - 2 * (int) barMarginH;

                int labelLeft = (spaceLeftForLabel >= labelWidth) ?
                        (int) (barRight - labelWidth - barMarginH) : barRight;
                int labelTop = barTop;
                int labelRight = labelLeft + labelWidth;
                int labelBottom = barBottom;

                bar.getLabelView().layout(
                        labelLeft,
                        labelTop,
                        labelRight,
                        labelBottom
                );
            });
        }
    }
}