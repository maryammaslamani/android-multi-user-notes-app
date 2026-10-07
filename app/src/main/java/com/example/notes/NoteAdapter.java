package com.example.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {

    public interface OnNoteActionListener {
        void onNoteClick(Note note);
        boolean onFavoriteClick(Note note, boolean newFavoriteValue);
    }

    private final OnNoteActionListener listener;

    public NoteAdapter(@NonNull Context context, ArrayList<Note> notes, OnNoteActionListener listener) {
        super(context, 0, notes);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);

            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.textView_note_title);
            holder.favoriteButton = convertView.findViewById(R.id.button_note_favorite);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        Note note = getItem(position);

        if (note == null)
            return convertView;

        holder.titleTextView.setText(note.getTitle());
        setFavoriteIcon(holder.favoriteButton, note.isFavorite());

        convertView.setOnClickListener(view -> listener.onNoteClick(note));

        holder.favoriteButton.setOnClickListener(view -> {
            boolean newFavoriteValue = !note.isFavorite();

            if (listener.onFavoriteClick(note, newFavoriteValue)) {
                note.setFavorite(newFavoriteValue);
                setFavoriteIcon(holder.favoriteButton, newFavoriteValue);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.favorite_scale);
                holder.favoriteButton.startAnimation(animation);
            }
        });

        return convertView;
    }

    private void setFavoriteIcon(ImageButton favoriteButton, boolean favorite) {
        if (favorite)
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        else
            favoriteButton.setImageResource(R.drawable.ic_favorite_outline);
    }

    private static class ViewHolder {
        TextView titleTextView;
        ImageButton favoriteButton;
    }
}