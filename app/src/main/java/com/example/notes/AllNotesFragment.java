package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AllNotesFragment extends Fragment {

    private NoteDatabaseHelper noteDatabaseHelper;
    private String currentUserEmail;
    private ListView notesListView;
    private SearchView searchView;
    private Spinner tagSpinner;
    private String currentQuery = "";
    private String currentTag = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_notes, container, false);

        notesListView = view.findViewById(R.id.listView_all_notes);
        searchView = view.findViewById(R.id.searchView_notes);
        tagSpinner = view.findViewById(R.id.spinner_tags);
        TextView emptyTextView = view.findViewById(R.id.textView_all_notes_empty);
        Button addNoteButton = view.findViewById(R.id.button_add_note);

        notesListView.setEmptyView(emptyTextView);

        noteDatabaseHelper = new NoteDatabaseHelper(requireContext());
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");
        currentTag = getString(R.string.all_tags);

        setupSearch();
        setupTagSpinner();
        addNoteButton.setOnClickListener(view1 -> startActivity(new Intent(requireContext(), AddEditNoteActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTags();
        loadNotes();
    }

    private void setupSearch() {
        searchView.setQueryHint(getString(R.string.search_notes));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query.trim();
                loadNotes();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText.trim();
                loadNotes();
                return true;
            }
        });
    }

    private void setupTagSpinner() {
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTag = parent.getItemAtPosition(position).toString();
                loadNotes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadTags() {
        ArrayList<String> tags = noteDatabaseHelper.getAllTags(currentUserEmail);
        tags.add(0, getString(R.string.all_tags));

        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tags);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);

        int selectedPosition = tags.indexOf(currentTag);

        if (selectedPosition < 0)
            selectedPosition = 0;

        tagSpinner.setSelection(selectedPosition);
    }

    private void loadNotes() {
        if (!isAdded())
            return;

        ArrayList<Note> notes;

        if (currentQuery.isEmpty() && currentTag.equals(getString(R.string.all_tags)))
            notes = noteDatabaseHelper.getAllNotes(currentUserEmail);
        else if (currentQuery.isEmpty())
            notes = noteDatabaseHelper.getNotesByTag(currentUserEmail, currentTag);
        else {
            notes = noteDatabaseHelper.searchNotes(currentUserEmail, currentQuery);

            if (!currentTag.equals(getString(R.string.all_tags))) {
                for (int i = notes.size() - 1; i >= 0; i--)
                    if (notes.get(i).getTag() == null || !notes.get(i).getTag().equalsIgnoreCase(currentTag))
                        notes.remove(i);
            }
        }

        setNotesAdapter(notes);
    }

    private void setNotesAdapter(ArrayList<Note> notes) {
        NoteAdapter noteAdapter = new NoteAdapter(requireContext(), notes, new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onNoteClick(Note note) {
                openNoteDetails(note);
            }

            @Override
            public boolean onFavoriteClick(Note note, boolean newFavoriteValue) {
                boolean updated = noteDatabaseHelper.setFavorite(note.getId(), currentUserEmail, newFavoriteValue);

                if (!updated)
                    Toast.makeText(requireContext(), R.string.favorite_update_failed, Toast.LENGTH_SHORT).show();

                return updated;
            }
        });

        notesListView.setAdapter(noteAdapter);
    }

    private void openNoteDetails(Note note) {
        Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
        intent.putExtra("NOTE_ID", note.getId());
        startActivity(intent);
    }
}