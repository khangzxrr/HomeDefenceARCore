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

    @ColumnInfo(name = "heal_potion")
    public long healPotion;

    public User(String username) {

        this.username = username;

        score = 9999;
        healPotion = 0;
    }
}
