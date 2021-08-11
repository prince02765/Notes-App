  package com.example.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class addNote extends AppCompatActivity {

    TextView save_btn;
    EditText title, note;
    private static final String TAG = "addNote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        save_btn = findViewById(R.id.save);
        title = findViewById(R.id.edit_title);
        note = findViewById(R.id.edit_note);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Note nt = new Note(note.getText().toString(), title.getText().toString(), false, new Timestamp(new Date()), userId);

                FirebaseFirestore.getInstance()
                        .collection("notes")
                        .add(nt)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: Successfully added note");
                                Toast.makeText(addNote.this, "Successfully added note", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(addNote.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }

        });

    }
}