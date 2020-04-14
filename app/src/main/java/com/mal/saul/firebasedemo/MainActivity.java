package com.mal.saul.firebasedemo;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("MainActivity", "error al obtener el token", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                Log.d("MainActivity", token);
            }
        });

        registerToFirebaseTopic();
    }

    private void registerToFirebaseTopic() {
        Log.d("MainActivity", "registering: ");
        FirebaseMessaging.getInstance().subscribeToTopic("test").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("MainActivity", "Subscribed");
                } else
                    Log.d("MainActivity", "Not subscribed" + task.getException());
            }
        });
    }
}
