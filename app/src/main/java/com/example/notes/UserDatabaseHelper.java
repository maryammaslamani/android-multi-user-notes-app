package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    public UserDatabaseHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USER(EMAIL TEXT PRIMARY KEY,FIRST_NAME TEXT, LAST_NAME TEXT,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", user.getEmail());
        contentValues.put("FIRST_NAME", user.getFirstName());
        contentValues.put("LAST_NAME", user.getLastName());
        contentValues.put("PASSWORD", user.getPassword());
        sqLiteDatabase.insert("USER", null, contentValues);
    }

    public boolean emailExists(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM USER " + "WHERE EMAIL = ? AND PASSWORD = ?",
                new String[]{email, password});
        boolean authenticated = cursor.moveToFirst();
        cursor.close();
        return authenticated;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM USER WHERE EMAIL = ?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow("EMAIL"));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("PASSWORD"));
            user = new User(userEmail, firstName, lastName, password);
        }
        cursor.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FIRST_NAME", user.getFirstName());
        contentValues.put("LAST_NAME", user.getLastName());
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            contentValues.put("PASSWORD", user.getPassword());
        int affectedRows = sqLiteDatabase.update("USER", contentValues, "EMAIL = ?",
                new String[]{user.getEmail()});
        return affectedRows > 0;
    }
}