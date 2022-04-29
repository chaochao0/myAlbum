package com.example.myalbum.database;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.myalbum.MainActivity;

import java.util.List;

public class ImageRepository {
    private ImageDao mImageDao;
    private FaceDao mFaceDao;
    static public LiveData<List<Image>> mAllImages;
    static public int ImageNum = 0;

    static public LiveData<List<Face>> mAllFaces;
    static public int FaceNum = 0;

    static public LiveData<List<ImageWithFaceList>> mAllImageWithFaces;

    public ImageRepository(Context context){

        MyalbumDatabase db = MyalbumDatabase.getDatabase(context.getApplicationContext());
        mImageDao = db.imageDao();
        mAllImages = mImageDao.getImageList();
        ImageNum = 0;//mAllImages.getValue().size();
        mFaceDao = db.faceDao();

        mAllFaces = mFaceDao.getFaceList();
        FaceNum = 0;//mAllFaces.getValue().size();

        mAllImageWithFaces = mImageDao.getImageWithFaceList();
    }

    public LiveData<List<Image>> getAllImages(){
        return mAllImages;
    }

    public LiveData<List<Face>> getAllFaces(){return mAllFaces;};

    public Image getImage(String path){
        if(mAllImages.getValue() == null){
            return mImageDao.getImage(path);
        }
        for(Image i:mAllImages.getValue()){
            if(i.path == path){
                return i;
            }
        }
        return null;
    }

    public int getImageNum(){
        if(mAllImages.getValue() == null){
            Log.i("getImageNum","livedata is null");
            return mImageDao.getImageList().getValue().size();
        }
        Log.i("getImageNum","livedata is not null");
        return mAllImages.getValue().size();
    }

    public List<Face> getFacesOfImage(int imageId){
        List<ImageWithFaceList> temp = mAllImageWithFaces.getValue();
        for(int i =0;i<ImageNum;i++){
            if(temp.get(i).image.imageId==imageId){
                return temp.get(i).faceList;
            }
        }
        return null;
    }


    public void insertImage(Image image){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mImageDao.insert(image);
        });
        ImageNum+=1;
    }

    public void insertImage(List<Image> images){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mImageDao.insert(images);
        });
        ImageNum+=images.size();
    }

    public void insertFace(List<Face> faces){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mFaceDao.insert(faces);
        });
        FaceNum+=faces.size();
    }

    public void insertImageWithFaceList(Image image, List<Face> faces){
        for(Face f:faces){
            f.imageOwnerId = image.imageId;
        }
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mImageDao.insert(image);
            mFaceDao.insert(faces);
        });
        ImageNum+=1;
        FaceNum+=faces.size();
    }




//    //找到 face index 对应的照片index
//    public int findFaceImage(int faceIndex){
//        int sumFaceNum=0;
//        for(int i =0;i<ImageNum;i++){
//            int faceNum = mAllImages.getValue().get(i).faceNum;
//            if(faceIndex>=sumFaceNum&&faceIndex<sumFaceNum+faceNum){
//                return i;
//            }
//            sumFaceNum+=faceNum;
//        }
//    }

}
