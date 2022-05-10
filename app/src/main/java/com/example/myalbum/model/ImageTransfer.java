package com.example.myalbum.model;

import static android.graphics.Color.rgb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.service.controls.actions.FloatAction;
import android.util.Log;

import com.example.myalbum.R;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ImageTransfer {
    public static String[] ID_TO_TRANSFER_CLASSES = new String[]{
           "梵高--星夜", "蒙克--呐喊","索尼娅·德劳内--电动棱镜","艺术画--candy","现代画--mosaic","抽象画--rainPrincess","抽象画--udnie"
    };
    public static int[] drawableId = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.candy, R.drawable.mosaic, R.drawable.rain_princess, R.drawable.udnie};
    public static String[] assetsModelPath = new String[]{
            "models/1_transformer.pt", "models/2_transformer.pt","models/3_transformer.pt","models/candy_transformer.pt","models/mosaic_transformer.pt","models/rain_princess_transformer.pt","models/udnie_transformer.pt"
    };
    public static ArrayList<String> modelPath = new ArrayList<>();
    //endregion

    public static ArrayList<Module> model = new ArrayList<Module>();

    public ImageTransfer(ArrayList<String> modelPath_){
        Log.i("ImageTransfer","create");
        modelPath = modelPath_;
        for(String path:modelPath)
            Log.i("ImageTransfer path",path);
        for(String path:modelPath){
            model.add(Module.load(path));
            Log.i("ImageTransfer","create one model");
        }
    }


    /**
     * 传入图片预测性别
     * @param bitmap
     * @param model_index 0:星夜，1：呐喊，2：棱镜
     * @return className
     */
    public static Bitmap transfer(Bitmap bitmap, int model_index,int size){
        Log.i("transfer","start");
        Tensor tensor = preprocess(bitmap,size);
        for(int i =0;i<tensor.shape().length;i++){
            Log.i("transfer input shape",String.valueOf(tensor.shape()[i]));
        }
        Log.i("transfer","start2");
//        int j = 1;
//        for(float i: tensor.getDataAsFloatArray()){
//            j++;
//            Log.i("transfer",(new Float(i)).toString());
//            if(j>100){
//                break;
//            }
//        };
        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.get(model_index).forward(inputs).toTensor();
        for(int i =0;i<outputs.shape().length;i++){
            Log.i("transfer output shape",String.valueOf(outputs.shape()[i]));
        }
        int width = (int)outputs.shape()[3];
        int height = (int)outputs.shape()[2];
        Log.i("transfer output_width",String.valueOf(width));
        Log.i("transfer output_height",String.valueOf(height));
        Bitmap new_bitmap = floatArrayToBitmap(outputs.getDataAsFloatArray(),width,height);

        return new_bitmap;
    };

    public static Bitmap floatArrayToBitmap(float[] floatArray,int width,int height){
        Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        int[] pixels = new int[width*height*4];
//        int min = (int) Collections.min(Arrays.asList(floatArray));
//        int max = (int) Collections.max(Arrays.asList(floatArray));
//        Arrays.stream(floatArray).

        for(int i=0;i<width*height;i++){
            int r = Math.min(Math.max((int) floatArray[i], 0), 255);
            int g = Math.min(Math.max((int) floatArray[i + width * height], 0),255);
            int b = Math.min(Math.max((int) floatArray[i + 2 * width * height], 0),255);
            pixels[i]=rgb(r,g,b);
        }
        bmp.setPixels(pixels,0,width,0,0,width,height);
        return bmp;
    }

    /**
     * 对图片进行预处理
     * @param bitmap
     * @param size 调整图片到指定大小
     * @return Tensor
     */
    public static Tensor preprocess(Bitmap bitmap,int size){
        float[] mean =  {0.0f, 0.0f, 0.0f};
        float[] std = {0.003921568f,0.003921568f,0.003921568f};
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("preprocess origin_width",String.valueOf(width));
        Log.i("preprocess origin_height",String.valueOf(height));
        float ratio = ((float)width)/(float)height;
        int new_width = (int)(ratio>1 ? size*ratio: size);
        int new_height = (int)(ratio>1 ? size : size/ratio);
        Log.i("preprocess new_width",String.valueOf(new_width));
        Log.i("preprocess new_height",String.valueOf(new_height));

        bitmap = Bitmap.createScaledBitmap(bitmap,new_width,new_height,false);
        Tensor tensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                mean, std, MemoryFormat.CHANNELS_LAST);

        return tensor;
    };


    /**
     * 计算最大的概率
     * @param scores
     * @return index
     */
    public static int argMax(float[] scores){
        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        return maxScoreIdx;
    }
}
