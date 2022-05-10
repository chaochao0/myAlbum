package com.example.myalbum.ui.face;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalbum.GlideEngine;
import com.example.myalbum.R;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageWithFaceList;
import com.example.myalbum.databinding.FaceFragmentBinding;
import com.example.myalbum.model.Result;
import com.example.myalbum.ui.GallaryActivity;
import com.example.myalbum.ui.dashboard.ClassFolderRecyclerViewAdapter;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;
import java.util.List;

public class FaceFragment extends Fragment {

    private FaceFragmentBinding binding;
    private static FaceViewModel faceViewModel = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(faceViewModel == null)
//            Log.i("faceViewModel","11111111111");
            faceViewModel =
                    new ViewModelProvider(this).get(FaceViewModel.class);

        binding = FaceFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);


        long beginTime = System.currentTimeMillis();
        faceViewModel.getAllImageWithFaces().observe(getViewLifecycleOwner(), new Observer<List<ImageWithFaceList>>() {
            @Override
            public void onChanged(List<ImageWithFaceList> imageWithFaceLists) {
                long currentTime = System.currentTimeMillis();
                System.out.println("getAllImageWithFaces观察到的时间为:" + (currentTime - beginTime)+"毫秒");

                System.out.println("faceViewModelGetAllImageWithFaces"+": imageWithFaceLists length"+imageWithFaceLists.size());
                int faceNum=0;
                for(ImageWithFaceList item: imageWithFaceLists){
                    if(item.faceList != null){
                        faceNum+=item.faceList.size();
                    }
                }
                System.out.println("faceViewModelGetAllImageWithFaces"+": faceNum length"+faceNum);

                RecyclerView recyclerView = binding.rvFace;

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
                recyclerView.setLayoutManager(layoutManager);
                FaceFolderRecyclerViewAdapter adapter = new FaceFolderRecyclerViewAdapter(getContext(),imageWithFaceLists);
                //调用这个函数的时候SpacePhoto并不是空的
                recyclerView.setAdapter(adapter);
                long endTime = System.currentTimeMillis();
                System.out.println("getAllImageWithFaces运行时间为:" + (endTime - currentTime)+"毫秒");
            }
        });

        faceViewModel.getRetrievalResult().observe(getViewLifecycleOwner(), new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> images) {
                if(images == null){
                    return;
                }
                if(images.size()==0){
                    new AlertDialog.Builder(getContext()).setTitle("提示")//设置对话框标题

                            .setMessage("所选照片没有检测到人脸，请更换一张新的照片")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {//添加确定按钮

                                @Override
                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件，点击事件没写，自己添加

                                }
                            }).show();//在按键响应事件中显示此对话框
                }
                else{
                    Intent intent = new Intent(getContext(), GallaryActivity.class);
                    intent.putExtra("imageList", GsonInstance.getInstance().getGson().toJson(images));
                    intent.putExtra("className", "人脸查询结果");

                    getContext().startActivity(intent);
                }
                faceViewModel.clearRetrievalResult();
            }
        });
//        binding.resultView.setVisibility(View.INVISIBLE);
//
//        binding.buttonSelect.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                onButtonSelect();
//            }
//        });
//
//        final ImageView imageViewFace = binding.imageViewFace;
//        faceViewModel.getBitmap().observe(getViewLifecycleOwner(), imageViewFace::setImageBitmap);
//        faceViewModel.getResultList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Result>>() {
//            @Override
//            public void onChanged(ArrayList<Result> results) {
//                binding.buttonSelect.setEnabled(true);
//                binding.resultView.setResults(results);
//                binding.resultView.invalidate();
//                binding.resultView.setVisibility(View.VISIBLE);
//            }
//        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
//        MenuInflater inflater =getActivity().getMenuInflater();
//
//        inflater.inflate(R.menu.title_with_button, menu);
        menu.getItem(0).setTitle("搜人");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart://监听菜单按钮
                System.out.println("搜人点击事件");
                PictureSelector.create(getContext())
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setMaxSelectNum(1)
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                for (LocalMedia media : result) {

                                    faceViewModel.onChoosePicture(media.getRealPath());
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


//    public void onButtonSelect() {
//        binding.buttonSelect.setEnabled(false);
//        binding.resultView.setVisibility(View.INVISIBLE);
//
//        float mImageViewWidth = (float)binding.imageViewFace.getWidth();
//        float mImageViewHeight = (float)binding.imageViewFace.getHeight();
//
//        PictureSelector.create(this)
//                .openGallery(SelectMimeType.ofImage())
//                .setImageEngine(GlideEngine.createGlideEngine())
//                .setMaxSelectNum(0)
//                .forResult(new OnResultCallbackListener<LocalMedia>() {
//                    @Override
//                    public void onResult(ArrayList<LocalMedia> result) {
//                        for (LocalMedia media : result) {
//                            faceViewModel.onChoosePicture(media.getRealPath(),mImageViewWidth,mImageViewHeight);
//                        }
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.i("onChoosePicture", "PictureSelector Cancel");
//                    }
//                });
//    }

    @Override
    public void onDestroyView() {
        Log.i("FaceFragmentOnDestroyVIew","start");
        super.onDestroyView();
        binding = null;
    }

}