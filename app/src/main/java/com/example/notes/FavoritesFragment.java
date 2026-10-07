package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private NoteDatabaseHelper noteDatabaseHelper;
    private String currentUserEmail;
    private ListView notesListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        notesListView = view.findViewById(R.id.listView_favorites);
        TextView emptyTextView = view.findViewById(R.id.textView_favorites_empty);
        notesListView.setEmptyView(emptyTextView);

        noteDatabaseHelper = new NoteDatabaseHelper(requireContext());
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        ArrayList<Note> notes = noteDatabaseHelper.getFavoriteNotes(currentUserEmail);

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

                if (updated)
                    notesListView.post(FavoritesFragment.this::loadNotes);
                else
                    Toast.makeText(requireContext(), R.string.favorite_update_failed, Toast.LENGTH_SHORT).show();

                return updated;
            }
        });

        notesListView.setAdapter(noteAdapter);
    }
}