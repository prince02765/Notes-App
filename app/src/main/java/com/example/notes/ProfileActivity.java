package com.example.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView circleImageView;
    TextInputEditText displayNameEditText;
    Button updateProfileButton;
    ProgressBar progressBar;

    String DISPLAY_NAME = null;
    String PROFILE_IMAGE_URL = null;

    private static final String TAG = "ProfileActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImageView = findViewById(R.id.circleImageView);
        displayNameEditText = findViewById(R.id.display_name_edit_text);
        updateProfileButton = findViewById(R.id.update_profile_button);
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(TAG, "onCreate: " + user.getDisplayName());

            if(user.getDisplayName() != null)
            {
                displayNameEditText.setText(user.getDisplayName());
                displayNameEditText.setSelection(user.getDisplayName().length());

            }
        }
        progressBar.setVisibility(View.GONE);
    }

    public void updateProfile(View view){
        view.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        DISPLAY_NAME = displayNameEditText.getText().toString();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(DISPLAY_NAME)
                .build();

        firebaseUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        view.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Successfully updated Profile", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    view.setEnabled(true);
                    Log.d(TAG, "onFailure: ",e.getCause());
                }
        });
    }
}