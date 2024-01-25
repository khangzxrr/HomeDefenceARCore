package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.samples.gltf.game.manager.EnemyNode;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        Scene.OnUpdateListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener {

    private ArFragment arFragment;
    private ModelRenderable spaceInvaderModel;

    Date spawnEnemiesStartTime;

    private static final int MAX_HEALTH = 6;
    private int health;
    private long score;
    private ArrayList<ImageView> healthImgs;
    private boolean isReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().addFragmentOnAttachListener(this);

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }


        loadModels();
        restart();
    }

    public void restart() {
        health = 6;
        healthImgs = new ArrayList<>();

        healthImgs.add(findViewById(R.id.heart0));
        healthImgs.add(findViewById(R.id.heart1));
        healthImgs.add(findViewById(R.id.heart2));
        healthImgs.add(findViewById(R.id.heart3));
        healthImgs.add(findViewById(R.id.heart4));
        healthImgs.add(findViewById(R.id.heart5));

        for (ImageView healthImage : healthImgs) {
            healthImage.setImageResource(R.drawable.heart);
        }
    }

    public void updateScore(long offset) {
        score += offset;

        TextView scoreTextview = findViewById(R.id.scoreTextView);
        scoreTextview.setText(String.format("Score: %d", score));
    }
    public void navigateToGameOverActivity() {
        Intent intent = new Intent(this, GameOverActivity.class);
        startActivity(intent);
        finish();
    }
    public void modifyHealth(int offset) {
        if (health == 0) {
            return;
        }

        if (health == MAX_HEALTH && offset > 0) {
            return;
        }

        health += offset;

        if (health < MAX_HEALTH) {
            int totalMissingHealth = MAX_HEALTH - health;

            for(int i = healthImgs.size() - 1; i > healthImgs.size() - 1 - totalMissingHealth; i--) {
                healthImgs.get(i).setImageResource(R.drawable.heart_empty);
            }
        }

        if (health == 0) { //game over
            navigateToGameOverActivity();
        }
    }



    public void loadModels() {

        ModelRenderable.builder()
                .setSource(this, Uri.parse("space_invader.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    spaceInvaderModel = model;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

    }

    @Override
    public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragment) {
            arFragment = (ArFragment) fragment;
            arFragment.setOnSessionConfigurationListener(this);
            arFragment.setOnViewCreatedListener(this);

            isReady = true;
        }
    }

    @Override
    public void onSessionConfiguration(Session session, Config config) {
        config.setFocusMode(Config.FocusMode.AUTO);
        arFragment.getInstructionsController().setEnabled(false);
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR );

    }

    @Override
    public void onViewCreated(ArSceneView arSceneView) {
        arFragment.setOnViewCreatedListener(null);

        // Fine adjust the maximum frame rate
        arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL);
        arSceneView.getScene().addOnUpdateListener(this::onUpdate);
    }

    public void spawnEnemy(){

        if (!isReady) {
            return;
        }

        Log.i("onUpdate", "SPAWN enemy space invader: " + ((spaceInvaderModel != null) ? "OK" : "NULL"));


        EnemyNode enemyNode = new EnemyNode(spaceInvaderModel, arFragment.getArSceneView().getScene(), arFragment.getArSceneView().getScene().getCamera(), () -> {
            modifyHealth(-1);
        }, () -> {
            updateScore(100);
        });
        enemyNode.spawn();


    }


    @Override
    public void onUpdate(FrameTime frameTime) {
        if (spawnEnemiesStartTime == null) {
            spawnEnemiesStartTime = new Date();
        }

        Date currentDateTime = new Date();

        long millis = currentDateTime.getTime() - spawnEnemiesStartTime.getTime();

        if (millis > 5000) {
            spawnEnemiesStartTime = new Date();
            spawnEnemy();
        }





    }
}
