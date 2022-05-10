package com.example.myalbum;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.hjq.bar.ITitleBarStyle;
import com.hjq.bar.TitleBar;

public class MyApplication extends Application {

    private static Context mContext;

    public static boolean isFirstOpen;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SharedPreferences base = getSharedPreferences("base",MODE_PRIVATE);
        boolean isFirstStart = base.getBoolean("isFirstStart",true);
        isFirstOpen = isFirstStart;
//        isFirstOpen = true;
        if(isFirstStart) {
            //代表第一次启动App
            SharedPreferences.Editor editor = base.edit();
            editor.putBoolean("isFirstStart",false);
            editor.commit();
            Toast.makeText(this,"第一次",Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(this,"不是第一次",Toast.LENGTH_LONG).show();
        }
    }
}
