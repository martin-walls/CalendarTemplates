package com.martinwalls.calendartemplates;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private ArrayList<String> template;
    private long calID = 5;
    private long eventID = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.template = getArguments().getStringArrayList("template");
        // Use the current date as the default
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String startTime = template.get(3);
        int startHour = Integer.parseInt(startTime.substring(0, 2));
        int startMin = Integer.parseInt(startTime.substring(2));
        String endTime = template.get(4);
        int endHour = Integer.parseInt(endTime.substring(0, 2));
        int endMin = Integer.parseInt(endTime.substring(2));
        Calendar eventBeginTime = Calendar.getInstance();
        eventBeginTime.set(year, month, day, startHour, startMin);
        Calendar eventEndTime = Calendar.getInstance();
        eventEndTime.set(year, month, day, endHour, endMin);

        createEvent(eventBeginTime, eventEndTime);

        /* CODE FOR ADDING EVENT THROUGH DEFAULT CALENDAR APP
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventBeginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.ALL_DAY, false)
                .putExtra(CalendarContract.Events.TITLE, templateName)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        startActivity(calendarIntent);
        */
    }

    public void createEvent(Calendar eventBeginTime, Calendar eventEndTime) {
        String templateName = template.get(0);
        String location = template.get(1);
        String description = template.get(2);

        // Insert event into calendar
        ContentResolver cr = Objects.requireNonNull(getActivity()).getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.TITLE, templateName);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.DTSTART, eventBeginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, eventEndTime.getTimeInMillis());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");
        values.put(CalendarContract.Events.EVENT_COLOR_KEY, 5);
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.getContext(), "Calendar permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri createUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        Snackbar.make(getActivity().findViewById(R.id.parent_linear_layout), "Event created", Snackbar.LENGTH_LONG)
                .setAction("Undo", undoListener)
                .setActionTextColor(getResources().getColor(R.color.colorAccent))
                .show();

        assert createUri != null;

        // Get event ID so it can be deleted / reminders added etc
        this.eventID = Long.parseLong(createUri.getLastPathSegment());

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