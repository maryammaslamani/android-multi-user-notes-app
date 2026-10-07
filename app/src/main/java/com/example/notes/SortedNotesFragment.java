package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SortedNotesFragment extends Fragment {

    private static final String SORT_ORDER_KEY = "SORT_ORDER";

    private NoteDatabaseHelper noteDatabaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String currentUserEmail;
    private String currentSortingMethod;
    private ListView notesListView;
    private Spinner sortingSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sorted_notes, container, false);

        notesListView = view.findViewById(R.id.listView_sorted_notes);
        sortingSpinner = view.findViewById(R.id.spinner_sorting);
        TextView emptyTextView = view.findViewById(R.id.textView_sorted_empty);
        notesListView.setEmptyView(emptyTextView);

        noteDatabaseHelper = new NoteDatabaseHelper(requireContext());
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");
        currentSortingMethod = sharedPrefManager.readString(SORT_ORDER_KEY, NoteDatabaseHelper.SORT_DATE);

        setupSortingSpinner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void setupSortingSpinner() {
        String[] options = {getString(R.string.sort_by_date), getString(R.string.sort_alphabetically)};

        ArrayAdapter<String> sortingAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        sortingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(sortingAdapter);

        if (NoteDatabaseHelper.SORT_ALPHABETICAL.equals(currentSortingMethod))
            sortingSpinner.setSelection(1);
        else
            sortingSpinner.setSelection(0);

        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortingMethod = position == 1 ? NoteDatabaseHelper.SORT_ALPHABETICAL : NoteDatabaseHelper.SORT_DATE;
                sharedPrefManager.writeString(SORT_ORDER_KEY, currentSortingMethod);
                loadNotes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadNotes() {
        ArrayList<Note> notes = noteDatabaseHelper.getNotesSorted(currentUserEmail, currentSortingMethod);

        NoteAdapter noteAdapter = new NoteAdapter(requireContext(), notes, new NoteAdapter.OnNoteActionListener() {
            @Override
            public void onNoteClick(Note note) {
                Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
                intent.putExtra("NOTE_ID", note.getId());
                startActivity(intent);
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
}