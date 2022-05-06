package com.example.myalbum.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.myalbum.utils.DateUtil;
import com.example.myalbum.utils.UIUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Google相册扫描器
 * Created by jiaojie.jia on 2017/3/15.
 */

public class AndroidPhotoScanner {

    private static final int MIN_SIZE = 1024 * 10;

    private static final long MIN_DATE = 1000000000;

    //扫描结果图片文件夹
    private static HashMap<String, ImageFolder> mGruopMap = new HashMap<>();

    //private static LinkedHashMap<String, List<PhotoItem>> mSectionsOfMonth = new LinkedHashMap<>();
    private static LinkedHashMap<String, List<PhotoItem>> mSectionsOfDay = new LinkedHashMap<>();

    private static List<ImageFolder> imageFloders = new ArrayList<>();

    public static ImageFolder mDefaultFolder;                  // 默认图片文件夹

    private static final SimpleDateFormat mDataFormatOfMonth = new SimpleDateFormat("yyyy年MM月");
    public static final SimpleDateFormat mDataFormatOfDay = new SimpleDateFormat("yyyy年MM月dd日");

    public static void startScan(){
        Log.i("startScan","start");
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        //readSystemGallery(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        readSystemGallery(collection);
    }

    private static void readSystemGallery(Uri uri){
        Log.i("readSystemGallery","start");
        //获取ContentResolver
        ContentResolver contentResolver = UIUtils.getContext().getContentResolver();
        Log.i("readSystemGallery ","1");
        //查询字段
        String[] projection = new String[]{MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATE_TAKEN};
        // 条件
//        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or "
//                + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";
        // 条件值
//        String[] selectionArgs = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        String[] selectionArgs = {"image/jpeg", "image/png"};

        // 排序
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        // 查询
        Cursor mCursor = MediaStore.Images.Media.query(contentResolver, uri, projection, selection, selectionArgs, sortOrder);
        Log.i("readSystemGallery ","2");
        while (mCursor != null && mCursor.moveToNext()) {
            //图片大小
            //getColumnIndexOrThrow
            @SuppressLint("Range") int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            //过滤掉10k以下的图片
            if(size < MIN_SIZE)
                continue;
            //修改日期
            @SuppressLint("Range") long modified = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            if(modified < MIN_DATE)
                continue;
            //图片路径
            @SuppressLint("Range") String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            if(TextUtils.isEmpty(path))
                continue;
            //图片Id
            @SuppressLint("Range") int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
            //图片宽度
            @SuppressLint("Range") int width = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
            //图片高度
            @SuppressLint("Range") int height = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
            //拍摄日期
            @SuppressLint("Range") int takendate = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
            double longitude = mCursor.getDouble(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE));
            double latitude = mCursor.getDouble(mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE));
            @SuppressLint("Range") int orientation = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

//            Log.i("readSystemGallery contentUri",contentUri.toString());
//            Log.i("readSystemGallery absolutepath",path);

            String parentName = new File(path).getParent();
            PhotoItem photoItem = new PhotoItem(id, path, width, height, size, latitude, longitude, 0, orientation, takendate, modified);

            // 查询缩略图非常消耗性能
//            photoItem.setThumbnail(getThumbnail(id));
            //根据父路径名将图片放入到mGruopMap中
            if (!mGruopMap.containsKey(parentName)) {
                ImageFolder floder = new ImageFolder();
                floder.setDir(parentName);
                floder.setFirstImagePath(path);
                floder.setCount(floder.getCount() + 1);
                List<PhotoItem> photoList = new ArrayList<>();
                photoList.add(photoItem);
                if(floder.isPhoto()) {
                    mDefaultFolder = floder;
//                    sortPhotosByMonth(photoItem);
                    sortPhotosByDay(photoItem);
                }
                floder.setList(photoList);
                mGruopMap.put(parentName, floder);
                imageFloders.add(floder);
                if(mDefaultFolder == null || !mDefaultFolder.isPhoto()) {
                    mDefaultFolder = floder;
                }
            } else {
                ImageFolder floder = mGruopMap.get(parentName);
                floder.setCount(floder.getCount() + 1);
                floder.getList().add(photoItem);
                if(floder.isPhoto()) {
//                    sortPhotosByMonth(photoItem);
                    sortPhotosByDay(photoItem);
                }
            }
        }
        if (mCursor != null) {
            mCursor.close();
        }
        Log.i("readSystemGallery ","4");
    }

    /** 获取照片缩略图 */
    @SuppressLint("Range")
    private static String getThumbnail(int imageId) {
        String thumbnailPath = null;
        final String[] projection = {MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID};
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(UIUtils.getContext().getContentResolver(),
                imageId, MediaStore.Images.Thumbnails.MICRO_KIND, projection);
        if(cursor != null && cursor.moveToFirst()) {
            thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }
        return thumbnailPath;
    }

    /** 根据当前视图，返回对应数据 */
    public static LinkedHashMap<String, List<PhotoItem>> getPhotoSections() {
//        switch (viewType) {
//            case DAY:
//                return mSectionsOfDay;
//            case YEAR:
//            case MONTH:
//            default:
//                return mSectionsOfMonth;
//        }
        return mSectionsOfDay;
    }

    public static List<ImageFolder> getImageFloders() {
        return imageFloders;
    }

//    /** 把照片按月分类 */
//    private static void sortPhotosByMonth(PhotoItem photo) {
//        Date date = new Date(photo.getModified() * 1000);
//        String millisecond = mDataFormatOfMonth.format(date);
//        if(!mSectionsOfMonth.containsKey(millisecond)) {
//            List<PhotoItem> section = new ArrayList<>();
//            section.add(photo);
//            mSectionsOfMonth.put(millisecond, section);
//        } else {
//            List<PhotoItem> section = mSectionsOfMonth.get(millisecond);
//            section.add(photo);
//        }
//    }

    /** 把照片按日分类 */
    public static void sortPhotosByDay(PhotoItem photo) {
        Date date = new Date(photo.getModified() * 1000);
        String detail = mDataFormatOfDay.format(date);
        String week = DateUtil.getWeek(date);
        String dayKey = detail + week;
        if(!mSectionsOfDay.containsKey(dayKey)) {
            List<PhotoItem> section = new ArrayList<>();
            section.add(photo);
            mSectionsOfDay.put(dayKey, section);
        } else {
            List<PhotoItem> section = mSectionsOfDay.get(dayKey);
            section.add(photo);
        }
    }

    public static void clear() {
        if(mGruopMap != null)
            mGruopMap.clear();
        if(imageFloders != null)
            imageFloders.clear();
        if(mSectionsOfDay != null)
            mSectionsOfDay.clear();
        mDefaultFolder = null;
    }

    public void insertImageToMediaStore(){
//        // 在用户的集合中创建一个新的图片
//        val values = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG1024.JPG")
//            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            put(MediaStore.Images.Media.IS_PENDING, 1)
//        }
//
//        val resolver = context.getContentResolver()
//        val collection = MediaStore.Images.Media
//                .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//        val item = resolver.insert(collection, values)
//
//// 向我们的图片中写入数据
//        resolver.openFileDescriptor(item, "w", null).use { pfd ->
//            // ...
//        }
//
//// 现在数据写入完毕，可以让其他应用查看了
//        values.clear()
//        values.put(MediaStore.Images.Media.IS_PENDING, 0)
//        resolver.update(item, values, null, null)
//————————————————
//        版权声明：本文为CSDN博主「晚风清扬」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//        原文链接：https://blog.csdn.net/KSWistron/article/details/104574242
    }
}
