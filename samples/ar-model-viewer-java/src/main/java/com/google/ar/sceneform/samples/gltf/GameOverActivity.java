package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.particles.ParticleSystem;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

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