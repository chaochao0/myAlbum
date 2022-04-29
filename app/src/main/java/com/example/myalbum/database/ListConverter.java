package com.example.myalbum.database;

import android.graphics.Rect;
import android.util.Log;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;

import org.pytorch.Tensor;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public String floatArrayToString(float[] array){
        Log.i("converter floatArrayToString", GsonInstance.getInstance().getGson().toJson(array));
        return GsonInstance.getInstance().getGson().toJson(array);
    }
    @TypeConverter
    public float[] stringToFloatArray(String json) {
        Type listType = new TypeToken<float[]>(){}.getType();
       // Log.i("converter stringToFloatList1", GsonInstance.getInstance().getGson().toJson(GsonInstance.getInstance().getGson().fromJson(json,listType)));
        return GsonInstance.getInstance().getGson().fromJson(json,listType);
    }
//    @TypeConverter
//    public String floatListToString(List<Float> list){
//        Log.i("converter floatListToString", GsonInstance.getInstance().getGson().toJson(list));
//        return GsonInstance.getInstance().getGson().toJson(list);
//    }
//    @TypeConverter
//    public List<Float> stringToFloatList(String json) {
//        Type listType = new TypeToken<List<Float>>(){}.getType();
//        return GsonInstance.getInstance().getGson().fromJson(json,listType);
//    }
    @TypeConverter
    public String rectListToString(List<Rect> list){
        Log.i("converter rectListToString", GsonInstance.getInstance().getGson().toJson(list));
        return GsonInstance.getInstance().getGson().toJson(list);
    }
    @TypeConverter
    public List<Rect> stringToRectList(String json) {
        Type listType = new TypeToken<List<Rect>>(){}.getType();
        return GsonInstance.getInstance().getGson().fromJson(json,listType);
    }

//    @TypeConverter
//    public String ListListToString(List<List<Float>> list){
//        Log.i("converter ListListToString", GsonInstance.getInstance().getGson().toJson(list));
//        return GsonInstance.getInstance().getGson().toJson(list);
//    }
//    @TypeConverter
//    public List<List<Float>> stringToListList(String json) {
//        Type listType = new TypeToken<List<List<Float>>>(){}.getType();
//        return GsonInstance.getInstance().getGson().fromJson(json,listType);
//    }

    @TypeConverter
    public String float2ArrayToString(float[][] array2){
        Log.i("converter float2ArrayToString", GsonInstance.getInstance().getGson().toJson(array2));
        return GsonInstance.getInstance().getGson().toJson(array2);
    }
    @TypeConverter
    public float[][] stringToFloat2Array(String json) {
        Type listType = new TypeToken<float[][]>(){}.getType();
        return GsonInstance.getInstance().getGson().fromJson(json,listType);
    }

    @TypeConverter
    public String intArrayToString(int[] array){
        Log.i("converter intArrayToString", GsonInstance.getInstance().getGson().toJson(array));
        return GsonInstance.getInstance().getGson().toJson(array);
    }
    @TypeConverter
    public int[] stringTointArray(String json) {
        Type listType = new TypeToken<int[]>(){}.getType();
        return GsonInstance.getInstance().getGson().fromJson(json,listType);
    }

    @TypeConverter
    public String rectToString(Rect rect){
        Log.i("converter rectToString", GsonInstance.getInstance().getGson().toJson(rect));
        return GsonInstance.getInstance().getGson().toJson(rect);
    }
    @TypeConverter
    public Rect stringToRect(String json) {
        Type listType = new TypeToken<Rect>(){}.getType();
        return GsonInstance.getInstance().getGson().fromJson(json,listType);
    }

}
