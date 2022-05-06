package com.example.myalbum.ui.dashboard;

import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.Image;
import com.example.myalbum.model.ImageClassifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ClassFolder {

    public String firstClassName;             //所属大类
    public String secondClassName;            //所属小类
    public String firstImagePath;              // 第一张图片路径
    public int classIndex;                        // 文件夹的名称
    public int count;                          // 文件夹图片数量
    public List<Image> mImagelist;               // 文件夹图片集合

    ClassFolder(Image firstImage){
        firstClassName = ImageClassifier.IMAGE_CLASSES[firstImage.classIndex].split("/")[0];
        secondClassName = ImageClassifier.IMAGE_CLASSES[firstImage.classIndex].split("/")[1];
        firstImagePath = firstImage.path;
        classIndex = firstImage.classIndex;
        count = 1;
        mImagelist = new ArrayList<>();
        mImagelist.add(firstImage);
    }

    public void add(Image image) {
        count++;
        mImagelist.add(image);
    }
}
