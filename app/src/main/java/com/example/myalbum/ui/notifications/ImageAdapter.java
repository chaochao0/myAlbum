package com.example.myalbum.ui.notifications;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myalbum.GlideEngine;
import com.example.myalbum.R;
import com.example.myalbum.data.PhotoItem;
import com.example.myalbum.model.ImageTransfer;
import com.example.myalbum.ui.home.RecyclerViewAdapter;
import com.youth.banner.adapter.BannerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 */
public class ImageAdapter extends BannerAdapter<Integer, ImageAdapter.BannerViewHolder> {

    Context mContext;
    List<Integer> drawImageId;
    public ImageAdapter(Context context,List<Integer> imagePathList) {
        super(imagePathList);
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        mContext = context;
        drawImageId = new ArrayList<>();
        drawImageId = imagePathList;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_banner,parent,false);
        return new BannerViewHolder(view);
//        ImageView imageView = new ImageView(parent.getContext());
//        //注意，必须设置为match_parent，这个是viewpager2强制要求的
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        return new BannerViewHolder(imageView);
    }

    /**
     * 绑定布局数据
     *
     * @param holder   XViewHolder
     * @param data     数据实体
     * @param position 当前位置
     * @param size     总数
     */
    @Override
    public void onBindView(BannerViewHolder holder, Integer data, int position, int size) {
        Glide.with(holder.itemView)
                .load(data)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(holder.imageView);
        holder.textView.setText(ImageTransfer.ID_TO_TRANSFER_CLASSES[position]);
    }

//    @Override
//    public void onBindView(BannerViewHolder holder, String data, int position, int size) {
//
//        GlideEngine.createGlideEngine().loadGridImage(mContext,data,holder.imageView);
//        holder.textView.setText(ImageTransfer.ID_TO_TRANSFER_CLASSES[position]);
//    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            this.imageView = view.findViewById(R.id.banner_image_view);
            this.textView = view.findViewById(R.id.banner_textview);
            mContext = view.getContext();
        }
    }
}
