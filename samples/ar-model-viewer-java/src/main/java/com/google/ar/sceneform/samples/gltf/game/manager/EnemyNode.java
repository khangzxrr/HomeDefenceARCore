package com.google.ar.sceneform.samples.gltf.game.manager;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.Renderable;

import java.util.Random;

public class EnemyNode extends Node {

    private final Node targetNode;
    private final Renderable model;
    private final Scene scene;

    private final IOnTouchTargetCallback onTouchTargetCallback;

    private boolean touchedPlayer;

    ObjectAnimator objectAnimation;

    public EnemyNode(Renderable enemyModel, Scene scene, Node targetNode, IOnTouchTargetCallback onTouchTargetCallback, IOnDestroyCallback onDestroyCallback) {

        this.onTouchTargetCallback = onTouchTargetCallback;

        touchedPlayer = false;
        this.targetNode = targetNode;
        model = enemyModel;
        this.scene = scene;

        setOnTapListener((hitTestResult, motionEvent) -> {
            setParent(null);

            objectAnimation.cancel();
            onDestroyCallback.onDestroy();

            Log.i("onDestroy", "callback on tap destroy");
        });

    }

    public void spawn() {

        setParent(scene);
        setRenderable(model);

        setLocalScale(new Vector3(0.06f, 0.06f, 0.06f));


        Random random = new Random();

//        float randomXOffset = 0;
//        float randomZOffset = 0;
        float randomXOffset = (random.nextFloat() + 0.01f)  * (random.nextBoolean() ? 1 : -1);
        float randomZOffset = (random.nextFloat() + 0.01f)  * (random.nextBoolean() ? 1 : -1);

        Vector3 randomForwardDirection = targetNode.getForward().scaled(1f);
        Vector3 startNodeWorldPosition = Vector3.add(targetNode.getWorldPosition(), new Vector3(randomForwardDirection.x + randomXOffset, randomForwardDirection.y, randomForwardDirection.z + randomZOffset));

        startNodeWorldPosition.set(startNodeWorldPosition.x, startNodeWorldPosition.y, startNodeWorldPosition.z);
        setWorldPosition(startNodeWorldPosition);

        startWalking(this, targetNode);
    }


    private void startWalking(Node startNode, Node endNode) {
        objectAnimation = new ObjectAnimator();
        objectAnimation.setAutoCancel(true);
        objectAnimation.setTarget(startNode);

        Vector3 endNodePosition = endNode.getWorldPosition();

        // All the positions should be world positions
        // The first position is the start, and the second is the end.
        objectAnimation.setObjectValues(startNode.getWorldPosition(), endNodePosition);


        // Use setWorldPosition to position andy.
        objectAnimation.setPropertyName("worldPosition");

        objectAnimation.addUpdateListener(valueAnimator -> {

            Vector3 currentAnimatedPostion = (Vector3) valueAnimator.getAnimatedValue("worldPosition");

            if (getDistanceBetweenVectorsInMeters(endNodePosition, currentAnimatedPostion) < 0.01f) {
                objectAnimation.cancel();
                setParent(null);

                Log.i("animation.addUpdateListener", "touch player!");
                onTouchTargetCallback.onTouchTarget();
            }
        });
        // The Vector3Evaluator is used to evaluator 2 vector3 and return the next
        // vector3.  The default is to use lerp.
        objectAnimation.setEvaluator(new Vector3Evaluator());
        // This makes the animation linear (smooth and uniform).
        objectAnimation.setInterpolator(new LinearInterpolator());
        // Duration in ms of the animation.
        objectAnimation.setDuration(4500);
        objectAnimation.start();



    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();

        Log.i("on deactive node", "deactive!!!");
    }



    private float getDistanceBetweenVectorsInMeters(Vector3 to, Vector3 from)
    {
        // Compute the difference vector between the two hit locations.
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        float dz = to.z - from.z;

        // Compute the straight-line distance (distanceMeters)
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
