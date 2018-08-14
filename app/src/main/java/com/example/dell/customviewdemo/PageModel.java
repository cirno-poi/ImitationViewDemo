package com.example.dell.customviewdemo;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

public class PageModel {
    @LayoutRes
    int sampleLayoutRes;
    @StringRes
    int titleRes;
    @LayoutRes
    int compareLayoutRes;

    public int getSampleLayoutRes() {
        return sampleLayoutRes;
    }

    public void setSampleLayoutRes(int sampleLayoutRes) {
        this.sampleLayoutRes = sampleLayoutRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getCompareLayoutRes() {
        return compareLayoutRes;
    }

    public void setCompareLayoutRes(int compareLayoutRes) {
        this.compareLayoutRes = compareLayoutRes;
    }

    public PageModel(@LayoutRes int sampleLayoutRes, @StringRes int titleRes, @LayoutRes int compareLayoutRes) {
        this.sampleLayoutRes = sampleLayoutRes;
        this.titleRes = titleRes;
        this.compareLayoutRes = compareLayoutRes;
    }
}
