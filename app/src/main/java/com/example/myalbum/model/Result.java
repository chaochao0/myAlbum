package com.example.myalbum.model;

import android.graphics.Rect;

public class Result {
    public int faceIndex;
    public Float score;
    public Rect rect;

    public Result(int cls, Float output, Rect rect) {
        this.faceIndex = cls;
        this.score = output;
        this.rect = rect;
    }
}
