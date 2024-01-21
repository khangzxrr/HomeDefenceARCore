package com.google.ar.sceneform.samples.gltf.game.manager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.samples.gltf.R;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.Random;

public class GameManager {

    private Renderable houseModel;
    private Renderable spaceInvaderModel;
    private ViewRenderable healthBarModel;


    private Node houseNode;
    private Node healthBarNode;

    private Context context;
    private ArFragment arFragment;

    private int enemyCount;

    private int health;
    private boolean isReady ;

    public GameManager(Context context, ArFragment arFragment) {
        this.context = context;
        this.arFragment = arFragment;
        isReady = false;
        enemyCount = 0;
        health = 100;

        loadModels();
    }

    public boolean isLoadedModels() {
        return (houseModel != null ) && (spaceInvaderModel != null) && (healthBarModel != null);
    }


    public void spawnEnemies(){

        if (!isReady || enemyCount > 1) {
            return;
        }

        Log.i("onUpdate", "SPAWN enemy space invader: " + ((spaceInvaderModel != null) ? "OK" : "NULL"));

        EnemyNode enemyNode = new EnemyNode(spaceInvaderModel, arFragment.getArSceneView().getScene(), houseNode, () -> {
            health -= 10;
            enemyCount--;
            ProgressBar progressBar = (ProgressBar) healthBarModel.getView();
            progressBar.setProgress(health);

            ViewRenderable.builder().setView(context, progressBar)
                    .build()
                    .thenAccept(viewRenderable -> {
                        healthBarNode.setRenderable(viewRenderable);
                    });

        }, () -> enemyCount--);
        enemyNode.spawn();

        enemyCount++;





    }


    public void setupHouse(HitResult hitResult) {
        if (isReady) {
            return;
        }
        // Create the Anchor.
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Create the transformable houseNode and add it to the anchor.
        houseNode = new Node();

        houseNode.setLocalScale(new Vector3(1f, 1f, 1f));

        houseNode.setParent(anchorNode);
        houseNode.setRenderable(this.houseModel)
                .animate(true).start();

        healthBarNode = new Node();
        healthBarNode.setParent(houseNode);
        healthBarNode.setEnabled(false);
        healthBarNode.setLocalPosition(new Vector3(0.0f, 0.3f, 0.0f));
        healthBarNode.setRenderable(healthBarModel);
        healthBarNode.setEnabled(true);

        isReady = true;
    }

    public void loadModels() {

        ModelRenderable.builder()
                .setSource(context, Uri.parse("space_invader.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    spaceInvaderModel = model;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            context, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

        ModelRenderable.builder()
                .setSource(context, Uri.parse("house.glb"))
                .setIsFilamentGltf(true)
                .setAsyncLoadEnabled(true)
                .build()
                .thenAccept(model -> {
                    houseModel = model;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(
                            context, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });

        ViewRenderable.builder()
                .setView(context, R.layout.view_model_title)
                .build()
                .thenAccept(viewRenderable -> {
                    healthBarModel = viewRenderable;
                })
                .exceptionally(throwable -> {
                    Toast.makeText(context, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }


}
