package com.example.myalbum.ui.dashboard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.example.myalbum.GlideEngine;
import com.example.myalbum.R;
import com.example.myalbum.data.AndroidPhotoScanner;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.databinding.FragmentDashboardBinding;
import com.example.myalbum.databinding.FragmentHomeBinding;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.ui.GallaryActivity;
import com.example.myalbum.ui.home.HomeFragment;
import com.example.myalbum.ui.home.RecyclerViewAdapter;
import com.example.myalbum.utils.DateUtil;
import com.luck.lib.camerax.utils.DateUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.Inflater;

//??????????????????
public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    static DashboardViewModel dashboardViewModel = null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if(dashboardViewModel == null)
//            Log.i("dashboardViewModel","111111111");
            dashboardViewModel =
                    new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        setHasOptionsMenu(true);


        dashboardViewModel.getImageList().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                System.out.println("dashboardViewModelGetImageListOnChanged"+": images length"+images.size());

                RecyclerView recyclerView = binding.rvClassify;

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
                recyclerView.setLayoutManager(layoutManager);
                ClassFolderRecyclerViewAdapter adapter = new ClassFolderRecyclerViewAdapter(getContext(),images);
                //???????????????????????????SpacePhoto???????????????
                recyclerView.setAdapter(adapter);
            }
        });
        dashboardViewModel.getRetrievalResult().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                if(images == null){
                    return;
                }

                Intent intent = new Intent(getContext(), GallaryActivity.class);
                intent.putExtra("imageList", GsonInstance.getInstance().getGson().toJson(images));
                intent.putExtra("className", "??????????????????");

                getContext().startActivity(intent);
                dashboardViewModel.clearRetrievalResult();
            }
        });
//        TextView textView = binding.textClass;
//        ImageView imageView = binding.image;
//        binding.button.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                onChoosePicture();
//            }});
//
//        dashboardViewModel.getClassName().observe(getViewLifecycleOwner(), textView::setText);
//        dashboardViewModel.getPicture().observe(getViewLifecycleOwner(),imageView::setImageBitmap);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
//        MenuInflater inflater =getActivity().getMenuInflater();
//
//        inflater.inflate(R.menu.title_with_button, menu);
        menu.getItem(0).setTitle("??????");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart://??????????????????
                System.out.println("????????????????????????");

                PictureSelector.create(getContext())
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setMaxSelectNum(1)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                for (LocalMedia media : result) {

                                    dashboardViewModel.onChoosePicture(media.getRealPath());
                                }
                            }

                            @Override
                            public void onCancel() {
                                Log.i("getResultFromPictureSelector", "PictureSelector Cancel");
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onChoosePicture() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia media : result) {
                            Log.i("onChoosePicture", "????????????:" + media.isCompressed());
                            Log.i("onChoosePicture", "??????:" + media.getCompressPath());
                            Log.i("onChoosePicture", "??????:" + media.getPath());
                            Log.i("onChoosePicture", "??????????????????:" + media.getRealPath());
                            Log.i("onChoosePicture", "????????????:" + media.isCut());
                            Log.i("onChoosePicture", "??????:" + media.getCutPath());
                            Log.i("onChoosePicture", "??????????????????:" + media.isOriginal());
                            Log.i("onChoosePicture", "????????????:" + media.getOriginalPath());
//                            Log.i("TAG", "Android Q ??????Path:" + media.getAndroidQToPath());
//                            BitmapFactory.decodeStream(FileInputStream(media.getPath()))
//                            FileInputStream stream = new FileInputStream(fileName);
//                            binding.image.setImageBitmap();

                            dashboardViewModel.onChoosePicture(media.getRealPath());
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i("onChoosePicture", "PictureSelector Cancel");
                    }
                });
    }

}