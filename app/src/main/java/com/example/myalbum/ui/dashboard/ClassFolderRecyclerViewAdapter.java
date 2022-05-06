package com.example.myalbum.ui.dashboard;

import android.content.Context;
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
import com.example.myalbum.database.Image;
import com.example.myalbum.model.ImageClassifier;
import com.example.myalbum.ui.home.RecyclerViewAdapter;

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



//    static class ViewHolder extends RecyclerView.ViewHolder{
//        ImageView mImage;
//        TextView mText;
//        public ViewHolder(View view){
//            super(view);
//            mImage = (ImageView) view.findViewById(R.id.image);
//            mText = (TextView) view.findViewById(R.id.date);
//
//
//        }
//    }

    public ClassFolderRecyclerViewAdapter(Context context, List<Image> imageList){
        LinkedHashMap<String,ClassFolder> classToFolder = new LinkedHashMap<>();
        for(Image image:imageList){
            if(!classToFolder.containsKey(ImageClassifier.IMAGE_CLASSES[image.classIndex])) {
                ClassFolder folder = new ClassFolder(image);
                classToFolder.put(ImageClassifier.IMAGE_CLASSES[image.classIndex],folder);
            } else {
                ClassFolder folder = classToFolder.get(ImageClassifier.IMAGE_CLASSES[image.classIndex]);
                folder.add(image);
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
//            GlideEngine.createGlideEngine().loadImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((PhotoHolder) holder).imageView);
            GlideEngine.createGlideEngine().loadGridImage(mContext,((ClassFolder)ClassFolderList.get(position)).firstImagePath,((ClassFolderRecyclerViewAdapter.ClassFolderHolder) holder).imageView);
            ((ClassFolderRecyclerViewAdapter.ClassFolderHolder) holder).classNameTextView.setText(((ClassFolder) ClassFolderList.get(position)).secondClassName);

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

