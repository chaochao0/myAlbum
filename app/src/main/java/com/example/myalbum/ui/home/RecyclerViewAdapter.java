package com.example.myalbum.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
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
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected static final int DATE = -1;
    protected static final int PHOTO = -2;

    public List<Object> photoWithDayList = new ArrayList<>();  //date(String)  PhoteItem
    private Context mContext;
//    LinkedHashMap<String, List<PhotoItem>> mPhotoWithDay;

    class DateHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public DateHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            textView = itemView.findViewById(R.id.date);
        }
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View

            imageView = itemView.findViewById(R.id.image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position1 = getBindingAdapterPosition();
                    if(position1 != RecyclerView.NO_POSITION)
                    {
                        PhotoItem photo = (PhotoItem) photoWithDayList.get(position1);
                        List<LocalMedia> list=new ArrayList<>();

                        LocalMedia media=new LocalMedia();
                        String url= photo.getPath();
                        media.setPath(url);
                        list.add(media);

                        PictureSelector.create(itemView.getContext())
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
//            System.out.println(entry.getKey());
            for(PhotoItem item:entry.getValue()){
                photoWithDayList.add(item);
            }
        }
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view;
        if(viewType == DATE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_date,parent,false);
            return new DateHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_image,parent,false);
            return new PhotoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof DateHolder){
//            if(position==0){
//                ((DateHolder) holder).textView.lay
//            }
            ((DateHolder) holder).textView.setText((String)photoWithDayList.get(position));
        }
        else if(holder instanceof PhotoHolder){
//            GlideEngine.createGlideEngine().loadImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((PhotoHolder) holder).imageView);
            GlideEngine.createGlideEngine().loadGridImage(mContext,((PhotoItem)photoWithDayList.get(position)).getPath(),((PhotoHolder) holder).imageView);
//            ((PhotoHolder) holder).imageView.setImageResource((int)photoWithDayList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position){
        //return ??????????????????????????????????????????????????????????????????
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
