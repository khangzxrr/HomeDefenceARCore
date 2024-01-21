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

    private boolean isTouchedTarget;
    public EnemyNode(Renderable enemyModel, Scene scene, Node targetNode, IOnTouchTargetCallback onTouchTargetCallback, IOnDestroyCallback onDestroyCallback) {

        this.onTouchTargetCallback = onTouchTargetCallback;
        isTouchedTarget = false;


        this.targetNode = targetNode;
        model = enemyModel;
        this.scene = scene;

        setOnTapListener((hitTestResult, motionEvent) -> {
            setParent(null);
            onDestroyCallback.onDestroy();

            Log.i("onDestroy", "callback on tap destroy");
        });

    }

    public void spawn() {

        setParent(scene);
        setRenderable(model);

        setLocalScale(new Vector3(0.06f, 0.06f, 0.06f));


        Random random = new Random();

        float randomXOffset = (random.nextFloat() + 0.2f)  * (random.nextBoolean() ? 1 : -1);
        float randomZOffset = (random.nextFloat() + 0.2f)  * (random.nextBoolean() ? 1 : -1);


        Vector3 startNodeWorldPosition = targetNode.getWorldPosition();
        startNodeWorldPosition.set(startNodeWorldPosition.x + randomXOffset, startNodeWorldPosition.y, startNodeWorldPosition.z + randomZOffset);
        setWorldPosition(startNodeWorldPosition);

        startWalking(this, targetNode);
    }


    private void startWalking(Node startNode, Node endNode) {
        ObjectAnimator objectAnimation = new ObjectAnimator();
        objectAnimation.setAutoCancel(true);
        objectAnimation.setTarget(startNode);

        // All the positions should be world positions
        // The first position is the start, and the second is the end.
        objectAnimation.setObjectValues(startNode.getWorldPosition(), endNode.getWorldPosition());

        // Use setWorldPosition to position andy.
        objectAnimation.setPropertyName("worldPosition");

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
    public void onTransformChange(Node originatingNode) {
        super.onTransformChange(originatingNode);



        if ((!isTouchedTarget) && (getDistanceBetweenVectorsInMeters(getWorldPosition(), targetNode.getWorldPosition()) < 0.01f)){

            isTouchedTarget = true;

            Log.i("position", String.valueOf(getDistanceBetweenVectorsInMeters(this.getWorldPosition(), targetNode.getWorldPosition())));

            originatingNode.setParent(null);


            onTouchTargetCallback.onTouchTarget();
        }
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
