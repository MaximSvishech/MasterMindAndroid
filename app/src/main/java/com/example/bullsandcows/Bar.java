package com.example.bullsandcows;
import android.view.View;


public class Bar {
    private View nameView;
    private View barView;
    private View labelView;
    private Double value;


    public Bar(View nameView, View barView, View labelView, Double value) {
        this.nameView = nameView;
        this.barView = barView;
        this.labelView = labelView;
        this.value = value;
    }

    public View getNameView() {
        return nameView;
    }

    public void setNameView(View nameView) {
        this.nameView = nameView;
    }

    public View getBarView() {
        return barView;
    }

    public void setBarView(View barView) {
        this.barView = barView;
    }

    public View getLabelView() {
        return labelView;
    }

    public void setLabelView(View labelView) {
        this.labelView = labelView;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
