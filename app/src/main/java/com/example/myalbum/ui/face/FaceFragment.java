package com.example.myalbum.ui.face;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myalbum.GlideEngine;
import com.example.myalbum.databinding.FaceFragmentBinding;
import com.example.myalbum.model.Result;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;

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

        binding.resultView.setVisibility(View.INVISIBLE);

        binding.buttonSelect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onButtonSelect();
            }
        });

        final ImageView imageViewFace = binding.imageViewFace;
        faceViewModel.getBitmap().observe(getViewLifecycleOwner(), imageViewFace::setImageBitmap);
        faceViewModel.getResultList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Result>>() {
            @Override
            public void onChanged(ArrayList<Result> results) {
                binding.buttonSelect.setEnabled(true);
                binding.resultView.setResults(results);
                binding.resultView.invalidate();
                binding.resultView.setVisibility(View.VISIBLE);
            }
        });
        return root;


//        return inflater.inflate(R.layout.face_fragment, container, false);
    }

    public void onButtonSelect() {
        binding.buttonSelect.setEnabled(false);
        binding.resultView.setVisibility(View.INVISIBLE);

        float mImageViewWidth = (float)binding.imageViewFace.getWidth();
        float mImageViewHeight = (float)binding.imageViewFace.getHeight();

        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia media : result) {
                            faceViewModel.onChoosePicture(media.getRealPath(),mImageViewWidth,mImageViewHeight);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.i("onChoosePicture", "PictureSelector Cancel");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        Log.i("FaceFragmentOnDestroyVIew","start");
        super.onDestroyView();
        binding = null;
    }

}