package com.example.notes;



import androidx.appcompat.app.AppCompatActivity;




import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;




public class NoteDetailActivity extends AppCompatActivity {


    ImageView image;
    EditText title_edittext, content_edittext;
    ImageButton save_btn;
    TextView  deleteNoteTextViewBtn;
    String title, content, docId;

    boolean iseditmode = false, new_btn = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        title_edittext = findViewById(R.id.notes_title_text);
        content_edittext = findViewById(R.id.notes_content_text);
        save_btn = findViewById(R.id.save_note_btn);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);
        image = findViewById(R.id.image);



        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        title_edittext.setText(title);
        content_edittext.setText(content);

        if (docId != null && !docId.isEmpty()) {
            iseditmode = true;
        }
        if (iseditmode) {

            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }



        save_btn.setOnClickListener(v -> savenotes());

        deleteNoteTextViewBtn.setOnClickListener(v -> deletenotefromFirebase());
    }



    void savenotes() {
        String title = title_edittext.getText().toString();
        String content = content_edittext.getText().toString();
        if (title == null || title.isEmpty()) {
            title_edittext.setError("Title should not be empty!!!");
            return;
        }
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(Timestamp.now());
        savenotefirebase(note);
    }

    void savenotefirebase(Note note) {
        DocumentReference docrefer;
        if (iseditmode) {
            docrefer = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            docrefer = Utility.getCollectionReferenceForNotes().document();
        }
        docrefer.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NoteDetailActivity.this, "Notes added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NoteDetailActivity.this, "Notes adding unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void deletenotefromFirebase() {
        DocumentReference docrefer;
        docrefer = Utility.getCollectionReferenceForNotes().document(docId);
        docrefer.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NoteDetailActivity.this, "Note deletion successful", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NoteDetailActivity.this, "Note deletion unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
