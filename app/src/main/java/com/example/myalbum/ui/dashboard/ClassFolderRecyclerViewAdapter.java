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
import com.example.myalbum.ui.home.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassifyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected static final int DATE = -1;
    protected static final int PHOTO = -2;

    public List<Object> photoWithClassList = new ArrayList<>();
    private Context mContext;
//    LinkedHashMap<String, List<PhotoItem>> mPhotoWithDay;

    class HeadingHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public HeadingHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            textView = itemView.findViewById(R.id.class_folder_text);
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

    public RecyclerViewAdapter(Context context, LinkedHashMap<String, List<PhotoItem>> photoWithDay){
        for(Map.Entry<String, List<PhotoItem>> entry: photoWithDay.entrySet()) {
            photoWithDayList.add(entry.getKey());
            for(PhotoItem item:entry.getValue()){
                photoWithDayList.add(item);
            }
        }
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        if(viewType == DATE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_date,parent,false);
            return new RecyclerViewAdapter.DateHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_image,parent,false);
            return new RecyclerViewAdapter.PhotoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof RecyclerViewAdapter.DateHolder){
            ((RecyclerViewAdapter.DateHolder) holder).textView.setText((String)photoWithDayList.get(position));
        }
        else if(holder instanceof RecyclerViewAdapter.PhotoHolder){
//            GlideEngine.createGlideEngine().loadImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((PhotoHolder) holder).imageView);
            GlideEngine.createGlideEngine().loadGridImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((RecyclerViewAdapter.PhotoHolder) holder).imageView);
//            ((PhotoHolder) holder).imageView.setImageResource((int)photoWithDayList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position){
        //return 的是一个标识，由自己定义一个不重复的数字即可
        if (photoWithDayList.get(position) instanceof String) {
            return DATE;
        } else {
            return PHOTO;
        }
    }

    @Override
    public int getItemCount(){
        return photoWithDayList.size();
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
                    return getItemViewType(position) == DATE
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}

