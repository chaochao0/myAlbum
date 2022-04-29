package com.example.myalbum.database;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity
@TypeConverters(ListConverter.class)
public class Image {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int imageId;   //照片id

    @ColumnInfo
    public String path;  //照片路径

    @ColumnInfo
    public int classIndex;  //照片类别

    @ColumnInfo
    public float[] imageFeatures;  //照片的特征向量

    @ColumnInfo
    public int faceNum;  //人脸数

//    @ColumnInfo
//    public List<Rect> rects;  //人脸框
//
//    @ColumnInfo
//    public float[][] facesFeatures;  //人脸特征向量
//
//    @ColumnInfo
//    public int[] faceClusters;  //人脸聚类类别
}



