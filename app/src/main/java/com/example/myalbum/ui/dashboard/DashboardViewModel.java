package com.example.myalbum.ui.dashboard;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myalbum.GlideEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import org.pytorch.IValue;
//import org.pytorch.LiteModuleLoader;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<String> className;
    private MutableLiveData<Bitmap> picture;
    Module module;
    public DashboardViewModel(@NonNull Application application) {
        super(application);

        AssetManager assetManager = application.getBaseContext().getAssets();
        Log.i("DashboardViewModel","create");

            Log.i("DashboardViewModel","is null");
            className = new MutableLiveData<>();
            picture = new MutableLiveData<>();
            try{
                picture.setValue(BitmapFactory.decodeStream(assetManager.open("2.jpg")));
//            module = Module.load(assetFilePath(application,"mobilenetv3_large_161_66acc.pt"));
            }catch (IOException e) {
                Log.e("PytorchHelloWorld", "Error reading assets", e);
            }
//        model_eval();
            className.setValue(ImageClassifier.predict(picture.getValue(),224));

    }

    public LiveData<String> getClassName() {
        return className;
    }

    public LiveData<Bitmap> getPicture(){
        return picture;
    }

    public void model_eval(){
        // preparing input tensor
        Bitmap  bitmap = Bitmap.createScaledBitmap(picture.getValue(),224,224,false);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
        System.out.println(inputTensor.toString());
        for(int i =0;i<10;i++)
            System.out.println(inputTensor.getDataAsFloatArray()[i]);
        for(long i:inputTensor.shape()){
            System.out.println(i);
        };
        // running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        System.out.println(scores.length);
        System.out.println(maxScoreIdx);
        System.out.println("hahahahha");
        className.setValue(ImageClassifier.IMAGE_CLASSES[maxScoreIdx]);
    }

    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    public static String assetFilePath(Context context, String assetName) throws IOException {
        System.out.println("picture file dir:" + context.getFilesDir());
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public void onChoosePicture(String filePath){
            try{
                picture.setValue(BitmapFactory.decodeStream(new FileInputStream(filePath)));
            }catch (IOException e) {
                Log.e("onChoosePicture", "Error reading file", e);
            }
            className.setValue(ImageClassifier.predict(picture.getValue(),224));
    }

}