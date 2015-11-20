package com.wes.tagcloud;

import android.graphics.Point;

/**
 * Created by wangyong on 15/11/19.
 */
public class TagModel {

    private String keywords;
    private int color;
    private int heat = -1;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

}
