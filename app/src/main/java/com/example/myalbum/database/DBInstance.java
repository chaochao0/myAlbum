//package com.example.myalbum.database;
//
//import androidx.room.Room;
//
//import com.google.gson.Gson;
//
//public class DBInstance {
//    private static DBInstance INSTANCE;
//    private static MyalbumDatabase db;
//
//    public static DBInstance getInstance() {
//        if (INSTANCE == null) {
//            synchronized (DBInstance.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new DBInstance();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    public MyalbumDatabase getDB() {
//        if (db == null) {
//            synchronized (DBInstance.class) {
//                if (db == null) {
////                    db = Room.databaseBuilder(getApplicationContext(),
////                            MyalbumDatabase.class, "myAlbumDatabase.db").build();
//                }
//            }
//        }
//        return db;
//    }
//}
