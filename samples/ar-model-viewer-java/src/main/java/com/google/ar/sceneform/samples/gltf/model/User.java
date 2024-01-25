package com.google.ar.sceneform.samples.gltf.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "score")
    public long score;

    public User(String username, long score) {

        this.username = username;
        this.score = score;
    }
}
