package com.example.myalbum.ui.face;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import com.example.myalbum.GlideEngine;
import com.example.myalbum.R;
import com.example.myalbum.database.Face;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.database.ImageWithFaceList;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.ui.GallaryActivity;
import com.example.myalbum.ui.dashboard.ClassFolder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FaceFolderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected static final int FIRST_CLASS = -1;
    protected static final int CLASS_FOLDER = -2;

    public List<FaceFolder> ClassFolderList = new ArrayList<>(); //FaceFolder
    private Context mContext;
//    LinkedHashMap<String, List<PhotoItem>> mPhotoWithDay;

    class FaceFolderHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView faceNumTextView;
        public FaceFolderHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            imageView = itemView.findViewById(R.id.circle_face_image);
            faceNumTextView = itemView.findViewById(R.id.face_num_textview);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        FaceFolder folder = (FaceFolder) ClassFolderList.get(position);
                        List<Image> imageList = folder.mImagelist;

                        Intent intent = new Intent(v.getContext(), GallaryActivity.class);
                        intent.putExtra("imageList", GsonInstance.getInstance().getGson().toJson(imageList));
                        intent.putExtra("className", "人物图片集");

                        v.getContext().startActivity(intent);

//                        List<String> images = new ArrayList<>();
//                        for(Image i:imageList){
//                            images.add(i.path);
//                        }
//                        new StfalconImageViewer.Builder<String>(v.getContext(), images, new ImageLoader<String>() {
//                            @Override
//                            public void loadImage(ImageView imageView, String image) {
//                                GlideEngine.createGlideEngine().loadImage(v.getContext(), image,imageView);
//                            }
//                        }).show();
//                        StfalconImageViewer.Builder<Image>(itemView.getContext(), images, ( ImageView view, Image image -> {
//                        return GlideEngine.createGlideEngine().loadImage(itemView.getContext(), image.path,view); })).show()



//                        PhotoItem photo = (PhotoItem) photoWithDayList.get(position);
//                        String url = spacePhoto.getUrl();
//                        Intent intent = new Intent(mContext, SpacePhotoActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("url",url);
//                        intent.putExtras(bundle);
//                        mContext.startActivity(intent);
                    }

                }
            });
        }
    }


    public FaceFolderRecyclerViewAdapter(Context context, List<ImageWithFaceList> imageWithFaceList){
        LinkedHashMap<Integer,FaceFolder> classToFaceFolder = new LinkedHashMap<>();

        for(ImageWithFaceList item:imageWithFaceList){
            if(item.faceList != null && item.faceList.size()>0){
                Image image = item.image;
                for(Face face: item.faceList){
                    if(!classToFaceFolder.containsKey(face.faceClusterType)) {
                        FaceFolder folder = new FaceFolder(image,face);
                        classToFaceFolder.put(face.faceClusterType,folder);
                    } else {
                        FaceFolder folder = classToFaceFolder.get(face.faceClusterType);
                        folder.add(image);
                    }
                }
            }
        }

        List<Integer> key = new ArrayList<>();  //按照face count从大到小的顺序 记录其cluster id
        for(int i = 0; i<classToFaceFolder.size();i++){
            int maxSize = -1;
            int maxSizeCluster = -2;
            for(Map.Entry<Integer, FaceFolder> entry: classToFaceFolder.entrySet()) {
                if(entry.getValue().count > maxSize && !key.contains(entry.getKey())){
                    maxSize = entry.getValue().count;
                    maxSizeCluster = entry.getKey();
                }
            }
            key.add(maxSizeCluster);

        }
        for(Integer i:key){
            ClassFolderList.add(classToFaceFolder.get(i));  //按从大到小的顺序存放
            System.out.println("faceCluserID:"+i.toString()+" 图片数量："+classToFaceFolder.get(i).count);
        }

        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_circle_image,parent,false);
        return new FaceFolderRecyclerViewAdapter.FaceFolderHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){

        FaceFolder folder = ((FaceFolder)ClassFolderList.get(position));
        int x = folder.rect.left;
        int y = folder.rect.top;
        int width = folder.rect.right - x;
        int height =  folder.rect.bottom - y;
        Bitmap bitmap = null;
        long startTime = System.currentTimeMillis();

        if(Util.isOnMainThread()) //判断是否在主线程处理
            System.out.println("faceonBindViewHolder start");
        Glide.with(mContext).asBitmap().load(folder.firstImagePath).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if(resource!=null){
                    Bitmap bitmapFace = Bitmap.createBitmap(resource,x,y,width,height);; //调用裁剪图片工具类进行裁剪
                    if(bitmapFace!=null)
                        ((FaceFolderHolder) holder).imageView.setImageBitmap(bitmapFace); //设置Bitmap到图片上
                }
            }
        });

        long endTime = System.currentTimeMillis();
        System.out.println("faceonBindViewHolder运行一次时间："+(endTime-startTime));

//        Bitmap bitmapFace = Bitmap.createBitmap(bitmap,x,y,width,height);
//        ((FaceFolderHolder) holder).imageView.setImageBitmap(bitmapFace);
        ((FaceFolderHolder) holder).faceNumTextView.setText(String.valueOf(folder.count)+"张人脸");

    }

    @Override
    public int getItemCount(){
        return ClassFolderList.size();
    }
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if(manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return getItemViewType(position) == FIRST_CLASS
//                            ? gridManager.getSpanCount() : 1;
//                }
//            });
//        }
//    }
}

