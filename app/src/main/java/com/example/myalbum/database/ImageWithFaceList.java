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

    public void printInfo(){
        image.printInfo();
        for(int j = 0;j<image.faceNum;j++){
            System.out.println("    face_"+j+" faceId:"+faceList.get(j).faceId);
            System.out.println("    face_"+j+" ownerId:"+faceList.get(j).imageOwnerId);
            System.out.println("    face_"+j+" faceClusterType:"+faceList.get(j).faceClusterType);
        }
    }
}
