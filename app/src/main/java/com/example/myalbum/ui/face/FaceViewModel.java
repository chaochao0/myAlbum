package com.example.myalbum.ui.face;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myalbum.MainActivity;
import com.example.myalbum.database.Face;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.database.ImageWithFaceList;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.Result;
import com.example.myalbum.retrieval.ImageRetrieval;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
//    private MutableLiveData<String> picturePath;
//    private MutableLiveData<Bitmap> bitmap;
//    private MutableLiveData<ArrayList<Result>> resultList;

    private LiveData<List<ImageWithFaceList>> mAllImageWithFaces;
    private MutableLiveData<List<Image>> retrievalResult;

    public FaceViewModel(@NonNull Application application){
        super(application);
        retrievalResult = new MutableLiveData<>();
        mAllImageWithFaces = ImageRepository.getImageRepositoryInstance().getAllImageWithFaceList();
//        Log.i("FaceViewModel","create");
//        picturePath = new MutableLiveData<>();
////        resultOrigin = new ArrayList<>();
//        resultList = new MutableLiveData<>();
//        AssetManager assetManager = application.getBaseContext().getAssets();
//        bitmap = new MutableLiveData<>();
//        try{
//            bitmap.setValue(BitmapFactory.decodeStream(assetManager.open("faceTest.jpg")));
//        }catch (IOException e) {
//            Log.e("FaceViewModel create", "Error reading assets", e);
//        }
    }

//    public LiveData<String> getPicturePath(){
//        return picturePath;
//    }
//
//    public LiveData<Bitmap> getBitmap(){
//        return bitmap;
//    }
    public LiveData<List<Image>> getRetrievalResult(){
        return retrievalResult;
    }

    public LiveData<List<ImageWithFaceList>> getAllImageWithFaces(){
        return mAllImageWithFaces;
    }





    public void onChoosePicture(String realPath) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(realPath));
        } catch (FileNotFoundException e) {
            Log.e("FaceViewModel onChoosePicture", "Error reading file", e);
        }

        Bitmap finalBitmap = bitmap;
        MainActivity.executorService.execute(new Runnable() {
             @Override
             public void run() {
                 String threadName = Thread.currentThread().getName();
                 Log.e("FaceViewModel run model", "线程：" + threadName+" begin");
                 //人脸检测
                 List<Face> faceList = new ArrayList<>();

                 float mImgScaleX = (float) finalBitmap.getWidth() / FaceDetection.mInputWidth;
                 float mImgScaleY = (float) finalBitmap.getHeight() / FaceDetection.mInputHeight;


                 ArrayList<Result> detectResult = FaceDetection.detect(finalBitmap,mImgScaleX,mImgScaleY,1,1,0,0);
                 List<Rect> rects = new ArrayList<>();


                 for(Result b:detectResult){
                     Rect a = b.rect;
                     int x = a.left > 0 ? a.left : 0;
                     a.left =x;
                     int y = a.top > 0 ? a.top : 0;
                     a.top =y;
                     int width = (a.right > finalBitmap.getWidth() ? finalBitmap.getWidth() : a.right) - x;
                     a.right = a.left+width;
                     int height = (a.bottom > finalBitmap.getHeight() ? finalBitmap.getHeight() : a.bottom) - y;
                     a.bottom = a.top+height;
                     if(width*height>22500&&b.score>=0.31&& (float)(width)/(float)height<1.45&&(float)height/(float)width<1.45){
                         rects.add(a);
                     }
                 }

                 for(Rect a:rects){
                     int x = a.left > 0 ? a.left : 0;
                     int y = a.top > 0 ? a.top : 0;
                     int width = (a.right > finalBitmap.getWidth() ? finalBitmap.getWidth() : a.right) - x;
                     int height =  (a.bottom > finalBitmap.getHeight() ? finalBitmap.getHeight() : a.bottom) - y;
                     Bitmap bitmapFace = Bitmap.createBitmap(finalBitmap,x,y,width,height);
                     float [] faceFeatures = FaceNet.getFeatureVector(bitmapFace);
                     Face face = new Face();
                     face.faceFeatures = faceFeatures;
                     face.rect = a;
                     faceList.add(face);
                 }
                 List<Image> result = new ArrayList<>();
                 if(faceList.size()<=0){
                    retrievalResult.postValue(result);
                 }
                 else{
                     result = ImageRetrieval.query(faceList,10);
                     retrievalResult.postValue(result);
                 }
             }


        });
    }

    public void clearRetrievalResult() {
        retrievalResult.setValue(null);
    }
}