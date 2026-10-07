package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotesDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NOTES = "NOTES";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_OWNER_EMAIL = "OWNER_EMAIL";
    private static final String COLUMN_TITLE = "TITLE";
    private static final String COLUMN_CONTENT = "CONTENT";
    private static final String COLUMN_CREATION_DATE = "CREATION_DATE";
    private static final String COLUMN_IS_FAVORITE = "IS_FAVORITE";
    private static final String COLUMN_TAG = "TAG";

    public static final String SORT_ALPHABETICAL = "alphabetical";
    public static final String SORT_DATE = "date";

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OWNER_EMAIL + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT NOT NULL, " +
                COLUMN_CREATION_DATE + " INTEGER NOT NULL, " +
                COLUMN_IS_FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_TAG + " TEXT)";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);
    }

    private Note cursorRowToNote(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String ownerEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OWNER_EMAIL));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
        long creationDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
        boolean favorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1;
        String tag = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG));

        return new Note(id, ownerEmail, title, content, creationDate, favorite, tag);
    }

    private ArrayList<Note> cursorToNoteList(Cursor cursor) {
        ArrayList<Note> notes = new ArrayList<>();

        try {
            while (cursor.moveToNext())
                notes.add(cursorRowToNote(cursor));
        } finally {
            cursor.close();
        }

        return notes;
    }

    public long addNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_OWNER_EMAIL, note.getOwnerEmail());
        contentValues.put(COLUMN_TITLE, note.getTitle());
        contentValues.put(COLUMN_CONTENT, note.getContent());
        contentValues.put(COLUMN_CREATION_DATE, note.getCreationDate());
        contentValues.put(COLUMN_IS_FAVORITE, note.isFavorite() ? 1 : 0);
        contentValues.put(COLUMN_TAG, note.getTag());

        return sqLiteDatabase.insert(TABLE_NOTES, null, contentValues);
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, note.getTitle());
        contentValues.put(COLUMN_CONTENT, note.getContent());
        contentValues.put(COLUMN_TAG, note.getTag());

        int rowsUpdated = sqLiteDatabase.update(TABLE_NOTES, contentValues,
                COLUMN_ID + " = ? AND " + COLUMN_OWNER_EMAIL + " = ?",
                new String[]{String.valueOf(note.getId()), note.getOwnerEmail()});

        return rowsUpdated > 0;
    }

    public boolean deleteNote(int noteId, String ownerEmail) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        int rowsDeleted = sqLiteDatabase.delete(TABLE_NOTES,
                COLUMN_ID + " = ? AND " + COLUMN_OWNER_EMAIL + " = ?",
                new String[]{String.valueOf(noteId), ownerEmail});

        return rowsDeleted > 0;
    }

    public Note getNoteById(int noteId, String ownerEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_ID + " = ? AND " + COLUMN_OWNER_EMAIL + " = ?",
                new String[]{String.valueOf(noteId), ownerEmail});

        Note note = null;

        try {
            if (cursor.moveToFirst())
                note = cursorRowToNote(cursor);
        } finally {
            cursor.close();
        }

        return note;
    }

    public ArrayList<Note> getAllNotes(String ownerEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_OWNER_EMAIL + " = ? ORDER BY " + COLUMN_CREATION_DATE + " DESC",
                new String[]{ownerEmail});

        return cursorToNoteList(cursor);
    }

    public ArrayList<Note> getFavoriteNotes(String ownerEmail) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_OWNER_EMAIL + " = ? AND " +
                        COLUMN_IS_FAVORITE + " = 1 ORDER BY " + COLUMN_CREATION_DATE + " DESC",
                new String[]{ownerEmail});

        return cursorToNoteList(cursor);
    }

    public ArrayList<Note> searchNotes(String ownerEmail, String query) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        if (query == null)
            query = "";

        String likePattern = "%" + query.trim() + "%";

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_OWNER_EMAIL + " = ? AND (" +
                        COLUMN_TITLE + " LIKE ? OR " + COLUMN_CONTENT + " LIKE ?) ORDER BY " +
                        COLUMN_CREATION_DATE + " DESC",
                new String[]{ownerEmail, likePattern, likePattern});

        return cursorToNoteList(cursor);
    }

    public ArrayList<Note> getNotesSorted(String ownerEmail, String sortingMethod) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String orderByClause = SORT_ALPHABETICAL.equals(sortingMethod)
                ? COLUMN_TITLE + " COLLATE NOCASE ASC"
                : COLUMN_CREATION_DATE + " DESC";

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_OWNER_EMAIL + " = ? ORDER BY " + orderByClause,
                new String[]{ownerEmail});

        return cursorToNoteList(cursor);
    }

    public ArrayList<Note> getNotesByTag(String ownerEmail, String tag) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTES + " WHERE " + COLUMN_OWNER_EMAIL + " = ? AND " +
                        COLUMN_TAG + " = ? COLLATE NOCASE ORDER BY " + COLUMN_CREATION_DATE + " DESC",
                new String[]{ownerEmail, tag});

        return cursorToNoteList(cursor);
    }

    public ArrayList<String> getAllTags(String ownerEmail) {
        ArrayList<String> tags = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT DISTINCT " + COLUMN_TAG + " FROM " + TABLE_NOTES + " WHERE " +
                        COLUMN_OWNER_EMAIL + " = ? AND " + COLUMN_TAG + " IS NOT NULL AND " +
                        COLUMN_TAG + " != '' ORDER BY " + COLUMN_TAG + " COLLATE NOCASE ASC",
                new String[]{ownerEmail});

        try {
            while (cursor.moveToNext())
                tags.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG)));
        } finally {
            cursor.close();
        }

        return tags;
    }

    public boolean setFavorite(int noteId, String ownerEmail, boolean favorite) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_IS_FAVORITE, favorite ? 1 : 0);

        int rowsUpdated = sqLiteDatabase.update(TABLE_NOTES, contentValues,
                COLUMN_ID + " = ? AND " + COLUMN_OWNER_EMAIL + " = ?",
                new String[]{String.valueOf(noteId), ownerEmail});

        return rowsUpdated > 0;
    }
}