package com.example.myalbum.ui.dashboard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myalbum.GlideEngine;
import com.example.myalbum.R;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.database.GsonInstance;
import com.example.myalbum.database.Image;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.ui.GallaryActivity;
import com.example.myalbum.ui.home.RecyclerViewAdapter;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassFolderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected static final int FIRST_CLASS = -1;
    protected static final int CLASS_FOLDER = -2;

    public List<Object> ClassFolderList = new ArrayList<>(); //包含 大类名1 小类1(ClassFolder) 小类2 小类3  大类2 小类   并且是按照大类名的顺序排列
    private Context mContext;
//    LinkedHashMap<String, List<PhotoItem>> mPhotoWithDay;

    class HeadingHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public HeadingHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            textView = itemView.findViewById(R.id.date);
        }
    }

    class ClassFolderHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView classNameTextView;
        public ClassFolderHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            imageView = itemView.findViewById(R.id.class_folder_imageview);
            classNameTextView = itemView.findViewById(R.id.class_folder_text);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION)
                    {
                        ClassFolder folder = (ClassFolder) ClassFolderList.get(position);
                        List<Image> imageList = folder.mImagelist;
                        Intent intent = new Intent(v.getContext(), GallaryActivity.class);
                        intent.putExtra("imageList", GsonInstance.getInstance().getGson().toJson(imageList));
                        intent.putExtra("className", folder.secondClassName);

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


    public ClassFolderRecyclerViewAdapter(Context context, List<Image> imageList){
        LinkedHashMap<String,ClassFolder> classToFolder = new LinkedHashMap<>();
        for(Image image:imageList){
            if(image.classIndex != -1)
            {
                if(!classToFolder.containsKey(ImageClassifier.IMAGE_CLASSES[image.classIndex])) {
                    ClassFolder folder = new ClassFolder(image);
                    classToFolder.put(ImageClassifier.IMAGE_CLASSES[image.classIndex],folder);
                } else {
                    ClassFolder folder = classToFolder.get(ImageClassifier.IMAGE_CLASSES[image.classIndex]);
                    folder.add(image);
                }
            }

        }
        for(String firstClass: ImageClassifier.FIRST_CLASSES){
            ClassFolderList.add(firstClass);
            boolean existClass = false;
            for(Map.Entry<String, ClassFolder> entry: classToFolder.entrySet()) {
                if(firstClass.equals(entry.getKey().split("/")[0])){
                    ClassFolderList.add(entry.getValue());
                    existClass = true;
                }
            }
            if(!existClass){
                ClassFolderList.remove(ClassFolderList.size() - 1);
            }


        }
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        if(viewType == FIRST_CLASS){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_date,parent,false);  //展示 风景 地点 等大类名
            return new ClassFolderRecyclerViewAdapter.HeadingHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_image_class,parent,false);
            return new ClassFolderRecyclerViewAdapter.ClassFolderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof ClassFolderRecyclerViewAdapter.HeadingHolder){
            ((ClassFolderRecyclerViewAdapter.HeadingHolder) holder).textView.setText((String)ClassFolderList.get(position));
        }
        else if(holder instanceof ClassFolderRecyclerViewAdapter.ClassFolderHolder){
            long startTime = System.currentTimeMillis();
//            GlideEngine.createGlideEngine().loadImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((PhotoHolder) holder).imageView);
            GlideEngine.createGlideEngine().loadGridImage(mContext,((ClassFolder)ClassFolderList.get(position)).firstImagePath,((ClassFolderRecyclerViewAdapter.ClassFolderHolder) holder).imageView);
            ((ClassFolderRecyclerViewAdapter.ClassFolderHolder) holder).classNameTextView.setText(((ClassFolder) ClassFolderList.get(position)).secondClassName);
            long endTime = System.currentTimeMillis();
            System.out.println("classonBindViewHolder一次运行时间"+(endTime-startTime));
            //            ((PhotoHolder) holder).imageView.setImageResource((int)photoWithDayList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position){
        //return 的是一个标识，由自己定义一个不重复的数字即可
        if (ClassFolderList.get(position) instanceof String) {
            return FIRST_CLASS;
        } else {
            return CLASS_FOLDER;
        }
    }

    @Override
    public int getItemCount(){
        return ClassFolderList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == FIRST_CLASS
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}

