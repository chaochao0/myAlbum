package com.example.myalbum.database;

import com.google.gson.Gson;

//https://blog.csdn.net/BADAO_LIUMANG_QIZHI/article/details/111246352?ops_request_misc=&request_id=&biz_id=102&utm_term=android%20%E5%AD%98%E5%82%A8%E4%B8%80%E4%B8%AAlist&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~sobaiduweb~default-8-111246352.nonecase&spm=1018.2226.3001.4450
public class GsonInstance {
    private static GsonInstance INSTANCE;
    private static Gson gson;

    public static GsonInstance getInstance() {
        if (INSTANCE == null) {
            synchronized (GsonInstance.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GsonInstance();
                }
            }
        }
        return INSTANCE;
    }

    public Gson getGson() {
        if (gson == null) {
            synchronized (GsonInstance.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

}
