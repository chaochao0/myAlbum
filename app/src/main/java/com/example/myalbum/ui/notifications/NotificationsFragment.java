package com.example.myalbum.ui.notifications;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myalbum.GlideEngine;
import com.example.myalbum.MyApplication;
import com.example.myalbum.R;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageRepository;
import com.example.myalbum.databinding.FragmentNotificationsBinding;
import com.example.myalbum.model.ImageTransfer;
import com.luck.lib.camerax.utils.DateUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.yalantis.ucrop.UCrop;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AlphaPageTransformer;
import com.youth.banner.transformer.DepthPageTransformer;
import com.youth.banner.transformer.RotateDownPageTransformer;
import com.youth.banner.transformer.RotateUpPageTransformer;
import com.youth.banner.transformer.RotateYTransformer;
import com.youth.banner.transformer.ScaleInTransformer;
import com.youth.banner.transformer.ZoomOutPageTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

        setHasOptionsMenu(true);

//        notificationsViewModel.getDrawableImageId().observe(getViewLifecycleOwner(), new Observer<int[]>() {
//            @Override
//            public void onChanged(int[] ints) {
//                System.out.println("notificationsViewModelgetImagePathListChanged");
//                List<Integer> drawImageId = Arrays.stream(ints).boxed().collect(Collectors.toList());
//                binding.banner.setAdapter(new BannerImageAdapter<Integer>(drawImageId) {
//                    @Override
//                    public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
//                        Glide.with(holder.itemView)
//                                .load(data)
//                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
//                                .into(holder.imageView);
//                    }
//                }).isAutoLoop(false)
//                .setUserInputEnabled(true)
////                        .setPageTransformer(new AlphaPageTransformer())
////                        .setPageTransformer(new DepthPageTransformer())
////                        .setPageTransformer(new RotateDownPageTransformer())
////                        .setPageTransformer(new RotateUpPageTransformer())
////                        .setPageTransformer(new RotateYTransformer())
////                        .setPageTransformer(new ScaleInTransformer())
//                        .setPageTransformer(new ZoomOutPageTransformer())
////                .setBannerGalleryMZ(90,1.f)
//                .setBannerGalleryEffect(30,10)
//                        .addBannerLifecycleObserver(getViewLifecycleOwner())//添加生命周期观察者
//                        .setIndicator(new CircleIndicator(getContext()));
//            }
//        });
        notificationsViewModel.getDrawableImageId().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                List<Integer> drawImageId = Arrays.stream(ints).boxed().collect(Collectors.toList());
                binding.banner.addBannerLifecycleObserver(getViewLifecycleOwner())//添加生命周期观察者
                .setAdapter(new ImageAdapter(getContext(),drawImageId))
                .setIndicator(new CircleIndicator(getContext()))
                .isAutoLoop(false)
                        .setUserInputEnabled(true)
                        .setPageTransformer(new ZoomOutPageTransformer())
                        .setBannerGalleryEffect(30,10)
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        System.out.println("OnBannerClick"+ImageTransfer.ID_TO_TRANSFER_CLASSES[position]);
                        PictureSelector.create(getContext())
                                .openGallery(SelectMimeType.ofImage())
                                .setImageEngine(GlideEngine.createGlideEngine())
                                .setMaxSelectNum(1)
                                .setEditMediaInterceptListener(new OnMediaEditInterceptListener() {
                                    @Override
                                    public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
                                        // 注意* 如果你实现自己的编辑库，需要在Activity的.setResult(); Intent中需要给MediaStore.EXTRA_OUTPUT保存编辑后的路径；
                                        // 如果有额外数据也可以通过CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA字段存入；

                                        // 1、构造编辑的数据源
                                        String currentEditPath = currentLocalMedia.getAvailablePath();
                                        Uri inputUri = PictureMimeType.isContent(currentEditPath)
                                                ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
                                        Uri destinationUri = Uri.fromFile(
                                                new File(currentLocalMedia.getSandboxPath(), DateUtils.getCreateFileName("CROP_") + ".jpeg"));
                                        UCrop uCrop = UCrop.of(inputUri, destinationUri);
                                        UCrop.Options options = new UCrop.Options();
                                        options.setHideBottomControls(false);
                                        uCrop.withOptions(options);
                                        uCrop.startEdit(fragment.getActivity(), fragment, requestCode);
                                    }
                                })
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(ArrayList<LocalMedia> result) {
                                        for (LocalMedia media : result) {
                                            int size = media.getWidth()>media.getHeight()?media.getHeight():media.getWidth();
                                            notificationsViewModel.onChoosePicture(media.getRealPath(),position,224);
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        Log.i("getResultFromPictureSelector", "PictureSelector Cancel");
                                    }
                                });
                    }
                });


                System.out.println("notificationsViewModelgetImagePathListChanged");
            }
        });
        notificationsViewModel.getNewPicture().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                  if(bitmap ==null){
                      return;
                  }
                  saveImage(bitmap,"temp.jpg");
                  notificationsViewModel.clearNewPicture();

//                PhotoItem photo = (PhotoItem) photoWithDayList.get(position1);
//                List<LocalMedia> list=new ArrayList<>();
//
//                LocalMedia media=new LocalMedia();
//                String url= photo.getPath();
//                media.setPath(url);
//                list.add(media);
//
//                PictureSelector.create(itemView.getContext())
//                        .openPreview()
//                        .setImageEngine(GlideEngine.createGlideEngine())
//                        .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
//                            @Override
//                            public void onPreviewDelete(int position) {
//
//                            }
//
//                            @Override
//                            public boolean onLongPressDownload(LocalMedia media) {
//                                return false;
//                            }
//                        }).startActivityPreview(0, true, (ArrayList<LocalMedia>)list);
            }
        });

//        initClickListener();

//        ImageView imageView = binding.imageViewTransfer;
//        notificationsViewModel.getNewPicture().observe(getViewLifecycleOwner(),imageView::setImageBitmap);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
//        MenuInflater inflater =getActivity().getMenuInflater();
//
//        inflater.inflate(R.menu.title_with_button, menu);
        menu.getItem(0).setTitle("搜图");
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart://监听菜单按钮
                System.out.println("查相似图点击事件");

                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void initClickListener(){
//        binding.imageButton1.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Log.i("View button1 id",String.valueOf(v.getId()));
//                getResultFromPictureSelector1();
//            }
//        });
//        binding.imageButton2.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Log.i("View button2 id",String.valueOf(v.getId()));
//                getResultFromPictureSelector2();
//            }
//        });
//    }

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
        Log.i("NotificationsFragmentOnDestroyVIew","start");

        super.onDestroyView();
        binding = null;
    }

    private Uri insertNewPicture(String newImageName){
        // Add a specific media item.
        ContentResolver resolver = MyApplication.getContext()
                .getContentResolver();

// Find all audio files on the primary external storage device.
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
// Publish a new song.
        ContentValues newImageDetails = new ContentValues();
//        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME,
//                "temp.jpg");
        newImageDetails.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, new Date(System.currentTimeMillis()).toString()+".png");
// Keeps a handle to the new song's URI in case we need to modify it
// later.

        Uri myFavoriteSongUri = resolver
                .insert(imageCollection, newImageDetails);

        return myFavoriteSongUri;
    }
    private void saveImage(Bitmap toBitmap,String imageName) {
        //开始一个新的进程执行保存图片的操作
//        Uri insertUri =MyApplication.getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        Uri insertUri = insertNewPicture(imageName);
        //使用use可以自动关闭流
        try {
            OutputStream outputStream = getActivity().getApplicationContext()
                    .getContentResolver().openOutputStream(insertUri, "rw");
            if (toBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                Log.e("保存成功", "success");
            } else {
                Log.e("保存失败", "fail");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //将新的image插入本地数据库
        Image image = new Image();
        image.classIndex = -1;
        image.path = "content://media"+insertUri.getPath();

        image.date = System.currentTimeMillis()/1000;
        ImageRepository.getImageRepositoryInstance().insertImage(image);


        //预览图片
        List<LocalMedia> list=new ArrayList<>();

        LocalMedia media=new LocalMedia();

        String url= image.path;
        media.setPath(url);
        list.add(media);


        PictureSelector.create(getContext())
                .openPreview()
                .setImageEngine(GlideEngine.createGlideEngine())
                .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
                    @Override
                    public void onPreviewDelete(int position) {

                    }

                    @Override
                    public boolean onLongPressDownload(LocalMedia media) {
                        return false;
                    }
                })
                .startActivityPreview(0, false, (ArrayList<LocalMedia>)list);
    }
}