package com.example.notes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NoteDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_note_detail);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.note_details);

        int noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);

        if (noteId == -1) {
            Toast.makeText(this, R.string.note_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState == null) {
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(noteId);
            getSupportFragmentManager().beginTransaction().replace(R.id.note_detail_fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}