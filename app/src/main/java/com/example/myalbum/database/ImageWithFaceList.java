package com.example.myalbum.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ImageWithFaceList {
    @Embedded public Image image;
    @Relation(
            parentColumn = "imageId",
            entityColumn = "imageOwnerId"
    )
    public List<Face> faceList;
}
