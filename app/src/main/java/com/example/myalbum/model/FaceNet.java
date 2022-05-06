package com.example.myalbum.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FaceNet {
    public static Module model = null;

    public static int inputWidth = 160;
    public static int inputHeight = 160;

    public static int outputLength = 512;

    // for facenet model, no need to apply MEAN and STD
    static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};
    public FaceNet(String modelPath){
        Log.i("FaceNet create","start");
        model = Module.load(modelPath);
//        try {
//            Bitmap bitmap = (BitmapFactory.decodeStream(new FileInputStream(facepicture)));
//            getFeatureVector(bitmap);
//        } catch (FileNotFoundException e) {
//            Log.e("FaceViewModel onChoosePicture", "Error reading file", e);
//        }


    }

    public static float [] getFeatureVector(Bitmap bitmap){
//        Log.i("FaceNet getFeatureVector","start");
//        Log.i("FaceNet getFeatureVector","origin bitmap width:"+bitmap.getWidth());
//        Log.i("FaceNet getFeatureVector","origin bitmap height:"+bitmap.getHeight());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true);
//        Log.i("FaceNet getFeatureVector","scaled bitmap width:"+resizedBitmap.getWidth());
//        Log.i("FaceNet getFeatureVector","scaled bitmap height:"+resizedBitmap.getHeight());
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, NO_MEAN_RGB, NO_STD_RGB);
//        for(int i =0;i<inputTensor.shape().length;i++){
//            Log.i("inputTensor shape",String.valueOf(inputTensor.shape()[i]));
//        }
        IValue inputs = IValue.from(inputTensor);
        Tensor outputsTensor = model.forward(inputs).toTensor();
        float[] outputs = outputsTensor.getDataAsFloatArray();
//        IValue[] outputTuple = model.forward(IValue.from(inputTensor)).toTuple();
//        Log.i("FaceNet getFeatureVector","forward success");
//        final Tensor outputTensor = outputTuple[0].toTensor();
//        for(int i =0;i<outputsTensor.shape().length;i++){
//            Log.i("outputTensor shape",String.valueOf(outputsTensor.shape()[i]));
//        }
//        for(int i =0;i<outputs.length;i++){
//            if(i>=0&&i<=5){
//                Log.i("featurevector first 5",String.valueOf(outputs[i]));
//            }
//            if(i>=outputs.length-5){
//                Log.i("featurevector end 5",String.valueOf(outputs[i]));
//            }
//        }
        return outputs;
    }
}
