package com.example.myalbum.retrieval;


import android.util.Log;

import com.example.myalbum.database.Face;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.ImageRepository;

import java.util.List;
import java.util.Vector;

// DBSCAN is the only algorithm that doesn't require the number of clusters to be defined.
//https://github.com/davidsandberg/facenet/blob/096ed770f163957c1e56efa7feeb194773920f6e/contributed/cluster.py#L149
public class FaceCluster {
    float eps = 0.65f;   //0.75
    int minPts = 4;     //2
    static public float[][] matrix;
    int faceNum;

    static public int[] cid; //cluser class id 聚类结果类别id -1：噪声点  0：初始为0,表示未分类  >0: 聚类后的类别id

    public FaceCluster(float eps_, int minPts_) {
        eps = eps_;
        minPts = minPts_;
//        List<Image> images = ImageRepository.mAllImages.getValue();
        List<Face> faces = ImageRepository.mAllFaces.getValue();
        faceNum = faces.size();

        cid = new int[faceNum];

        matrix = new float[faceNum][faceNum];
        for (int i = 0; i < faceNum; i++) {
            matrix[i][i] = 0;
            for (int j = i + 1; j < faceNum; j++) {
                matrix[i][j] = ComputeDistance(faces.get(i).faceFeatures, faces.get(j).faceFeatures);
                matrix[j][i] = matrix[i][j];
            }
        }
        System.out.println(GsonInstance.getInstance().getGson().toJson(matrix));
        dbscan();
    }

    //测试算法正确性使用
    public FaceCluster() {
        final double[][] points = {
                {3.0, 8.04},
                {4.0, 7.95},
                {4.4, 8.58},
                {3.6, 8.81},
                {5.0, 8.33},
                {6.0, 6.96},
                {17.0, 4.24},
                {18.0, 4.26},
                {16.0, 3.84},
                {17.0, 4.82},
                {15.0, 5.68},
                {17.0, 5.68},
                {11.0, 10.68},
                {13.0, 9.68},
                {11.8, 10.0},
                {12.0, 11.18},
                {8.0, 12.0},
                {9.2, 9.68},
                {8.8, 11.2},
                {10.0, 11.4},
                {7.0, 9.68},
                {6.1, 10.68},
                {5.70, 1.68},
                {5.0, 2.68},
                {12.0, 0.68}
        };
        eps = 1.f;
        minPts = 1;
        faceNum = 25;
        cid = new int[faceNum];

        matrix = new float[faceNum][faceNum];
        for (int i = 0; i < faceNum; i++) {
            matrix[i][i] = 0;
            for (int j = i + 1; j < faceNum; j++) {
                matrix[i][j] = ComputeDistance(points[i], points[j]);
                matrix[j][i] = matrix[i][j];
            }
        }
        dbscan();
        for (int i = 0; i < 25; i++) {
            if (cid[i] != -1) {
                cid[i]--;
            }
        }
        Log.i("faceCluster test length", String.valueOf(cid.length));
        Log.i("faceCluster test", " ");
        System.out.println(GsonInstance.getInstance().getGson().toJson(cid));
        System.out.println(GsonInstance.getInstance().getGson().toJson(matrix));

    }

    public float ComputeDistance(double[] a, double[] b) {
        float sum = 0.0f;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow((a[i] - b[i]), 2);
        }
        float dist = (float) Math.sqrt(sum);
        return dist;
    }

    public float ComputeDistance(float[] a, float[] b) {
        float sum = 0.0f;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow((a[i] - b[i]), 2);
        }
        float dist = (float) Math.sqrt(sum);
        return dist;
    }


    //由于自己到自己的距离是0,所以自己也是自己的neighbor
    public Vector<Integer> getNeighbors(int p) {
        Vector<Integer> neighbors = new Vector<>();
        for (int i = 0; i < faceNum; i++) {
            if (matrix[p][i] <= eps) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    public int dbscan() {
        int clusterID = 0;

        boolean[] visited = new boolean[faceNum];

        for (int i = 0; i < faceNum; i++)
            visited[i] = false;

        for (int i = 0; i < faceNum; i++) {
            if (visited[i]) {
                continue;
            }
            visited[i] = true;
            Vector<Integer> neighbors = getNeighbors(i);
            if (neighbors.size() < minPts) {
                cid[i] = -1; //cid初始为0,表示未分类；分类后设置为一个正数；设置为-1表示噪声。
            } else {
                clusterID++;
                expandCluster(i, neighbors, clusterID, visited);
            }
        }
        return clusterID;
    }

    private void mergeList(Vector<Integer> toList, Vector<Integer> fromList) {
        for (int i = 0; i < fromList.size(); i++) {
            if (toList.contains(fromList.get(i))) {
                continue;
            }
            toList.add(fromList.get(i));
        }
    }

    private void expandCluster(int i, Vector<Integer> neighbors, int clusterID, boolean[] visited) {
        cid[i] = clusterID;
        for (int j = 0; j < neighbors.size(); j++) {
            if (!visited[neighbors.get(j)]) {
                visited[neighbors.get(j)] = true;
                Vector<Integer> jneighbors = getNeighbors(neighbors.get(j));
                if (jneighbors.size() >= minPts) {
                    mergeList(neighbors, jneighbors);
                }
            }
            if (cid[neighbors.get(j)] <= 0) {
                cid[neighbors.get(j)] = clusterID;
            }
        }
    }
}

