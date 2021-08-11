package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity<ExtendedFloatingActionButton> extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NotesRecyclerAdapter.NoteListener {
    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    NotesRecyclerAdapter notesRecyclerAdapter;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabAddNote;
    FloatingActionButton fabAddDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);

        fabMenu = findViewById(R.id.fab_menu);
        fabAddNote = findViewById(R.id.flaot_add_note);
        fabAddDraw = findViewById(R.id.flaot_add_draw);

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity();
                fabMenu.close(true);
            }
        });

        fabAddDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, drawActiviy.class);
                startActivity(intent);
                fabMenu.close(true);
            }
        });
    }

    private void showActivity() {
        Intent intent = new Intent(MainActivity.this, addNote.class);
        startActivity(intent);
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this,LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this);
                return true;

            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                return true;

            case R.id.dev_contact:
                startActivity(new Intent(MainActivity.this,devContactActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if(notesRecyclerAdapter != null){
            notesRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            startLoginActivity();
            return;
        }
        initRecyclerView(firebaseAuth.getCurrentUser());
    }

    private void initRecyclerView(FirebaseUser user){
        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userId", user.getUid())
                .orderBy("completed", Query.Direction.ASCENDING)
                .orderBy("created", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        notesRecyclerAdapter = new NotesRecyclerAdapter(options,this);
        recyclerView.setAdapter(notesRecyclerAdapter);

        notesRecyclerAdapter.startListening();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Toast.makeText(MainActivity.this, "Deleting", Toast.LENGTH_SHORT).show();

                NotesRecyclerAdapter.NoteViewHolder noteViewHolder = (NotesRecyclerAdapter.NoteViewHolder) viewHolder;
                noteViewHolder.deletItem();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.delete))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot) {
        Log.d(TAG, "handleCheckChanged: " + isChecked);
        snapshot.getReference().update("completed", isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void handleEditNote(DocumentSnapshot snapshot) {

        Note note = snapshot.toObject(Note.class);

        EditText editText = new EditText( this);
        editText.setText(note.getText().toString());
        editText.setSelection(note.getText().length());
        new AlertDialog.Builder(this)
                .setTitle("Edit Note")
                .setView(editText)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newText = editText.getText().toString();
                        note.setText(newText);
                        snapshot.getReference().set(note)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: Successfully Updated");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: "+ e.getLocalizedMessage());
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancle", null)
                .show();
    }

    @Override
    public void handledeleteItem(DocumentSnapshot snapshot) {

        DocumentReference documentReference = snapshot.getReference();
        Note note = snapshot.toObject(Note.class);

        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Item Deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                    }
                });

        Snackbar.make(recyclerView, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        documentReference.set(note);
                    }
                })
                .show();
    }
}