package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import org.w3c.dom.Text;

import java.util.List;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        List<User> users = AppDatabase.getInstance(this).userDao().getAll();

        TextView top1TextView = findViewById(R.id.top1TextView);
        TextView top2TextView = findViewById(R.id.top2TextView);
        TextView top3TextView = findViewById(R.id.top3TextView);

        if (users.size() > 0) {
            top1TextView.setText(String.format("%s - %d", users.get(0).username, users.get(0).score));
        }
        if (users.size() > 1) {
            top2TextView.setText(String.format("%s - %d", users.get(1).username,  users.get(1).score));
        }
        if (users.size() > 2) {
            top3TextView.setText(String.format("%s - %d", users.get(2).username,  users.get(2).score));
        }

    }
}