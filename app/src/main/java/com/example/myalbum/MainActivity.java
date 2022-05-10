package com.example.myalbum;

import static com.example.myalbum.data.AndroidPhotoScanner.mDefaultFolder;
import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.Face;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.database.MyalbumDatabase;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.model.FaceDetection;
import com.example.myalbum.model.FaceNet;
import com.example.myalbum.model.ImageTransfer;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private ActivityMainBinding binding;

    ImageClassifier imageClassifer;

    ImageTransfer imageTransfer;

    FaceDetection faceDetection;

    FaceNet faceNet;

    public static ImageRepository repo;

    public static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.i("onCreate","MainActivity");

        verifyStoragePermissions(this);//动态申请存储读写权限
//        isFirstOpen();

        context = getApplicationContext();

        repo= ImageRepository.getImageRepositoryInstance();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                Log.e("initModel", "线程："+threadName);
                initModel();
            }
        });
//        repo.getAllImages().observe(this, new Observer<List<Image>>() {
//            @Override
//            public void onChanged(List<Image> images) {
//                Log.i("initDatabase images length 1", String.valueOf(images.size()));
//                for(Image i:images){
//                    System.out.println(String.valueOf(i.date));
//                }
//
//            }
//        });
        repo.getAllFaces().observe(this, new Observer<List<Face>>() {
            @Override
            public void onChanged(List<Face> faces) {
                Log.i("facesLength", String.valueOf(faces.size()));
                for(Face face:faces){
                    face.printFaceInfo();
                }
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,R.id.navigation_dashboard, R.id.navigation_face,R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void initModel(){
        ArrayList<String> transferList= new ArrayList<String>();
//        String[] modelPath = {"1_transformer.pt", "2_transformer.pt", "3_transformer.pt"};
        String[] modelPath = ImageTransfer.assetsModelPath;
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

    public static String assetFilePath(Context context, String assetName) throws IOException {
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

    public static Context getContextObject(){
        return context;
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                Log.i("verifyStoragePermissions","no");
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
            else{
                Log.i("verifyStoragePermissions","yes");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.title_with_button, menu);
        return true;
    }
}