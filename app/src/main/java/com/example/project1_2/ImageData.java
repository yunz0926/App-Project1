package com.example.project1_2;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ImageData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="Resource")
    public Object resource;

    public ImageData(Object resource){
        this.resource = resource;
    }

}
