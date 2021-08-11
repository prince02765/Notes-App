package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity( new Intent(this, MainActivity.class));
            this.finish();
        }
    }

    public void handleLogin(View view) {

        List<AuthUI.IdpConfig> provider = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setLogo(R.drawable.second_notes)
                .setAlwaysShowSignInMethodScreen(true)
                .build();
        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AUTHUI_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // We have signed in the user or we have a new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.getEmail());

                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Welcome Back Again", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }

            else{
                // Signed in failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response==null){
                    Log.d(TAG, "onActivityResult: The user has cancelled the signed in request");
                }
                else{
                    Log.d(TAG, "onActivityResult: ", response.getError());
                }

            }
        }
    }
}
