package com.example.myalbum.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FaceDao {

    @Query("SELECT * FROM face")
    LiveData<List<Face>> getFaceList();

    @Query("SELECT * FROM face")
    List<Face> getFaceListNow();

//    @Query("SELECT * FROM face where imageOwnerId = :image.imageId ")
//    LiveData<List<Face>> getFaceList(Image image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Face face);

    @Insert
    void insert(List<Face> faces);

    @Delete
    void deleteFace(Face face);

    @Delete
    void deleteFace(List<Face> faces);

    @Update
    void updateFace(Face face);  //Room 使用主键将传递的实体实例与数据库中的行进行匹配。如果没有具有相同主键的行，Room 不会进行任何更改。

}
