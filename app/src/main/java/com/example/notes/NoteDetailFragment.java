package com.example.notes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDetailFragment extends Fragment {

    private static final String ARG_NOTE_ID = "NOTE_ID";

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private TextView tagTextView;
    private ImageButton favoriteButton;
    private NoteDatabaseHelper noteDatabaseHelper;
    private String currentUserEmail;
    private int noteId;
    private Note note;

    public static NoteDetailFragment newInstance(int noteId) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NOTE_ID, noteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        titleTextView = view.findViewById(R.id.textView_detail_title);
        contentTextView = view.findViewById(R.id.textView_detail_content);
        dateTextView = view.findViewById(R.id.textView_detail_date);
        tagTextView = view.findViewById(R.id.textView_detail_tag);
        favoriteButton = view.findViewById(R.id.button_detail_favorite);
        Button editButton = view.findViewById(R.id.button_edit_note);
        Button deleteButton = view.findViewById(R.id.button_delete_note);
        Button emailButton = view.findViewById(R.id.button_email_note);

        noteDatabaseHelper = new NoteDatabaseHelper(requireContext());

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        currentUserEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");
        noteId = getArguments() == null ? -1 : getArguments().getInt(ARG_NOTE_ID, -1);

        editButton.setOnClickListener(view1 -> editNote());
        deleteButton.setOnClickListener(view1 -> showDeleteConfirmation());
        emailButton.setOnClickListener(view1 -> sendNoteByEmail());
        favoriteButton.setOnClickListener(view1 -> changeFavoriteState());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNote();
    }

    private void loadNote() {
        if (noteId == -1 || currentUserEmail.isEmpty()) {
            requireActivity().finish();
            return;
        }

        note = noteDatabaseHelper.getNoteById(noteId, currentUserEmail);

        if (note == null) {
            Toast.makeText(requireContext(), R.string.note_not_found, Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return;
        }

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(note.getCreationDate()));
        dateTextView.setText(getString(R.string.created_value, formattedDate));

        String tag = note.getTag();

        if (tag == null || tag.isEmpty())
            tagTextView.setText(getString(R.string.tag_value, getString(R.string.no_tag)));
        else
            tagTextView.setText(getString(R.string.tag_value, tag));

        setFavoriteIcon();
    }

    private void setFavoriteIcon() {
        if (note.isFavorite())
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        else
            favoriteButton.setImageResource(R.drawable.ic_favorite_outline);
    }

    private void editNote() {
        if (note == null)
            return;

        Intent intent = new Intent(requireContext(), AddEditNoteActivity.class);
        intent.putExtra("NOTE_ID", note.getId());
        startActivity(intent);
    }

    private void showDeleteConfirmation() {
        if (note == null)
            return;

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_note)
                .setMessage(R.string.delete_note_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteNote())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteNote() {
        boolean deleted = noteDatabaseHelper.deleteNote(note.getId(), currentUserEmail);

        if (deleted) {
            Toast.makeText(requireContext(), R.string.note_deleted_successfully, Toast.LENGTH_SHORT).show();
            requireActivity().setResult(Activity.RESULT_OK);
            requireActivity().finish();
        } else
            Toast.makeText(requireContext(), R.string.note_delete_failed, Toast.LENGTH_SHORT).show();
    }

    private void sendNoteByEmail() {
        if (note == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, note.getContent());

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(requireContext(), R.string.no_email_application, Toast.LENGTH_SHORT).show();
        }
    }

    private void changeFavoriteState() {
        if (note == null)
            return;

        boolean newFavoriteValue = !note.isFavorite();
        boolean updated = noteDatabaseHelper.setFavorite(note.getId(), currentUserEmail, newFavoriteValue);

        if (updated) {
            note.setFavorite(newFavoriteValue);
            setFavoriteIcon();

            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.favorite_scale);
            favoriteButton.startAnimation(animation);
        } else
            Toast.makeText(requireContext(), R.string.favorite_update_failed, Toast.LENGTH_SHORT).show();
    }
}