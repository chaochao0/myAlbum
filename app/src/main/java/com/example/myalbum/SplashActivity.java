package com.example.myalbum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null){  // 去掉标题栏
            getSupportActionBar().hide();
        }
        Log.i("SplashActivity","onCreated");
//        boolean hasPer =  verifyStoragePermissions(SplashActivity.this);
//        if(hasPer){
//            Log.i("SplashActivity","hasPermissions");
//            startActivity(new Intent(this,MainActivity.class));
//            finish();
//        }
//        else{
//
//        }
        if(MyApplication.isFirstOpen){
            Log.i("SplashActivity","isFirstOpen");

            verifyStoragePermissions(SplashActivity.this);

//            SharedPreferences base = getSharedPreferences("base",MODE_PRIVATE);
//            SharedPreferences.Editor editor = base.edit();
//            editor.putBoolean("isFirstStart",false);
//            editor.commit();
        }
        else{
            Log.i("SplashActivity","isNotFirstOpen");
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }

    public static boolean verifyStoragePermissions(Activity activity) {
        try {
            System.out.println("verifyStoragePermissions1");
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                System.out.println("verifyStoragePermissions2");
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("onRequestPermissionsResult1");
        if(requestCode == REQUEST_EXTERNAL_STORAGE && grantResults.length > 0){
            // 判断 是否获得 权限
            for(int i=0;i < grantResults.length;i++){
                // 未得到 授权 的权限
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){
                        Log.i("WY",permissions[i]+" 未授权且不再询问");
                        new AlertDialog.Builder(this).setTitle("警告")//设置对话框标题
                                .setMessage("应用必须要读写权限才能正常使用！！")
                                .setPositiveButton("退出", new DialogInterface.OnClickListener() {//添加确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                        finish();
                                    }
                                }).show();//在按键响应事件中显示此对话框

                    }else{
                        Log.i("WY",permissions[i]+" 未授权");
                        new AlertDialog.Builder(this).setTitle("警告")//设置对话框标题
                                .setMessage("应用必须要读写权限才能使用！！")
                                .setPositiveButton("退出", new DialogInterface.OnClickListener() {//添加确定按钮
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加
                                        finish();
                                    }
                                }).show();//在按键响应事件中显示此对话框
                    }
                }else{
                    Log.i("WY",permissions[i]+" 已授权");
                }
            }
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

}