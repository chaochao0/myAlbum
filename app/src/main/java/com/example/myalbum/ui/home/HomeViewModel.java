package com.example.myalbum.ui.home;

import static java.lang.Thread.sleep;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myalbum.MainActivity;
import com.example.myalbum.MyApplication;
import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.Face;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.model.Result;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

//    private MutableLiveData<LinkedHashMap<String, List<PhotoItem>>> mPhotoWithDay;
    private LiveData<List<Image>> mImageList;
    public int lastOffset = -1;
    public int lastPosition = -1;

    private boolean needScanner = true;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Log.i("HomeViewModel","create");
        mImageList = ImageRepository.getImageRepositoryInstance().getAllImages();
    }

    public LiveData<List<Image>> getImageList() {
        return mImageList;
    }


//    public LiveData<LinkedHashMap<String, List<PhotoItem>>> getPhotoWithDay(){return mPhotoWithDay;}


}