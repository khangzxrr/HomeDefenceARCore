package com.google.ar.sceneform.samples.gltf.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.google.ar.sceneform.samples.gltf.model.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM user ORDER BY score DESC")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE username = :username")
    List<User> findUserByUserName(String username);

    @Insert
    void insertAll(User... users);

    @Query("UPDATE user SET score = score + :score WHERE username = :username")
    void updateScore(long score, String username);

    @Query("UPDATE user SET heal_potion = heal_potion + :healPotionOffset WHERE username = :username")
    void updateHealPotion(long healPotionOffset, String username);




}
