package com.example.myalbum.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.example.myalbum.GlideEngine;
import com.example.myalbum.MyApplication;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.ui.dashboard.DashboardFragment;
import com.google.gson.Gson;
import com.luck.picture.lib.interfaces.OnCallbackListener;

import org.pytorch.IValue;
//import org.pytorch.LiteModuleLoader;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageClassifier {
    public static String[] FIRST_CLASSES = new String[]{
            "风景",
            "地点",
            "建筑",
            "交通",
            "运动",
            "植物",
            "活动",
            "儿童",
            "美食",
            "动物",
            "物品"
    };
    //region classNames
    public static String[] IMAGE_CLASSES = new String[]{
            "交通/充气艇",
            "交通/公交车",
            "交通/加油站",
            "交通/地铁",
            "交通/林区道路",
            "交通/汽车",
            "交通/火车",
            "交通/直升机",
            "交通/自动扶梯",
            "交通/船",
            "交通/赛车",
            "交通/车舱",
            "交通/铁轨",
            "交通/飞机",
            "交通/高速公路",
            "儿童/儿童房间",
            "儿童/婴儿房",
            "儿童/幼儿园",
            "儿童/操场",
            "儿童/旋转木马",
            "儿童/沙箱",
            "儿童/海洋球",
            "儿童/玩具",
            "儿童/玩具商店",
            "动物/家禽",
            "动物/野生动物",
            "地点/书店",
            "地点/停车场",
            "地点/公园",
            "地点/办公室",
            "地点/医院",
            "地点/卧室",
            "地点/卫生间",
            "地点/厨房",
            "地点/大厅",
            "地点/大商场",
            "地点/大礼堂",
            "地点/娱乐室",
            "地点/家用餐厅",
            "地点/小巷",
            "地点/店面",
            "地点/快餐店",
            "地点/教室",
            "地点/杂货店",
            "地点/水族馆",
            "地点/淋浴室",
            "地点/游乐场",
            "地点/游戏厅",
            "地点/游泳馆",
            "地点/玉米田",
            "地点/生产车间",
            "地点/电影院",
            "地点/百货商场",
            "地点/科学博物馆",
            "地点/自然历史博物馆",
            "地点/舞厅",
            "地点/艺术展览",
            "地点/街道",
            "地点/超市",
            "地点/酒吧",
            "地点/集市",
            "地点/飞机座舱",
            "地点/餐厅",
            "建筑/乡间别墅",
            "建筑/佛塔",
            "建筑/公寓大楼",
            "建筑/凉亭",
            "建筑/古代遗迹",
            "建筑/城堡",
            "建筑/寺庙",
            "建筑/小木屋",
            "建筑/建筑物正面",
            "建筑/房子住宅",
            "建筑/拱门",
            "建筑/摩天大楼",
            "建筑/教堂",
            "建筑/桥",
            "建筑/楼房__",
            "建筑/沟渠桥",
            "建筑/法院大楼",
            "建筑/灯塔",
            "建筑/谷仓_",
            "建筑/高架桥",
            "植物/植物",
            "植物/沙漠植物",
            "植物/蔬菜",
            "植物/鲜花",
            "活动/会议",
            "活动/创作",
            "活动/宴会",
            "活动/演出",
            "活动/演奏",
            "活动/美容",
            "活动/聚餐_酒席",
            "活动/聚餐_餐厅",
            "活动/跳舞",
            "活动/野营",
            "活动/音乐",
            "物品/泳池",
            "物品/珠宝首饰",
            "物品/窗户",
            "物品/衣柜",
            "物品/衣饰",
            "物品/计算机",
            "美食/冰激凌",
            "美食/披萨",
            "美食/熟食",
            "美食/糖果",
            "美食/肉",
            "美食/面包糕点",
            "运动/保龄球",
            "运动/健身",
            "运动/冲浪",
            "运动/室内溜冰",
            "运动/室外溜冰",
            "运动/排球",
            "运动/搏击",
            "运动/斗牛",
            "运动/棒球",
            "运动/武术",
            "运动/滑雪",
            "运动/篮球",
            "运动/足球",
            "运动/足球场",
            "运动/运动场",
            "运动/骑马",
            "运动/高尔夫",
            "风景/丛林",
            "风景/乡村",
            "风景/冰原裂缝",
            "风景/冰川",
            "风景/城市",
            "风景/天空",
            "风景/孤峰",
            "风景/小溪",
            "风景/小路",
            "风景/山崖",
            "风景/山洞",
            "风景/峡谷",
            "风景/水底",
            "风景/沙漠",
            "风景/河流",
            "风景/海岛",
            "风景/海岸",
            "风景/海滩",
            "风景/湖泊",
            "风景/湿地",
            "风景/瀑布",
            "风景/火山",
            "风景/田地",
            "风景/田野",
            "风景/稻田",
            "风景/竹林",
            "风景/草地",
            "风景/荒地",
            "风景/雨林",
            "风景/雪地",
            "风景/雪山",
            "风景/高山",
            "风景/鱼塘",
            "风景/麦田"
    };
    //endregion

    public static Module model = null;

    public ImageClassifier(String modelPath){

        model = Module.load(modelPath);
        Log.i("imageclassifier","created");
    }


    /**
     * 传入图片预测性别
     * @param bitmap
     * @param size 规定传入的图片要符合一个大小标准，这里是224*224
     * @return className
     */
    public static List<Object> predict(Bitmap bitmap, int size){
//        Log.i("imagePredict","begin");
        Tensor tensor = preprocess(bitmap,size);
//        for(int i =0;i<tensor.shape().length;i++){
//            Log.i("predict bitmap shape",String.valueOf(tensor.shape()[i]));
//        }

        IValue inputs = IValue.from(tensor);
        IValue[] outputs = model.forward(inputs).toTuple();
        Tensor features= outputs[0].toTensor();
        Tensor pres = outputs[1].toTensor();
//        Log.i("featureList length",String.valueOf(featureList.length));
//        for(int i =0;i<features.shape().length;i++){
//            Log.i("features shape",String.valueOf(features.shape()[i]));
//        }
//        for(int i =0;i<pres.shape().length;i++){
//            Log.i("pres shape",String.valueOf(pres.shape()[i]));
//        }
        float[] scores = pres.getDataAsFloatArray();
//        Tensor outputs = model.forward(inputs).toTensor();
//        float[] scores = outputs.getDataAsFloatArray();
        int classIndex = argMax(scores);
//        Log.i("pres score",String.valueOf(scores[classIndex]));
        List<Object> r = new ArrayList<Object>();
//        r.add(IMAGE_CLASSES[classIndex]);
        r.add(classIndex);
        r.add(features.getDataAsFloatArray());
        return r;
    };

    //必须在线程中调用
    public static List<Object> predict(String path){
        FutureTarget<Bitmap> futureTarget =
        Glide.with(MyApplication.getContext())
                .asBitmap()
                .override(224)
                .centerCrop()
                .load(path)
                .submit();

        Bitmap bitmap = null;
        try {
            bitmap = futureTarget.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("predict bitmap height",String.valueOf(bitmap.getHeight()));
        Log.i("predict bitmap width",String.valueOf(bitmap.getWidth()));
        final Tensor tensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
        // Do something with the Bitmap and then when you're done with it:


        Log.i("tensor shape", GsonInstance.getInstance().getGson().toJson(tensor.shape()));

        IValue inputs = IValue.from(tensor);
        if(inputs == null){
            Log.i("isnull","fsdafsaf");
        }
        IValue[] outputs = model.forward(inputs).toTuple();
        Tensor features= outputs[0].toTensor();
        Tensor pres = outputs[1].toTensor();
//        Log.i("featureList length",String.valueOf(featureList.length));
        for(int i =0;i<features.shape().length;i++){
            Log.i("features shape",String.valueOf(features.shape()[i]));
        }
        for(int i =0;i<pres.shape().length;i++){
            Log.i("pres shape",String.valueOf(pres.shape()[i]));
        }
        float[] scores = pres.getDataAsFloatArray();
//        Tensor outputs = model.forward(inputs).toTensor();
//        float[] scores = outputs.getDataAsFloatArray();
        int classIndex = argMax(scores);
        Log.i("pres score",String.valueOf(scores[classIndex]));
        List<Object> r = new ArrayList<Object>();
//        r.add(IMAGE_CLASSES[classIndex]);
        r.add(classIndex);
        r.add(features.getDataAsFloatArray());
        Glide.with(MyApplication.getContext()).clear(futureTarget);
        return r;
    };
    /**
     * 对图片进行预处理
     * @param bitmap
     * @param size 调整图片到指定大小
     * @return Tensor
     */
    public static Tensor preprocess(Bitmap bitmap,int size){

        bitmap = Bitmap.createScaledBitmap(bitmap,size,size,false);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
        return inputTensor;
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
