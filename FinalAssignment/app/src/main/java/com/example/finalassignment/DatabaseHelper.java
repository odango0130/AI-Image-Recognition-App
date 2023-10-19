package com.example.finalassignment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ImageDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_IMAGES = "images";
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE_PATH = "path";
    private static final String[] KEY_TAGS = {"tag1", "tag2", "tag3", "tag4", "tag5"};
    private static final String[] KEY_ACCURACIES = {"accuracy1", "accuracy2", "accuracy3", "accuracy4", "accuracy5"};


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGES_TABLE = "CREATE TABLE " + TABLE_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_IMAGE_PATH + " TEXT,";
        for(int i=0; i<5; i++) {
            CREATE_IMAGES_TABLE += KEY_TAGS[i] + " TEXT,"
                    + KEY_ACCURACIES[i] + " REAL,";
        }
        CREATE_IMAGES_TABLE = CREATE_IMAGES_TABLE.substring(0, CREATE_IMAGES_TABLE.length()-1) + ")";
        db.execSQL(CREATE_IMAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public void addImagePath(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_PATH, path);
        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }

    public void addImage(String path, String[] tags, float[] accuracies) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_PATH, path);
        for(int i=0; i<5; i++) {
            values.put(KEY_TAGS[i], tags[i]);
            values.put(KEY_ACCURACIES[i], accuracies[i]);
        }
        db.insert(TABLE_IMAGES, null, values);
        db.close();
    }


    @SuppressLint("Range")
    public List<String> getAllImagePaths() {
        List<String> paths = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                paths.add(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return paths;
    }

    // This is a new method in your DatabaseHelper class.
    public HashMap<String, String[]> getAllImageTags() {
        HashMap<String, String[]> imageTags = new HashMap<>();

        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String[] tags = new String[5];
                for (int i = 0; i < 5; i++) {
                    tags[i] = cursor.getString(cursor.getColumnIndex(KEY_TAGS[i]));
                }
                imageTags.put(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)), tags);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return imageTags;
    }

    public HashMap<String, float[]> getAllImageAccuracies() {
        HashMap<String, float[]> imageAccuracies = new HashMap<>();

        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                float[] accuracies = new float[5];
                for (int i = 0; i < 5; i++) {
                    accuracies[i] = cursor.getFloat(cursor.getColumnIndex(KEY_ACCURACIES[i]));
                }
                imageAccuracies.put(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)), accuracies);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return imageAccuracies;
    }

    public List<String> getImagesByTag(String tag) {
        List<String> paths = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 0; i < 5; i++) {
            String selectQuery = "SELECT " + KEY_IMAGE_PATH + " FROM " + TABLE_IMAGES + " WHERE " + KEY_TAGS[i] + " LIKE ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{ "%" + tag + "%" });

            if (cursor.moveToFirst()) {
                do {
                    paths.add(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH)));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        db.close();

        return paths;
    }

    public HashMap<String, String[]> getTagsForImages(List<String> imagePaths) {
        HashMap<String, String[]> imageTags = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        for (String path : imagePaths) {
            String[] tags = new String[5];

            Cursor cursor = db.query(TABLE_IMAGES, KEY_TAGS, KEY_IMAGE_PATH + "=?", new String[]{path}, null, null, null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < 5; i++) {
                    tags[i] = cursor.getString(cursor.getColumnIndex(KEY_TAGS[i]));
                }
                imageTags.put(path, tags);
            }

            cursor.close();
        }
        db.close();

        return imageTags;
    }

    public HashMap<String, float[]> getAccuraciesForImages(List<String> imagePaths) {
        HashMap<String, float[]> imageAccuracies = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        for (String path : imagePaths) {
            float[] accuracies = new float[5];

            Cursor cursor = db.query(TABLE_IMAGES, KEY_ACCURACIES, KEY_IMAGE_PATH + "=?", new String[]{path}, null, null, null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < 5; i++) {
                    accuracies[i] = cursor.getFloat(cursor.getColumnIndex(KEY_ACCURACIES[i]));
                }
                imageAccuracies.put(path, accuracies);
            }

            cursor.close();
        }
        db.close();

        return imageAccuracies;
    }



}