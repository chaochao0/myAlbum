package com.example.myalbum.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;


//默认情况下，为了避免糟糕的UI性能，Room不允许你在主线程上发出查询。当Room查询返回LiveData时，查询将自动在后台线程上异步运行。

@Dao
public interface ImageDao {
    //向 DAO 类添加一个方法，用于返回将父实体与子实体配对的数据类的所有实例。该方法需要 Room 运行两次查询，因此应向该方法添加 @Transaction 注释，以确保整个操作以原子方式执行。
    @Transaction
    @Query("SELECT * FROM image")
    public LiveData<List<ImageWithFaceList>> getImageWithFaceList();

//    @Transaction
//    @Query("SELECT * FROM image where imageId = :imageId_")
//    public ImageWithFaceList getImageWithFaceList(int imageId_);

    @Query("SELECT * FROM image")
    LiveData<List<Image>> getImageList();


    @Query("SELECT * FROM image WHERE path = :path")
    Image getImage(String path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Image image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Image> images);

    @Delete
    void deleteImage(Image image);

    @Delete
    void deleteImage(List<Image> images);

    @Update
    void updateImage(Image image);  //Room 使用主键将传递的实体实例与数据库中的行进行匹配。如果没有具有相同主键的行，Room 不会进行任何更改。

}
