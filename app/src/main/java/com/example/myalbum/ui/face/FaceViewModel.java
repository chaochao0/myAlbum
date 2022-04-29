package com.example.myalbum.ui.face;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myalbum.MainActivity;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.Result;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class FaceViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> picturePath;
    private MutableLiveData<Bitmap> bitmap;

//    private ArrayList<Result> resultOrigin;  //result的原始保存地址，每次获得结果后重新new并赋值，然后让resultList指向它
    private MutableLiveData<ArrayList<Result>> resultList;


    public FaceViewModel(@NonNull Application application){
        super(application);
        Log.i("FaceViewModel","create");
        picturePath = new MutableLiveData<>();
//        resultOrigin = new ArrayList<>();
        resultList = new MutableLiveData<>();
        AssetManager assetManager = application.getBaseContext().getAssets();
        bitmap = new MutableLiveData<>();
        try{
            bitmap.setValue(BitmapFactory.decodeStream(assetManager.open("faceTest.jpg")));
        }catch (IOException e) {
            Log.e("FaceViewModel create", "Error reading assets", e);
        }
    }

    public LiveData<String> getPicturePath(){
        return picturePath;
    }

    public LiveData<Bitmap> getBitmap(){
        return bitmap;
    }

//    public void postResults(ArrayList<Result> results){
//        resultOrigin = new ArrayList<Result>(results);
//
//        resultList.postValue(resultOrigin);
//
//    }

    public LiveData<ArrayList<Result>> getResultList(){
        return resultList;
    }


    public void onChoosePicture(String realPath, float mImageViewWidth, float mImageViewHeight) {
        try {
            bitmap.setValue(BitmapFactory.decodeStream(new FileInputStream(realPath)));
        } catch (FileNotFoundException e) {
            Log.e("FaceViewModel onChoosePicture", "Error reading file", e);
        }
        Bitmap mBitmap = bitmap.getValue();
        MainActivity.executorService.execute(new Runnable() {
             @Override
             public void run() {
                 String threadName = Thread.currentThread().getName();
                 Log.e("FaceViewModel run model", "线程：" + threadName+" begin");

                 float mImgScaleX = (float)mBitmap.getWidth() / FaceDetection.mInputWidth;
                 float mImgScaleY = (float)mBitmap.getHeight() / FaceDetection.mInputHeight;

//                 float mIvScaleX = 1;//(mBitmap.getWidth() > mBitmap.getHeight() ? mImageViewWidth / mBitmap.getWidth() : mImageViewHeight / mBitmap.getHeight());
//                 float mIvScaleY  = 1;//(mBitmap.getHeight() > mBitmap.getWidth() ? mImageViewHeight / mBitmap.getHeight() : mImageViewWidth / mBitmap.getWidth());
//
//                 float mStartX = 0;// (mImageViewWidth - mIvScaleX * mBitmap.getWidth())/2;
//                 float mStartY = 0;//(mImageViewHeight -  mIvScaleY * mBitmap.getHeight())/2;
                 float mIvScaleX = (mBitmap.getWidth() > mBitmap.getHeight() ? mImageViewWidth / mBitmap.getWidth() : mImageViewHeight / mBitmap.getHeight());
                 float mIvScaleY  = (mBitmap.getHeight() > mBitmap.getWidth() ? mImageViewHeight / mBitmap.getHeight() : mImageViewWidth / mBitmap.getWidth());

                 float mStartX = (mImageViewWidth - mIvScaleX * mBitmap.getWidth())/2;
                 float mStartY = (mImageViewHeight -  mIvScaleY * mBitmap.getHeight())/2;

                 ArrayList<com.example.myalbum.model.Result> results = FaceDetection.detect(mBitmap,mImgScaleX,mImgScaleY,mIvScaleX,mIvScaleY,mStartX,mStartY);
//                 int x = results.get(0).rect.left;
//                 int y = results.get(0).rect.top;
//                 int width = results.get(0).rect.right - x;
//                 int height =  results.get(0).rect.bottom-y;
//                 Bitmap bitmapFace = Bitmap.createBitmap(bitmap.getValue(),x,y,width,height);
//                 bitmap.postValue(bitmapFace);
//                 float[] featureVector =  FaceNet.getFeatureVector(bitmapFace);

                 resultList.postValue(results);
             }
        });

    }
}