package com.martinwalls.calendartemplates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
                COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_START + " TEXT, " +
                COLUMN_END + " TEXT, " +
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
        values.put(COLUMN_START, template.getStartTime().getTimeString());
        values.put(COLUMN_END, template.getEndTime().getTimeString());
        values.put(COLUMN_COLOUR, template.getColour().getColourId());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TEMPLATES, null, values);
        db.close();
    }

    public void updateTemplate(Template template) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOCATION, template.getLocation());
        values.put(COLUMN_DESCRIPTION, template.getDescription());
        values.put(COLUMN_START, template.getStartTime().getTimeString());
        values.put(COLUMN_END, template.getEndTime().getTimeString());
        values.put(COLUMN_COLOUR, template.getColour().getColourId());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_TEMPLATES, values, COLUMN_NAME + "=?", new String[]{template.getName()});
        db.close();
    }

    public void deleteTemplate(String templateName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEMPLATES, COLUMN_NAME + "=?", new String[]{templateName});
        db.close();
    }

    public Template getTemplate(String templateName) {
        Template result = new Template();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TEMPLATES +
                " WHERE " + COLUMN_NAME + " = '" + templateName + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            result.setName(templateName);
            result.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
            result.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            result.setStartTime(Template.parseTime(cursor.getString(cursor.getColumnIndex(COLUMN_START))));
            result.setEndTime(Template.parseTime(cursor.getString(cursor.getColumnIndex(COLUMN_END))));
            int colourInt = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOUR));
            for (Colour colour : Colour.values()) {
                if (colour.getColourId() == colourInt) {
                    result.setColour(colour);
                }
            }
        }
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<Template> getAllTemplates() {
        ArrayList<Template> result = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TEMPLATES;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Template template = new Template();
            template.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            template.setLocation(cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION)));
            template.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            template.setStartTime(Template.parseTime(cursor.getString(cursor.getColumnIndex(COLUMN_START))));
            template.setEndTime(Template.parseTime(cursor.getString(cursor.getColumnIndex(COLUMN_END))));
            int colourInt = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOUR));
            for (Colour colour : Colour.values()) {
                if (colour.getColourId() == colourInt) {
                    template.setColour(colour);
                }
            }
            result.add(template);
        }
        cursor.close();
        db.close();
        return result;
    }
}
