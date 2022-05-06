package com.example.myalbum.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//如果您的应用在单个进程中运行，在实例化 AppDatabase 对象时应遵循单例设计模式。每个 RoomDatabase 实例的成本相当高，而您几乎不需要在单个进程中访问多个实例。

@Database(entities = {Image.class,Face.class},version = 1,exportSchema = false)
public abstract class MyalbumDatabase extends RoomDatabase {
    public abstract ImageDao imageDao();
    public abstract  FaceDao faceDao();

    private static volatile MyalbumDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static public MyalbumDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyalbumDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MyalbumDatabase.class, "myAlbumDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
