package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private EditText tagEditText;
    private NoteDatabaseHelper noteDatabaseHelper;
    private String currentUserEmail;
    private int noteId = -1;
    private Note noteToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");

        if (currentUserEmail.isEmpty()) {
            openLoginAndClearTask();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_add_edit_note);
        titleEditText = findViewById(R.id.editText_note_title);
        contentEditText = findViewById(R.id.editText_note_content);
        tagEditText = findViewById(R.id.editText_note_tag);
        Button saveButton = findViewById(R.id.button_save_note);
        Button cancelButton = findViewById(R.id.button_cancel_note);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteDatabaseHelper = new NoteDatabaseHelper(this);
        noteId = getIntent().getIntExtra("NOTE_ID", -1);

        if (noteId == -1)
            setTitle(R.string.add_note);
        else {
            setTitle(R.string.edit_note);
            loadNote();
        }

        saveButton.setOnClickListener(view -> saveNote());
        cancelButton.setOnClickListener(view -> finish());
    }

    private void loadNote() {
        noteToEdit = noteDatabaseHelper.getNoteById(noteId, currentUserEmail);

        if (noteToEdit == null) {
            Toast.makeText(this, R.string.note_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleEditText.setText(noteToEdit.getTitle());
        contentEditText.setText(noteToEdit.getContent());

        if (noteToEdit.getTag() != null)
            tagEditText.setText(noteToEdit.getTag());
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String tag = tagEditText.getText().toString().trim();

        titleEditText.setError(null);
        contentEditText.setError(null);

        if (title.isEmpty()) {
            titleEditText.setError(getString(R.string.title_required));
            titleEditText.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            contentEditText.setError(getString(R.string.content_required));
            contentEditText.requestFocus();
            return;
        }

        if (noteId == -1)
            addNewNote(title, content, tag);
        else
            updateExistingNote(title, content, tag);
    }

    private void addNewNote(String title, String content, String tag) {
        Note note = new Note(currentUserEmail, title, content, System.currentTimeMillis(), tag);
        long result = noteDatabaseHelper.addNote(note);

        if (result != -1) {
            Toast.makeText(this, R.string.note_added_successfully, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else
            Toast.makeText(this, R.string.note_save_failed, Toast.LENGTH_SHORT).show();
    }

    private void updateExistingNote(String title, String content, String tag) {
        noteToEdit.setTitle(title);
        noteToEdit.setContent(content);
        noteToEdit.setTag(tag);

        boolean updated = noteDatabaseHelper.updateNote(noteToEdit);

        if (updated) {
            Toast.makeText(this, R.string.note_updated_successfully, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else
            Toast.makeText(this, R.string.note_save_failed, Toast.LENGTH_SHORT).show();
    }

    private void openLoginAndClearTask() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}