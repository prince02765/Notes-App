package com.example.notes;

import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesRecyclerAdapter extends FirestoreRecyclerAdapter<Note,NotesRecyclerAdapter.NoteViewHolder> {

    NoteListener noteListener;
    private static final String TAG = "NotesRecyclerAdapter";

    public NotesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options, NoteListener noteListener) {
        super(options);
        this.noteListener = noteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.noteTextView.setText(note.getText());
        holder.noteTitleView.setText(note.getTitle());
        holder.checkBox.setChecked(note.getCompleted());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.cardView.setCardBackgroundColor(holder.cardView.getResources().getColor(getRandomColor(), null));
        }
        CharSequence dateCharSeq = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", note.getCreated().toDate());

        holder.dateTextView.setText(dateCharSeq);
    }

    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.purple);
        colorCode.add(R.color.Deep_Purple);
        colorCode.add(R.color.pink);

        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.line);

        colorCode.add(R.color.design_default_color_secondary_variant);
        colorCode.add(R.color.blue2);
        colorCode.add(R.color.Indigo);

        colorCode.add(R.color.notgreen);
        colorCode.add(R.color.Light_Blue);
        colorCode.add(R.color.Teal);

        colorCode.add(R.color.Green);
        colorCode.add(R.color.Light_Green);
        colorCode.add(R.color.Lime);

        colorCode.add(R.color.Yellow);
        colorCode.add(R.color.Amber);
        colorCode.add(R.color.Brown);
        colorCode.add(R.color.Blue_Grey);

        Random random = new Random();
        int number =random.nextInt(colorCode.size());

        return colorCode.get(number);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_row, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTextView, noteTitleView, dateTextView;
        CheckBox checkBox;
        MaterialCardView cardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTextView = itemView.findViewById(R.id.noteTextView);
            noteTitleView = itemView.findViewById(R.id.noteTitleView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            cardView = itemView.findViewById(R.id.item_card_view);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isCheck) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    Note note = getItem(getAdapterPosition());
                    if (note.getCompleted() != isCheck) {
                        noteListener.handleCheckChanged(isCheck, snapshot);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    noteListener.handleEditNote(snapshot);
                }
            });
        }

        public void deletItem() {
            noteListener.handledeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    interface NoteListener {
         void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);

         void handleEditNote(DocumentSnapshot snapshot);
         void handledeleteItem(DocumentSnapshot snapshot);
    }
}