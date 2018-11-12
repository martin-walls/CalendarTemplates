package com.martinwalls.calendartemplates;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private LinearLayout parentLinearLayout;
    public static final String TEMPLATE_NAME = "com.martinwalls.calendartemplates.TEMPLATE_NAME";
    public static final int PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;

    public static final long calID = 5;

    long eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLinearLayout = findViewById(R.id.parent_linear_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTemplateIntent = new Intent(getApplicationContext(), NewTemplateActivity.class);
                startActivity(newTemplateIntent);
            }
        });

//        FileIO.writeFile(this, "", false);

        showTemplates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Shows the menu buttons in the toolbar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_calendar:
                // Open calendar (schedule) at current time
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                // System.currentTimeMillis() returns ms since epoch
                ContentUris.appendId(builder, System.currentTimeMillis());
                Intent calendarIntent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(calendarIntent);
                return true;

            default:
                // User's action not recognised
                return super.onOptionsItemSelected(item);
        }
    }

    public void addTemplateEventOnClick(View view) {
        // Check for calendar permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        TextView templateNameView = (TextView) parent.getChildAt(0);
        String templateName = (String) templateNameView.getText();
        // Get template info
        final ArrayList<String> template = FileIO.getTemplateInfo(this, templateName);

        final Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                String startTime = template.get(3);
                int startHour = Integer.parseInt(startTime.substring(0, 2));
                int startMin = Integer.parseInt(startTime.substring(3));
                String endTime = template.get(4);
                int endHour = Integer.parseInt(endTime.substring(0, 2));
                int endMin = Integer.parseInt(endTime.substring(3));
                Calendar eventBeginTime = Calendar.getInstance();
                eventBeginTime.set(year, month, day, startHour, startMin);
                Calendar eventEndTime = Calendar.getInstance();
                eventEndTime.set(year, month, day, endHour, endMin);


                createEvent(template, eventBeginTime, eventEndTime);

            }
        }, curYear, curMonth, curDay);
        datePicker.setTitle("Select Date");
        datePicker.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_CALENDAR:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    public void editOnClick(View view) {
        // Get template name
        TextView templateNameText = view.findViewById(R.id.template_name);
        String templateName = (String) templateNameText.getText();

        Intent editIntent = new Intent(this, EditActivity.class);
        editIntent.putExtra(TEMPLATE_NAME, templateName);
        startActivity(editIntent);
    }

    private void showTemplates() {
        ArrayList<ArrayList<String>> templatesList = FileIO.getTemplates(this);
        if (templatesList.size() > 0) {
            for (ArrayList<String> template : templatesList) {
                if (template.isEmpty()) {
                    continue;
                }
                // get the name of the template
                String templateName = template.get(0);
                // add a layout for the template
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert inflater != null;
                @SuppressLint("InflateParams") View rowView = inflater.inflate(R.layout.template_field, null);
                TextView nameField = rowView.findViewById(R.id.template_name);
                nameField.setText(templateName);
                parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
            }
            parentLinearLayout.removeView(findViewById(R.id.no_templates));
        }
    }

    public void createEvent(ArrayList<String> template, Calendar eventBeginTime, Calendar eventEndTime) {
        String templateName = template.get(0);
        String location = template.get(1);
        String description = template.get(2);
        String color = template.get(5);
        int colorID;
        if (color.isEmpty()) {
            colorID = 6;
        } else {
            colorID = Integer.parseInt(color);
        }

        // Insert event into calendar
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.TITLE, templateName);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.DTSTART, eventBeginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, eventEndTime.getTimeInMillis());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");
        values.put(CalendarContract.Events.EVENT_COLOR_KEY, colorID);
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri createUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Snackbar.make(findViewById(R.id.parent_linear_layout), "Event created", Snackbar.LENGTH_LONG)
                .setAction("Undo", undoListener)
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();

        assert createUri != null;

        // Get event ID so it can be deleted / reminders added etc
        this.eventID = Long.parseLong(Objects.requireNonNull(createUri.getLastPathSegment()));

        // Add reminder for event
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 30);
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    final View.OnClickListener undoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Delete the event just created
            View rootView = view.getRootView();
            ContentResolver cr = rootView.getContext().getContentResolver();
            Uri deleteUri;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            int rows = cr.delete(deleteUri, null, null);
            Log.i("CalendarTemplates", "Rows deleted: " + rows);
        }
    };
}





