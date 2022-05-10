package com.example.myalbum.ui.face;

import android.graphics.Rect;

import com.example.myalbum.database.Face;
import com.example.myalbum.database.Image;
import com.example.myalbum.model.ImageClassifier;

import java.util.ArrayList;
import java.util.List;

public class FaceFolder {
    public String firstImagePath;              // 第一张图片路径
    public Rect rect;                          //人脸在第一章图片中的位置
    public int count;                          // 文件夹图片数量
    public List<Image> mImagelist;               // 文件夹图片集合

    FaceFolder(Image firstImage, Face firstFace){
        firstImagePath = firstImage.path;
        rect = firstFace.rect;
        count = 1;
        mImagelist = new ArrayList<>();
        mImagelist.add(firstImage);
    }

    public void add(Image image) {
        count++;
        mImagelist.add(image);
    }
}
