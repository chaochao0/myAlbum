package com.example.myalbum;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.myalbum.ui.dashboard.ImageClassifier;
import com.example.myalbum.ui.notifications.ImageTransfer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myalbum.databinding.ActivityMainBinding;

//import org.pytorch.LiteModuleLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    ImageClassifier imageClassifer;

    ImageTransfer imageTransfer;

    public static ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.i("onCreate","111");

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                Log.e("TAG", "线程："+threadName);
                initModel();
            }
        });
        Log.i("onCreate","222");

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void initModel(){
        ArrayList<String> transferList= new ArrayList<String>();
//        String[] modelPath = {"1_transformer.pt", "2_transformer.pt", "3_transformer.pt"};
        String[] modelPath = {"1_transformer.pt","2_transformer.pt"};
        String classiferModel="";
        try{
            classiferModel=assetFilePath(this,"mobilenetv3_large_161_66acc.pt");
            for(String i:modelPath){
                Log.i("nmsl","hhaha");
                transferList.add(assetFilePath(this,i));
//                transferList.add(i);
            }
        }catch(IOException e) {
            Log.e("PytorchHelloWorld", "Error reading assets", e);
        }
        imageTransfer = new ImageTransfer(transferList);
        imageClassifer = new ImageClassifier(classiferModel);
    }
    public static String assetFilePath(Context context, String assetName) throws IOException {
        System.out.println("picture file dir:" + context.getFilesDir());
        File file = new File(context.getFilesDir(), assetName);
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