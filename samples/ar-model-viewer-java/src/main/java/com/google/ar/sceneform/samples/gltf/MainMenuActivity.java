package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

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


        TextView greetingTextView = findViewById(R.id.useGreetingTextView);
        greetingTextView.setText(String.format("Hello %s, your score: %d", users.get(0).username, users.get(0).score));

        findViewById(R.id.startGameBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });


    }
}