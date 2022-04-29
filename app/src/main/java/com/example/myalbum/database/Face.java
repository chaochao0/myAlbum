package com.example.myalbum.database;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

@Entity
@TypeConverters(ListConverter.class)
public class Face {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int faceId;   //face id

    @ColumnInfo
    public int imageOwnerId;  //所属照片id

    @ColumnInfo
    public float[] faceFeatures;  //人脸的特征向量

    @ColumnInfo
    public Rect rect;  //在所属图片中的人脸框

    @ColumnInfo
    public int faceClusterType;  //人脸聚类类别  从1开始
}
