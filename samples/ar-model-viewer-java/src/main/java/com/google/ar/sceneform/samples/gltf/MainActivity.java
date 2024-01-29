package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.game.manager.EnemyNode;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        FragmentOnAttachListener,
        Scene.OnUpdateListener,
        BaseArFragment.OnSessionConfigurationListener,
        ArFragment.OnViewCreatedListener,
        View.OnClickListener
{

    private ArFragment arFragment;
    private ModelRenderable spaceInvaderModel;

    Date spawnEnemiesStartTime;

    private static final int MAX_HEALTH = 6;
    private static final int MAX_GAME_SPEED = 4000;
    private int health;
    private long score;
    private ArrayList<ImageView> healthImgs;
    private ArrayList<ImageView> gameSpeedImgs;
    private boolean isReady;
    private int gameSpeed;

    private ImageView useHealPotionBtn;
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

        useHealPotionBtn = findViewById(R.id.useHealPotionBtn);
        useHealPotionBtn.setOnClickListener(this);

        updateHealPotionCountTextView();
    }

    private void updateHealPotionCountTextView(){
        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);


        TextView healPotionCount = findViewById(R.id.healPointRemainTextView);
        healPotionCount.setText(String.format("x%d", users.get(0).healPotion));

        if (users.get(0).healPotion == 0) {
            useHealPotionBtn.setTransitionAlpha(0.5f);
        }
    }

    private void useHealPotion() {
        List<User> users = AppDatabase.getInstance(this).userDao().findUserByUserName(UserManagement.currentUser.username);

        if (health == MAX_HEALTH || (users.get(0).healPotion == 0)) return;


        AppDatabase.getInstance(this).userDao().updateHealPotion(-1, UserManagement.currentUser.username);

        ScaleAnimation animation = new ScaleAnimation(0.7f, 1f, 0.7f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animation.setRepeatCount(2);
        animation.setRepeatMode(Animation.REVERSE);

        useHealPotionBtn.startAnimation(animation);

        modifyHealth(1);


    }

    @Override
    public void onClick(View view) {
        useHealPotion();
        updateHealPotionCountTextView();
    }



    public void restart() {
        health = 6;
        gameSpeed = 4000;
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

        gameSpeedImgs = new ArrayList<>();
        gameSpeedImgs.add(findViewById(R.id.gameSpeed0));
        gameSpeedImgs.add(findViewById(R.id.gameSpeed1));
        gameSpeedImgs.add(findViewById(R.id.gameSpeed2));
        gameSpeedImgs.add(findViewById(R.id.gameSpeed3));

        updateGameSpeed(0);
    }

    public void updateGameSpeed(int offset) {

        if (gameSpeed + offset < 1000) {
            return;
        }

        if (gameSpeed + offset > MAX_GAME_SPEED) {
            gameSpeed = MAX_HEALTH;
        }

        gameSpeed += offset;

        for(int i = 0 ; i < gameSpeedImgs.size(); i++) {
            gameSpeedImgs.get(i).setVisibility(View.INVISIBLE);

            Log.i("gameSpeed", String.valueOf((MAX_GAME_SPEED - gameSpeed) / 1000));
            if (i <  (MAX_GAME_SPEED - gameSpeed) / 1000) {
                gameSpeedImgs.get(i).setVisibility(View.VISIBLE);
            }
        }
    }
    public void updateScore(long offset) {
        score += offset;

        TextView scoreTextview = findViewById(R.id.scoreTextView);
        scoreTextview.setText(String.format("Score: %d", score));

        AppDatabase.getInstance(this).userDao().updateScore(score, UserManagement.currentUser.username);
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

        health += offset;

        if (offset < 0) { //losing health

            updateGameSpeed(1000);
        }

        if (offset > 0) {
            for (int i = 0 ; i < health; i++){
                healthImgs.get(i).setImageResource(R.drawable.heart);
            }

            return;
        }

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
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
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

            updateGameSpeed(-100);
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

        if (millis < gameSpeed) return;

        spawnEnemiesStartTime = new Date();
        spawnEnemy();





    }


}
