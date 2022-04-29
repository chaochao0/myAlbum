package com.example.myalbum.model;

import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

;

public class FaceDetection {
    public static Module model;
    public static String[] mClasses = {"face"};

    // model input image size
    public static int mInputWidth = 640;
    public static int mInputHeight = 640;

    // model output is of size 25200*(num_of_class+5)
    private static int mOutputRow = 25200; // as decided by the YOLOv5 model for input image of size 640*640
    private static int mOutputColumn = 6; // left, top, right, bottom, score and 80 class probability
    private static float mThreshold = 0.30f; // score above which a detection is generated
    private static int mNmsLimit = 15;

    // for yolov5 model, no need to apply MEAN and STD
    static float[] NO_MEAN_RGB = new float[] {0.0f, 0.0f, 0.0f};
    static float[] NO_STD_RGB = new float[] {1.0f, 1.0f, 1.0f};


    public FaceDetection(String modelPath){
        Log.i("FaceDetection create","start");
        model = Module.load(modelPath);
    }

    public static ArrayList<Result> detect(Bitmap bitmap,float mImgScaleX, float mImgScaleY, float mIvScaleX, float mIvScaleY, float mStartX, float mStartY){
        Log.i("FaceDetection detect","start");
        Log.i("FaceDetection detect","origin bitmap width:"+bitmap.getWidth());
        Log.i("FaceDetection detect","origin bitmap height:"+bitmap.getHeight());
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, mInputWidth, mInputHeight, true);
        Log.i("FaceDetection detect","scaled bitmap width:"+resizedBitmap.getWidth());
        Log.i("FaceDetection detect","scaled bitmap height:"+resizedBitmap.getHeight());
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, NO_MEAN_RGB, NO_STD_RGB);
        for(int i =0;i<inputTensor.shape().length;i++){
            Log.i("inputTensor shape",String.valueOf(inputTensor.shape()[i]));
        }
        IValue[] outputTuple = model.forward(IValue.from(inputTensor)).toTuple();
        Log.i("FaceDetection detect","forward success");
        final Tensor outputTensor = outputTuple[0].toTensor();
        for(int i =0;i<outputTensor.shape().length;i++){
            Log.i("outputTensor shape",String.valueOf(outputTensor.shape()[i]));
        }
        final float[] outputs = outputTensor.getDataAsFloatArray();
        Log.i("outputs length",String.valueOf(outputs.length));
        final ArrayList<Result> results =  outputsToNMSPredictions(outputs, mImgScaleX, mImgScaleY, mIvScaleX, mIvScaleY, mStartX, mStartY);
//        Log.i("printResult",results.)

        //加上过滤条件：  1，score>0.3 2.人脸大于10000 3.长宽比比例小于1.5

        return results;

//        runOnUiThread(() -> {
//            mButtonDetect.setEnabled(true);
//            mButtonDetect.setText(getString(R.string.detect));
//            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//            mResultView.setResults(results);
//            mResultView.invalidate();
//            mResultView.setVisibility(View.VISIBLE);
//        });

    }

    //输出非极大值抑制处理后的结果
    static ArrayList<Result> outputsToNMSPredictions(float[] outputs, float imgScaleX, float imgScaleY, float ivScaleX, float ivScaleY, float startX, float startY) {
        Log.i("FaceDetection outputsToNMSPredictions","start");
        ArrayList<Result> results = new ArrayList<>();
        for (int i = 0; i< mOutputRow; i++) {
            if (outputs[i* mOutputColumn +4] > mThreshold) {
                float x = outputs[i* mOutputColumn];
                float y = outputs[i* mOutputColumn +1];
                float w = outputs[i* mOutputColumn +2];
                float h = outputs[i* mOutputColumn +3];

                float left = imgScaleX * (x - w/2);
                float top = imgScaleY * (y - h/2);
                float right = imgScaleX * (x + w/2);
                float bottom = imgScaleY * (y + h/2);

                float max = outputs[i* mOutputColumn +5];
                int cls = 0;
                for (int j = 0; j < mOutputColumn -5; j++) {
                    if (outputs[i* mOutputColumn +5+j] > max) {
                        max = outputs[i* mOutputColumn +5+j];
                        cls = j;
                    }
                }

                Rect rect = new Rect((int)(startX+ivScaleX*left), (int)(startY+top*ivScaleY), (int)(startX+ivScaleX*right), (int)(startY+ivScaleY*bottom));
                Result result = new Result(cls, outputs[i*mOutputColumn+4], rect);
                results.add(result);
            }
        };
        Log.i("FaceDetection outputsToNMSPredictions","end");

        return nonMaxSuppression(results, mNmsLimit, mThreshold);
    }

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     Removes bounding boxes that overlap too much with other boxes that have
     a higher score.
     - Parameters:
     - boxes: an array of bounding boxes and their scores
     - limit: the maximum number of boxes that will be selected
     - threshold: used to decide whether boxes overlap too much
     */
    static ArrayList<Result> nonMaxSuppression(ArrayList<Result> boxes, int limit, float threshold) {

        // Do an argsort on the confidence scores, from high to low.
        Collections.sort(boxes,
                new Comparator<Result>() {
                    @Override
                    public int compare(Result o1, Result o2) {
                        return o1.score.compareTo(o2.score);
                    }
                });

        ArrayList<Result> selected = new ArrayList<>();
        boolean[] active = new boolean[boxes.size()];
        Arrays.fill(active, true);
        int numActive = active.length;

        // The algorithm is simple: Start with the box that has the highest score.
        // Remove any remaining boxes that overlap it more than the given threshold
        // amount. If there are any boxes left (i.e. these did not overlap with any
        // previous boxes), then repeat this procedure, until no more boxes remain
        // or the limit has been reached.
        boolean done = false;
        for (int i=0; i<boxes.size() && !done; i++) {
            if (active[i]) {
                Result boxA = boxes.get(i);
                selected.add(boxA);
                if (selected.size() >= limit) break;

                for (int j=i+1; j<boxes.size(); j++) {
                    if (active[j]) {
                        Result boxB = boxes.get(j);
                        if (IOU(boxA.rect, boxB.rect) > threshold) {
                            active[j] = false;
                            numActive -= 1;
                            if (numActive <= 0) {
                                done = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        int index=0;
        for(Result r:selected){
            r.faceIndex = index;
            index++;
        }
        return selected;
    }

    /**
     Computes intersection-over-union overlap between two bounding boxes.
     */
    static float IOU(Rect a, Rect b) {
        float areaA = (a.right - a.left) * (a.bottom - a.top);
        if (areaA <= 0.0) return 0.0f;

        float areaB = (b.right - b.left) * (b.bottom - b.top);
        if (areaB <= 0.0) return 0.0f;

        float intersectionMinX = Math.max(a.left, b.left);
        float intersectionMinY = Math.max(a.top, b.top);
        float intersectionMaxX = Math.min(a.right, b.right);
        float intersectionMaxY = Math.min(a.bottom, b.bottom);
        float intersectionArea = Math.max(intersectionMaxY - intersectionMinY, 0) *
                Math.max(intersectionMaxX - intersectionMinX, 0);
        return intersectionArea / (areaA + areaB - intersectionArea);
    }

}
