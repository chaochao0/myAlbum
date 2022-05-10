package com.example.myalbum.retrieval;

import static java.lang.Thread.sleep;

import com.example.myalbum.database.Face;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.database.ImageWithFaceList;

import java.util.ArrayList;
import java.util.List;

public class ImageRetrieval {

    public static List<Image> query(List<Face> faceList, int topNum){
        List<Image> results = new ArrayList<>(topNum);
        List<Float> scores = new ArrayList<>();
        while(ImageRepository.getImageRepositoryInstance().getAllImageWithFaceList().getValue() == null) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<ImageWithFaceList> imageWithFaceList = ImageRepository.getImageRepositoryInstance().getAllImageWithFaceList().getValue();

        for(ImageWithFaceList i:imageWithFaceList){
            if(i.faceList==null||i.faceList.size()<=0){
                scores.add(new Float(100));
                continue;
            }
            float scoreMin = 100;
            for(Face desFace:i.faceList){
                for(Face oriFace:faceList){
                    float score = FaceCluster.ComputeDistance(oriFace.faceFeatures,desFace.faceFeatures);
                    if(score<scoreMin){
                        scoreMin = score;
                    }
                }
            }
            scores.add(new Float(scoreMin));
        }
        for(int k = 0; k< topNum; k++){
            float min = 100;
            int topkIndex = -1;
            for(int i=0;i<scores.size()-1;i++){
                if(scores.get(i).floatValue()<min){
                    min = scores.get(i).floatValue();
                    topkIndex = i;
                }
            }
            System.out.println("faceRetrievalResultScores: "+"imageindex:"+topkIndex+" "+scores.get(topkIndex));
            scores.set(topkIndex,new Float(100.0));
            results.add(imageWithFaceList.get(topkIndex).image);
        }

        return results;
    }

    public static List<Image> query(Image image,int topNum){
        List<Image> results = new ArrayList<>(topNum);
        List<Float> scores = new ArrayList<>();
        for(Image i: ImageRepository.mAllImages.getValue()){
            if(image.path.equals(i.path)  || i.imageFeatures == null || i.imageFeatures.length<=0){
                scores.add(new Float(-1.0));
                continue;
            }
            float score = innerProduct(i.imageFeatures,image.imageFeatures);
            scores.add(new Float(score));
        }
        for(int k = 0; k< topNum; k++){
            float max = -1;
            int topkIndex = -1;
            for(int i=0;i<scores.size()-1;i++){
                if(scores.get(i).floatValue()>max){
                    max = scores.get(i).floatValue();
                    topkIndex = i;
                }
            }
            System.out.println("imageRetrievalResultScores: "+"imageindex:"+topkIndex+" "+scores.get(topkIndex));
            scores.set(topkIndex,new Float(-1.0));
            results.add(ImageRepository.mAllImages.getValue().get(topkIndex));
        }
        return results;
    }


    static float innerProduct(float[] x, float[] y) {

        float sum = 0.0F;

        for (int i = 0; i < x.length; ++i)

            sum = sum + x[i] * y[i];

        return sum;

    }

//    public static int[] argsort(float[] input) {
//
//        int[] rs =  new int[input.length];
//
//        for(int i=0;i<input.length;i++){
//            rs[i] = i;
//        }
//
//        for(int i=0;i<input.length-1;i++) {
//            for(int j=i+1;j<input.numRows;j++) {
//                if(input.get(i,0) > input.get(j, 0)) {
//
//                    double tmp = input.get(j, 0);
//                    int tmpIndex = rs[j];
//
//                    input.set(j, 0, input.get(i,0));
//                    input.set(i, 0, tmp);
//
//                    rs[j] = rs[i];
//                    rs[i] = tmpIndex;
//
//                }
//            }
//        }
//
//        return rs;
//    }
}
