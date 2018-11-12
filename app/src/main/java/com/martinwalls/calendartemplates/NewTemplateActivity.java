package com.martinwalls.calendartemplates;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class NewTemplateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_template);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_template, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                if (createTemplateFile()) {
                    Toast.makeText(this, "Template created", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent(this, MainActivity.class);
                    startActivity(returnIntent);
                }
                return true;
            case android.R.id.home:
                confirmDiscardDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        confirmDiscardDialog();
    }

    public void confirmDiscardDialog() {
        // Check if all fields are empty, is so don't show dialog, just go back
        EditText nameField = findViewById(R.id.name_field);
        String name = nameField.getText().toString();
        EditText locationField = findViewById(R.id.location_field);
        String location = locationField.getText().toString();
        EditText descriptionField = findViewById(R.id.description_field);
        String description = descriptionField.getText().toString();
        EditText startTimeField = findViewById(R.id.start_time_field);
        String startTime = startTimeField.getText().toString();
        EditText endTimeField = findViewById(R.id.end_time_field);
        String endTime = endTimeField.getText().toString();
        EditText colorField = findViewById(R.id.color_field);
        String color = colorField.getText().toString();

        if (!(name.isEmpty() && location.isEmpty() && description.isEmpty() && startTime.isEmpty()
                && endTime.isEmpty() && color.isEmpty())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to discard this template?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    public boolean createTemplateFile() {
        EditText nameField = findViewById(R.id.name_field);
        String name = nameField.getText().toString();
        EditText locationField = findViewById(R.id.location_field);
        String location = locationField.getText().toString();
        EditText descriptionField = findViewById(R.id.description_field);
        String description = descriptionField.getText().toString();
        EditText startTimeField = findViewById(R.id.start_time_field);
        String startTime = startTimeField.getText().toString();
        EditText endTimeField = findViewById(R.id.end_time_field);
        String endTime = endTimeField.getText().toString();
        EditText colorField = findViewById(R.id.color_field);
        String color = colorField.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "You must enter a name", Toast.LENGTH_SHORT).show();
        } else if (startTime.isEmpty()) {
            Toast.makeText(this, "You must enter a start time.", Toast.LENGTH_SHORT).show();
        } else if (endTime.isEmpty()) {
            Toast.makeText(this, "You must enter an end time", Toast.LENGTH_SHORT).show();
        } else {
            FileIO.addNewTemplate(this, name, location, description, startTime, endTime, color);
            return true;
        }
        return false;
    }

    public void colorOnClick(View view) {
        final EditText editText = (EditText) view;

        final String[] colors = {"Lavender", "Sage", "Grape", "Flamingo", "Banana", "Tangerine",
                "Peacock", "Graphite", "Blueberry", "Basil", "Tomato"};
        final String[] colorIDs = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(colorIDs[which]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startTimeOnClick(View view) {
        final EditText editText = (EditText) view;

        // if a start time is already set, use that
        String curSetTime = editText.getText().toString();
        int hour;
        int min;
        if (curSetTime.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);
        } else {
            hour = Integer.parseInt(curSetTime.substring(0, 2));
            min = Integer.parseInt(curSetTime.substring(3));
        }

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String timeText = String.format("%02d", hour) + ":" + String.format("%02d", min);
                editText.setText(timeText);
            }
        }, hour, min, true); // Show 24hr time
        timePicker.setTitle("Select Time");
        timePicker.show();
    }

    public void endTimeOnClick(View view) {
        final EditText editText = (EditText) view;
        String curSetTime = editText.getText().toString();

        int hour;
        int min;
        if (curSetTime.isEmpty()) {
            // Get start time so end time is 1 hr after
            ViewGroup parent = (ViewGroup) editText.getParent();
            EditText startTimeField = parent.findViewById(R.id.start_time_field);
            String startTime = startTimeField.getText().toString();

            // If user has not yet entered start time, use current time
            if (startTime.isEmpty()) {
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                min = c.get(Calendar.MINUTE);
            } else {
                hour = (Integer.parseInt(startTime.substring(0, 2)) + 1) % 24;
                min = Integer.parseInt(startTime.substring(3));
            }
        } else {
            hour = Integer.parseInt(curSetTime.substring(0, 2));
            min = Integer.parseInt(curSetTime.substring(3));
        }

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String timeText = String.format("%02d", hour) + ":" + String.format("%02d", min);
                editText.setText(timeText);
            }
        }, hour, min, true); // Show 24hr time
        timePicker.setTitle("Select Time");
        timePicker.show();
    }
}
