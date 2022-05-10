package com.example.myalbum.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.myalbum.R;
import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.ui.home.RecyclerViewAdapter;
import com.example.myalbum.utils.DateUtil;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class GallaryActivity extends AppCompatActivity {
    public static List<Image> imageList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);

        }


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("imageList");
        if(intent.getStringExtra("className")!=null)
            this.setTitle(intent.getStringExtra("className"));
        // Capture the layout's TextView and set the string as its text
        Type listType = new TypeToken<List<Image>>(){}.getType();
        imageList = GsonInstance.getInstance().getGson().fromJson(message,listType);
        LinkedHashMap<String, List<PhotoItem>> mSectionsOfDay = new LinkedHashMap<>();
//                for(Image image:images){
//                    image.printInfo();
//                }
        for(Image image:imageList){
            PhotoItem photo = new PhotoItem(image.path,image.date);

            Date date = new Date(photo.getModified() * 1000);
            String detail = AndroidPhotoScanner.mDataFormatOfDay.format(date);
            String week = DateUtil.getWeek(date);
            String dayKey = detail + week;
            if(!mSectionsOfDay.containsKey(dayKey)) {
                List<PhotoItem> section = new ArrayList<>();
                section.add(photo);
                mSectionsOfDay.put(dayKey, section);
            } else {
                List<PhotoItem> section = mSectionsOfDay.get(dayKey);
                section.add(photo);
            }
        }
//                for(Map.Entry<String, List<PhotoItem>> entry: mSectionsOfDay.entrySet()) {
//                    Log.i("photoWithDay",entry.getKey());
//                    for(PhotoItem item:entry.getValue()){
//                        Log.i("path",item.getPath());
//                    }
//                }

        RecyclerView recyclerView = findViewById(R.id.rv_gallary);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mSectionsOfDay);
        //调用这个函数的时候SpacePhoto并不是空的
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}