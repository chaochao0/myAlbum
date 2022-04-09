package com.example.myalbum.ui.notifications;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myalbum.GlideEngine;
import com.example.myalbum.databinding.FragmentNotificationsBinding;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    static NotificationsViewModel notificationsViewModel = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(notificationsViewModel == null)
            notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initClickListener();

        ImageView imageView = binding.imageView;
        notificationsViewModel.getNewPicture().observe(getViewLifecycleOwner(),imageView::setImageBitmap);
        return root;
    }

    public void initClickListener(){
        binding.imageButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("View button1 id",String.valueOf(v.getId()));
                getResultFromPictureSelector1();
            }
        });
        binding.imageButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("View button2 id",String.valueOf(v.getId()));
                getResultFromPictureSelector2();
            }
        });
    }

    public void getResultFromPictureSelector1(){
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia media : result) {
                            notificationsViewModel.onChoosePicture(media.getRealPath(),0,224);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i("getResultFromPictureSelector", "PictureSelector Cancel");
                    }
                });
    }
    public void getResultFromPictureSelector2(){
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia media : result) {
                            notificationsViewModel.onChoosePicture(media.getRealPath(),1,224);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i("getResultFromPictureSelector", "PictureSelector Cancel");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}