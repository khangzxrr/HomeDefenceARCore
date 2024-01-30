package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.particles.ParticleSystem;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import org.w3c.dom.Text;

import java.util.List;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        TextView scoreView = findViewById(R.id.gameOverScoreView);

        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);

        if (users.size() == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        scoreView.setText(String.format("Hello %s, your score: %d", users.get(0).username, users.get(0).score));

        findViewById(R.id.tryAgainBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.backtoMainMenuBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });

        new ParticleSystem(this, 80, R.drawable.space_invader, 10000)
                .setScaleRange(0.1f, 0.1f)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 360)
                .setRotationSpeed(144)
                .setAcceleration(0.00005f, 90)
                .emit(findViewById(R.id.tryAgainBtn), 8);

        MediaPlayer gameOverPlayer = MediaPlayer.create(this, R.raw.gameover);
        gameOverPlayer.start();
    }
}