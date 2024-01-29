package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import java.util.List;

public class ShopActivity extends AppCompatActivity implements View.OnClickListener {

    User currentUser;
    TextView yourScoreTextView;

    ImageView healPotionItemImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);

        if (users.size() == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        currentUser = users.get(0);



        yourScoreTextView = findViewById(R.id.yourScoreTextView);
        yourScoreTextView.setText(String.format("Your score point: %d\nYour heal potion: %d", currentUser.score, currentUser.healPotion));

        healPotionItemImageView = findViewById(R.id.healPotionItemShop);

        healPotionItemImageView.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (currentUser.score < 300) {
            Toast.makeText(this, "Your score is lower than item score point! PLAY MORE!!", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.getInstance(this).userDao().updateHealPotion(1, currentUser.username);
        AppDatabase.getInstance(this).userDao().updateScore(-300, currentUser.username);

        List<User>  refreshedUsers = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);
        currentUser = refreshedUsers.get(0);

        yourScoreTextView.setText(String.format("Your score point: %d\nYour heal potion: %d", currentUser.score, currentUser.healPotion));


        ScaleAnimation animation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.REVERSE);

        healPotionItemImageView.startAnimation(animation);

        Toast.makeText(this, "Purchase successfully!", Toast.LENGTH_LONG).show();

    }
}