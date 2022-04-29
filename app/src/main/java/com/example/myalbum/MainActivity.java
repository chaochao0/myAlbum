package com.example.myalbum;

import static java.lang.Thread.sleep;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import com.example.myalbum.database.Face;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.database.MyalbumDatabase;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.ImageTransfer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.myalbum.databinding.ActivityMainBinding;
import com.example.myalbum.model.Result;
import com.example.myalbum.retrieval.FaceCluster;

//import org.pytorch.LiteModuleLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    ImageClassifier imageClassifer;

    ImageTransfer imageTransfer;

    FaceDetection faceDetection;

    FaceNet faceNet;

    public static ImageRepository repo;


    public static ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.i("onCreate","MainActivity");
        repo= new ImageRepository(getApplication());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                Log.e("TAG", "线程："+threadName);
                initModel();
//                initDatabase();

            }
        });
        repo.getAllImages().observe(this, new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                Log.i("initDatabase images length 1", String.valueOf(images.size()));
                for(Image i:images){
                    System.out.println("imagepath"+String.valueOf(i.path));
                }

            }
        });
        repo.getAllFaces().observe(this, new Observer<List<Face>>() {
            @Override
            public void onChanged(List<Face> faces) {
                Log.i("initDatabase faceslength", String.valueOf(faces.size()));

                new FaceCluster(0.75f,1);
                System.out.println("      "+GsonInstance.getInstance().getGson().toJson(FaceCluster.cid));
                System.out.println("right "+"[1,2,3,2,1,3,2,1,3]");

            }
        });

        Log.i("onCreate","222");


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_face, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void initModel(){
        ArrayList<String> transferList= new ArrayList<String>();
//        String[] modelPath = {"1_transformer.pt", "2_transformer.pt", "3_transformer.pt"};
        String[] modelPath = {"models/1_transformer.pt", "models/2_transformer.pt"};
        String classiferModel="";
        String faceDetectionModel = "";
        String faceNetModel = "";
        try{
            classiferModel=assetFilePath(this, "models/my_mobilenetv3_161_66acc.pt");
            faceDetectionModel=assetFilePath(this, "models/yolov5FaceDetection.pt");
            faceNetModel = assetFilePath(this, "models/facenet_160_512_noOpt_noLite.pt");
            for(String i:modelPath){
                transferList.add(assetFilePath(this,i));
//                transferList.add(i);
            }
        }catch(IOException e) {
            Log.e("initModel", "Error reading assets", e);
        }
        imageTransfer = new ImageTransfer(transferList);
        imageClassifer = new ImageClassifier(classiferModel);
        faceDetection = new FaceDetection(faceDetectionModel);
        faceNet = new FaceNet(faceNetModel);

    }

    public void initDatabase(){
//        repo= new ImageRepository(getApplicationContext());


//        db = Room.databaseBuilder(getApplicationContext(),
//                MyalbumDatabase.class, "myAlbumDatabase.db").build();

        //测试数据库
        //insert
        Image image = new Image();

        String picturePath = "";
        try{
            picturePath = assetFilePath(this,"人脸图片1.jpg");
            image.path = picturePath;
        }catch(IOException e) {
            Log.e("initDatabase", "Error reading assets", e);
        }
        Bitmap bitmap = null;
        try {
            bitmap = (BitmapFactory.decodeStream(new FileInputStream(picturePath)));
        } catch (FileNotFoundException e) {
            Log.e("initDatabase", "Error reading file", e);
        }
        List<Object> predictResult = ImageClassifier.predict(bitmap,224);
        image.classIndex = (int)predictResult.get(0);
        float[] features = (float[]) predictResult.get(1);
        Log.i("image features length",String.valueOf(features.length));
//        List<Float> imageFeatures = new ArrayList<Float>(features.length);
//        Collections.addAll(imageFeatures, features);
        Log.i("initDatabase image type",ImageClassifier.IMAGE_CLASSES[image.classIndex]);
        image.imageFeatures = features;
        Log.i("initDatabase features", GsonInstance.getInstance().getGson().toJson(image.imageFeatures));

        float mImgScaleX = (float)bitmap.getWidth() / FaceDetection.mInputWidth;
        float mImgScaleY = (float)bitmap.getHeight() / FaceDetection.mInputHeight;

        ArrayList<Result> detectResult = FaceDetection.detect(bitmap,mImgScaleX,mImgScaleY,1,1,0,0);
        List<Rect> rects = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        for(Result a:detectResult){
            rects.add(a.rect);
        }
        image.faceNum = rects.size();

        Log.i("initDatabase imageId", GsonInstance.getInstance().getGson().toJson(image.imageId));


        Log.i("initDatabase rects length",String.valueOf(rects.size()));

        float[][] faceFeature = new float[rects.size()][512];;
        int i =0;
        for(Rect a:rects){
            int x = a.left > 0 ? a.left : 0;
            int y = a.top > 0 ? a.top : 0;
            int width = (a.right > bitmap.getWidth()?bitmap.getWidth():a.right) - x;
            int height =  (a.bottom > bitmap.getHeight()?bitmap.getHeight():a.bottom) - y;
            Bitmap bitmapFace = Bitmap.createBitmap(bitmap,x,y,width,height);
            float [] faceFeatures = FaceNet.getFeatureVector(bitmapFace);
            faceFeature[i]=faceFeatures;
            i++;
            Face face = new Face();
            face.faceFeatures = faceFeatures;
            face.rect = a;
            face.imageOwnerId = image.imageId;
            faces.add(face);
        }

        Log.i("initDatabase facefeature length",String.valueOf(faceFeature.length));
//
        repo.insertImageWithFaceList(image,faces);
//        repo.insert(faces);
//        Image image2 = repo.getImage(picturePath);
//        Log.i("initDatabase imageId", GsonInstance.getInstance().getGson().toJson(image2.imageId));
//
//        Log.i("features from database length",String.valueOf(image2.imageFeatures.length));
//        Log.i("features from database",GsonInstance.getInstance().getGson().toJson(image2.imageFeatures));
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Image> images1 = ImageRepository.mAllImages.getValue();
        if(images1 != null)
            Log.i("initDatabase images length 2", String.valueOf(images1.size()));

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Image> images2 = ImageRepository.mAllImages.getValue();
        if(images2 != null)
            Log.i("initDatabase images length 3", String.valueOf(images2.size()));

    }


    public static String assetFilePath(Context context, String assetName) throws IOException {
        System.out.println("picture file dir:" + context.getFilesDir());
        File file = new File(context.getFilesDir(), assetName.split("/")[assetName.split("/").length-1]);
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

}