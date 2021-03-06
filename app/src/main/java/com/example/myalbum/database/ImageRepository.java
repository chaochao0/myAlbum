package com.example.myalbum.database;

import static java.lang.Thread.sleep;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myalbum.MainActivity;
import com.example.myalbum.MyApplication;
import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.model.Result;
import com.example.myalbum.retrieval.FaceCluster;
import com.example.myalbum.ui.home.HomeFragment;
import com.example.myalbum.utils.DateUtil;
import com.example.myalbum.utils.UIUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageRepository {
    private ImageDao mImageDao;
    private FaceDao mFaceDao;
    static public LiveData<List<Image>> mAllImages;
    static public int ImageNum = 0;

    static public LiveData<List<Face>> mAllFaces;
    static public int FaceNum = 0;

    static public LiveData<List<ImageWithFaceList>> mAllImageWithFaces;

    private static ImageRepository INSTANCE = null;

    public ImageRepository(Context context){
        MyalbumDatabase db = MyalbumDatabase.getDatabase(context);
        mImageDao = db.imageDao();
        mAllImages = mImageDao.getImageList();
        ImageNum = 0;//mAllImages.getValue().size();
        mFaceDao = db.faceDao();

        mAllFaces = mFaceDao.getFaceList();
        FaceNum = 0;//mAllFaces.getValue().size();

        mAllImageWithFaces = mImageDao.getImageWithFaceList();
        if(MyApplication.isFirstOpen){
            Log.i("ImageRepositoryCreateIsFirstOpen","is firstopen");
//            initMydatabase();
        }
        else{
            Log.i("ImageRepositoryCreateIsNotFirstOpen ","is not firstopen");
            readFromDatabase();
            //            mPhotoWithDay.setValue(AndroidPhotoScanner.getPhotoSections());
        }
    }

    private void readFromDatabase() {
//        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
//            List<Image> images= mImageDao.getImageListNow();
//
//            for(int i=0;i<7;i++){
//                this.mImageDao.deleteImage(images.get(i));
//            }
//        });

    }

    public static ImageRepository getImageRepositoryInstance(){
        if (INSTANCE == null) {
            synchronized (ImageRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImageRepository(UIUtils.getContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<Image>> getAllImages(){
        return mAllImages;
    }

    public LiveData<List<Face>> getAllFaces(){return mAllFaces;};

    public LiveData<List<ImageWithFaceList>> getAllImageWithFaceList(){
        return mAllImageWithFaces;
    }



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

    public void deleteAllImages(){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mImageDao.deleteImage(mImageDao.getImageListNow());
        });

    }
    public void deleteAllIFaces(){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mFaceDao.deleteFace(mFaceDao.getFaceListNow());
        });

    }
    public void deleteImageWithFaceList(List<ImageWithFaceList> imageWithFaceList){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            for(ImageWithFaceList i:imageWithFaceList){
                mImageDao.deleteImage(i.image);
                mFaceDao.deleteFace(i.faceList);
            }
        });

    }
    public void insertImageWithFaceList(List<ImageWithFaceList> imageWithFaceList){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            for(ImageWithFaceList i:imageWithFaceList){

                long imagid = mImageDao.insert(i.image);
                for(Face f:i.faceList){
                    f.imageOwnerId = (int)imagid;
                }
                mFaceDao.insert(i.faceList);
                System.out.println("hlhafk"+imagid);
            }

        });

    }


    public void updateImage(Image image){
        MyalbumDatabase.databaseWriteExecutor.execute(() -> {
            mImageDao.updateImage(image);
        });
    }



//    //?????? face index ???????????????index
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

    //??????????????????????????? ?????????????????? ????????????????????????
    public void initMydatabase(){
        MyalbumDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                Log.i("beginScanPhoto 1", "?????????"+threadName);
                AndroidPhotoScanner photoScanner = new AndroidPhotoScanner();
                AndroidPhotoScanner.startScan();
                ImageRepository.getImageRepositoryInstance().deleteAllImages();  //???????????????
                ImageRepository.getImageRepositoryInstance().deleteAllIFaces();
                if(ImageClassifier.model==null||FaceDetection.model==null||FaceNet.model == null){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("initMydatabase 1","start");

                List<Image> imageList = new ArrayList<>();
                List<Face> faceListAll = new ArrayList<>();
                //insert
                Log.i("initMydatabase 2","image size"+String.valueOf(AndroidPhotoScanner.mDefaultFolder.getList().size()));
                int imageId = 0;
                int faceId = 0;

                long costTimeSum = 0;
                for(PhotoItem photo:AndroidPhotoScanner.mDefaultFolder.getList())
                {

                    long startTime = System.currentTimeMillis();
                    Image image = new Image();
                    image.imageId = imageId;
                    image.path = photo.getPath();
                    image.date = photo.getModified();
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(new FileInputStream(photo.getPath()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        continue;
                    }
                    //????????????
                    List<Object> predictResult = ImageClassifier.predict(bitmap,224);

                    image.classIndex =(int)predictResult.get(0);
                    image.imageFeatures = (float[]) predictResult.get(1);
                    Log.i("initMydatabase classIndex",String.valueOf(image.classIndex));

                    //????????????
                    List<Face> faceList = new ArrayList<>();

                    float mImgScaleX = (float)photo.getImageWidth() / FaceDetection.mInputWidth;
                    float mImgScaleY = (float)photo.getImageHeight() / FaceDetection.mInputHeight;


                    ArrayList<Result> detectResult = FaceDetection.detect(bitmap,mImgScaleX,mImgScaleY,1,1,0,0);
                    List<Rect> rects = new ArrayList<>();


                    for(Result b:detectResult){
                        Rect a = b.rect;
                        int x = a.left > 0 ? a.left : 0;
                        a.left =x;
                        int y = a.top > 0 ? a.top : 0;
                        a.top =y;
                        int width = (a.right > photo.getImageWidth() ? photo.getImageWidth() : a.right) - x;
                        a.right = a.left+width;
                        int height = (a.bottom > photo.getImageHeight() ? photo.getImageHeight() : a.bottom) - y;
                        a.bottom = a.top+height;
                        if(width*height>22500&&b.score>=0.31&& (float)(width)/(float)height<1.45&&(float)height/(float)width<1.45){
                            rects.add(a);
                        }
                    }

                    image.faceNum = rects.size();

                    for(Rect a:rects){
                        int x = a.left > 0 ? a.left : 0;
                        int y = a.top > 0 ? a.top : 0;
                        int width = (a.right > photo.getImageWidth() ? photo.getImageWidth() : a.right) - x;
                        int height =  (a.bottom > photo.getImageHeight() ? photo.getImageHeight() : a.bottom) - y;
                        Bitmap bitmapFace = Bitmap.createBitmap(bitmap,x,y,width,height);
                        float [] faceFeatures = FaceNet.getFeatureVector(bitmapFace);
                        Face face = new Face();
                        face.faceId = faceId;
                        faceId++;
                        face.faceFeatures = faceFeatures;
                        face.rect = a;
                        face.imageOwnerId = image.imageId;
                        faceList.add(face);
                    }
                    imageList.add(image);
                    faceListAll.addAll(faceList);


                    long endTime = System.currentTimeMillis();
                    costTimeSum+=(endTime - startTime);

                    System.out.println("imageId:"+image.imageId);
                    System.out.println("path:"+image.path);
                    System.out.println("classIndex:"+image.classIndex);
                    System.out.println("faceNum:"+image.faceNum);
                    for(int j = 0;j<image.faceNum;j++){
                        System.out.println("    face"+j+" faceId:"+faceList.get(j).faceId);
                        System.out.println("    face"+j+" ownerId:"+faceList.get(j).imageOwnerId);
                    }
                    System.out.println("???"+String.valueOf(imageId)+"?????????"+"???????????????:" + (endTime - startTime)+"??????");
                    imageId++;
                }
                System.out.println("??????????????????:" + (costTimeSum)+"??????");
                System.out.println("????????????"+imageList.size()+"????????????"+"?????????"+faceListAll.size()+"?????????");

                new FaceCluster(0.75f,2,faceListAll);
                System.out.println("      "+GsonInstance.getInstance().getGson().toJson(FaceCluster.cid));
                for(int k = 0;k<faceListAll.size();k++){
                    faceListAll.get(k).faceClusterType = FaceCluster.cid[k];
                }
                insertImage(imageList);
                insertFace(faceListAll);
            };
        });
    }


    private ImageWithFaceList getNewImageAndFaces(PhotoItem photo){
        Image image = new Image();
        image.path = photo.getPath();
        image.date = photo.getModified();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(photo.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //????????????
        List<Object> predictResult = ImageClassifier.predict(bitmap,224);

        image.classIndex =(int)predictResult.get(0);
        image.imageFeatures = (float[]) predictResult.get(1);
        Log.i("initMydatabase classIndex",String.valueOf(image.classIndex));

        //????????????
        List<Face> faceList = new ArrayList<>();

        float mImgScaleX = (float)photo.getImageWidth() / FaceDetection.mInputWidth;
        float mImgScaleY = (float)photo.getImageHeight() / FaceDetection.mInputHeight;


        ArrayList<Result> detectResult = FaceDetection.detect(bitmap,mImgScaleX,mImgScaleY,1,1,0,0);
        List<Rect> rects = new ArrayList<>();


        for(Result b:detectResult){
            Rect a = b.rect;
            int x = a.left > 0 ? a.left : 0;
            a.left =x;
            int y = a.top > 0 ? a.top : 0;
            a.top =y;
            int width = (a.right > photo.getImageWidth() ? photo.getImageWidth() : a.right) - x;
            a.right = a.left+width;
            int height = (a.bottom > photo.getImageHeight() ? photo.getImageHeight() : a.bottom) - y;
            a.bottom = a.top+height;
            if(width*height>22500&&b.score>=0.31&& (float)(width)/(float)height<1.45&&(float)height/(float)width<1.45){
                rects.add(a);
            }
        }

        image.faceNum = rects.size();

        for(Rect a:rects){
            int x = a.left > 0 ? a.left : 0;
            int y = a.top > 0 ? a.top : 0;
            int width = (a.right > photo.getImageWidth() ? photo.getImageWidth() : a.right) - x;
            int height =  (a.bottom > photo.getImageHeight() ? photo.getImageHeight() : a.bottom) - y;
            Bitmap bitmapFace = Bitmap.createBitmap(bitmap,x,y,width,height);
            float [] faceFeatures = FaceNet.getFeatureVector(bitmapFace);
            Face face = new Face();
            face.faceFeatures = faceFeatures;
            face.rect = a;
            face.imageOwnerId = image.imageId;
            faceList.add(face);
        }
        ImageWithFaceList result = new ImageWithFaceList();
        result.image = image;
        result.faceList = faceList;
        return result;
    }

    public void restartScanPhoto(){
        MyalbumDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                Log.i("restartScanPhoto", "?????????" + threadName);
                long startTime = System.currentTimeMillis();

//                try {
//                    sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                AndroidPhotoScanner.clear();
                AndroidPhotoScanner.startScan();
                List<PhotoItem> photos = AndroidPhotoScanner.mDefaultFolder.getList();
                List<ImageWithFaceList> imageWithFaceLists = null;
                List<Face> faceListAll = new ArrayList<>();
                if(mAllImageWithFaces.getValue() == null){
                    imageWithFaceLists = mImageDao.getImageWithFaceListNow();
                }
                else {
                    imageWithFaceLists = mAllImageWithFaces.getValue();
                }


//                List<Image> images = new ArrayList<>();
//                if(mAllImages.getValue() == null) {
//                    images = mImageDao.getImageListNow();
//                } else{
//                    images = mAllImages.getValue();
//                }

                List<ImageWithFaceList> newImageWithFaceLists = new ArrayList<>();
//                List<Image> newImages = new ArrayList<>();
//                List<Face> newFaces = new ArrayList<>();

                List<ImageWithFaceList> deleteImageWithFaceLists = new ArrayList<>();
                int i = 0;
                int j = 0;

                while(i<photos.size() && j<imageWithFaceLists.size()) {  //?????????????????? ????????????????????????????????????
                    if(photos.get(i).getModified() > imageWithFaceLists.get(j).image.date){    //??????????????????????????????????????????
                        ImageWithFaceList result = getNewImageAndFaces(photos.get(i));

                        newImageWithFaceLists.add(result);
                        faceListAll.addAll(result.faceList);
//                        newImages.add(newImage);
//                        newFaces.addAll(newFace);
                        i++;
                    }
                    else if(photos.get(i).getModified() == imageWithFaceLists.get(j).image.date){
                        faceListAll.addAll(imageWithFaceLists.get(j).faceList);
                        i++;
                        j++;
                    }
                    else{
                        deleteImageWithFaceLists.add(imageWithFaceLists.get(j));
//                        deleteImages.add(imageWithFaceLists.get(j).image);
//                        deleteFaces.addAll(imageWithFaceLists.get(j).faceList);
                        j++;
                    }
                }
                while(i<photos.size()){
                    ImageWithFaceList result = getNewImageAndFaces(photos.get(i));

                    newImageWithFaceLists.add(result);
                    faceListAll.addAll(result.faceList);
                    i++;
                }
                while(j<imageWithFaceLists.size()){
                    deleteImageWithFaceLists.add(imageWithFaceLists.get(j));
                    j++;
                }


                long endTime1 = System.currentTimeMillis();
                System.out.println("?????????????????????:" + (endTime1 - startTime)+"??????");

                new FaceCluster(0.75f,2,faceListAll);
                System.out.println("faceListAllLength:"+faceListAll.size());
                System.out.println("      "+GsonInstance.getInstance().getGson().toJson(FaceCluster.cid));
                for(int k = 0;k<faceListAll.size();k++){
                    faceListAll.get(k).faceClusterType = FaceCluster.cid[k];
                }
                long endTime2 = System.currentTimeMillis();
                System.out.println("faceCluster?????????????????????:" + (endTime2 - endTime1)+"??????");


                insertImageWithFaceList(newImageWithFaceLists);
                deleteImageWithFaceList(deleteImageWithFaceLists);

                System.out.println("newImageWithFaceListScan:"+newImageWithFaceLists.size());
                for(ImageWithFaceList newImageWithFace:newImageWithFaceLists)
                {
                    newImageWithFace.printInfo();
                }

                System.out.println("deleteImageWithFaceListScan:"+deleteImageWithFaceLists.size());
                for(ImageWithFaceList deleteImageWithFace:deleteImageWithFaceLists)
                {
                    deleteImageWithFace.printInfo();
                }
                HomeFragment.scanButtonIsAvailable = true;
            }
        });
    }
}
