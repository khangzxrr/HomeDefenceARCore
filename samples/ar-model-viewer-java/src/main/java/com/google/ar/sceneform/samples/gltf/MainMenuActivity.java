package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    TextView greetingTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);

        if (users.size() == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        ImageView logo = findViewById(R.id.mainMenuLogo);

        ScaleAnimation animation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        logo.startAnimation(animation);


        greetingTextView = findViewById(R.id.useGreetingTextView);
        greetingTextView.setText(String.format("Hello %s, your score: %d", users.get(0).username, users.get(0).score));

        findViewById(R.id.startGameBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.scoreboardBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, ScoreboardActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.shopBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);

        if (users.size() == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        greetingTextView.setText(String.format("Hello %s, your score: %d", users.get(0).username, users.get(0).score));


    }
}