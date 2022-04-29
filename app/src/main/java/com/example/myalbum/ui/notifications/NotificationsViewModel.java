package com.example.myalbum.ui.notifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myalbum.MainActivity;
import com.example.myalbum.model.ImageTransfer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> picturePath;

    private MutableLiveData<Bitmap> newPicture;

    public NotificationsViewModel() {
        picturePath = new MutableLiveData<>();
        newPicture = new MutableLiveData<>();
    }

    public void onChoosePicture(String pictureFileName,int model_index, int size) {
        Log.i("NotificationsViewModel onChoosePicture","start");
        MainActivity.executorService.execute(new Runnable() {
             @Override
             public void run() {
                 String threadName = Thread.currentThread().getName();
                 Log.e("TAG", "线程：" + threadName);
                 picturePath.postValue(pictureFileName);
                 try {
                     Log.i("button 1", "on click start");
                     Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(pictureFileName));
                     Bitmap newBitmap = ImageTransfer.transfer(bitmap, model_index, size);
                     newPicture.postValue(newBitmap);
                 } catch (FileNotFoundException e) {
                     Log.e("initClickListener", "Error reading picturePath", e);
                 }
             }
         }
        );
    }

//        this.picturePath.setValue(pictureFileName);
//        try {
//            Log.i("button 1","on click start");
//            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(pictureFileName));
//            Bitmap newBitmap = ImageTransfer.transfer(bitmap,model_index,size);
//            newPicture.setValue(newBitmap);
//        } catch (FileNotFoundException e) {
//            Log.e("initClickListener", "Error reading picturePath", e);
//        }
//    }
    public LiveData<Bitmap> getNewPicture() {
        return newPicture;
    }
    public LiveData<String> getPath() {
        return picturePath;
    }
}