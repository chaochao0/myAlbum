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
    public long date;  //照片时间

    @ColumnInfo
    public int classIndex;  //照片类别   -1：不进行分类

    @ColumnInfo
    public float[] imageFeatures;  //照片的特征向量

    @ColumnInfo
    public int faceNum;  //人脸数

    public void printInfo(){
        System.out.println("imageId"+imageId);
        System.out.println("date:"+date);
        System.out.println("path:"+path);
//        System.out.println("imageFeatures："+GsonInstance.getInstance().getGson().toJson(imageFeatures));
        System.out.println("classIndex:"+classIndex);
        System.out.println("faceNum:"+faceNum);
    }
//    @ColumnInfo
//    public List<Rect> rects;  //人脸框
//
//    @ColumnInfo
//    public float[][] facesFeatures;  //人脸特征向量
//
//    @ColumnInfo
//    public int[] faceClusters;  //人脸聚类类别
}



