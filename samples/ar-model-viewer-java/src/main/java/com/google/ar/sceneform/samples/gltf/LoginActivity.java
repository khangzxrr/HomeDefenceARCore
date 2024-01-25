package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.ar.sceneform.samples.gltf.dao.UserManagement;
import com.google.ar.sceneform.samples.gltf.model.User;
import com.google.ar.sceneform.samples.gltf.repository.AppDatabase;

import java.util.List;

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginButton).setOnClickListener(view -> {

            EditText usernameTextField = findViewById(R.id.usernameTextField);

            if (usernameTextField.getText().length() == 0) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_LONG).show();
                return;
            }

            List<User> user = AppDatabase.getInstance(this).userDao().findUserByUserName(usernameTextField.getText().toString());

            Intent intent = new Intent(this, MainMenuActivity.class);

            if (user.size() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Account");
                builder.setMessage("Account is not found, do you want to create a new account?");
                builder.setPositiveButton("Create account", (dialog, id) -> {
                    // User taps OK button.
                    User newUser = new User(usernameTextField.getText().toString(), 0);
                    AppDatabase.getInstance(this).userDao().insertAll(newUser);

                    UserManagement.currentUser = newUser;

                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancels the dialog.
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                UserManagement.currentUser = user.get(0);
                startActivity(intent);
                finish();
            }
        });
    }
}