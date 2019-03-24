package com.martinwalls.calendartemplates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "calendarTemplates.db";

    public static final String LOG_TAG = DBHandler.class.getSimpleName();
    private final Context context;

    private static final String TABLE_TEMPLATES = "Templates";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_LOCATION = "Location";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_START = "StartTime";
    private static final String COLUMN_END = "EndTime";
    private static final String COLUMN_COLOUR = "Colour";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEMPLATES_TABLE = "CREATE TABLE " + TABLE_TEMPLATES + " (" +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE " +
                COLUMN_LOCATION + " TEXT " +
                COLUMN_DESCRIPTION + " TEXT " +
                COLUMN_START + " TEXT " +
                COLUMN_END + " TEXT " +
                COLUMN_COLOUR + " INTEGER )";
        db.execSQL(CREATE_TEMPLATES_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO onUpgrade
    }

    public void addNewTemplate(Template template) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, template.getName());
        values.put(COLUMN_LOCATION, template.getLocation());
        values.put(COLUMN_DESCRIPTION, template.getDescription());
        values.put(COLUMN_START, template.getStartTime());
        values.put(COLUMN_END, template.getEndTime());
        values.put(COLUMN_COLOUR, template.getColour());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TEMPLATES, null, values);
        db.close();
    }

    public void updateTemplate(Template template) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION, template.getLocation());
        values.put(COLUMN_DESCRIPTION, template.getDescription());
        values.put(COLUMN_START, template.getStartTime());
        values.put(COLUMN_END, template.getEndTime());
        values.put(COLUMN_COLOUR, template.getColour());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_TEMPLATES, values, COLUMN_NAME + "=?", new String[]{template.getName()});
        db.close();
    }

    public void deleteTemplate(String templateName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMPLATES, COLUMN_NAME + "=?", new String[]{templateName});
        db.close();
    }
}
