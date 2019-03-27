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

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    private String templateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        templateName = intent.getStringExtra(MainActivity.TEMPLATE_NAME);
        getSupportActionBar().setTitle(templateName);

        fillTemplateValues(templateName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_template, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                updateTemplateFile();
                Toast.makeText(this, "Template updated", Toast.LENGTH_SHORT).show();
                Intent doneReturnIntent = new Intent(this, MainActivity.class);
                startActivity(doneReturnIntent);
                return true;
            case R.id.action_delete:
                confirmDeleteDialog();
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

        DBHandler dbHandler = new DBHandler(this);
        //remove ArrayList<String> template = FileIO.getTemplateInfo(this, getSupportActionBar().getTitle().toString());
        Template template = dbHandler.getTemplate(templateName);


        if (!(name.equals(template.getName())
                && location.equals(template.getLocation())
                && description.equals(template.getDescription())
                && startTime.equals(template.getStartTime())
                && endTime.equals(template.getEndTime())
                && color.equals(template.getColour().getColourId()))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to discard your edits?")
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

    public void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this template?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTemplateFile();
                        Toast.makeText(getApplicationContext(), "Template deleted", Toast.LENGTH_SHORT).show();
                        Intent deleteReturnIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(deleteReturnIntent);
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
    }

    public void updateTemplateFile() {
        Template updatedTemplate = new Template();

        // Get values in text edits
        EditText nameField = findViewById(R.id.name_field);
        updatedTemplate.setName(nameField.getText().toString());

        EditText locationField = findViewById(R.id.location_field);
        updatedTemplate.setLocation(locationField.getText().toString());

        EditText descriptionField = findViewById(R.id.description_field);
        updatedTemplate.setDescription(descriptionField.getText().toString());

        EditText startTimeField = findViewById(R.id.start_time_field);
        updatedTemplate.setStartTime(Template.parseTime(startTimeField.getText().toString()));

        EditText endTimeField = findViewById(R.id.end_time_field);
        updatedTemplate.setEndTime(Template.parseTime(endTimeField.getText().toString()));

        EditText colorField = findViewById(R.id.color_field);
        String colourString = colorField.getText().toString();
        for (Colour colour : Colour.values()) {
            if (colour.name().equals(colourString)) {
                updatedTemplate.setColour(colour);
            }
        }//TODO show colour in input box as name not number

        //remove FileIO.updateTemplate(this, templateToUpdate, name, location, description, startTime, endTime, color);
        DBHandler dbHandler = new DBHandler(this);
        dbHandler.updateTemplate(updatedTemplate);
    }

    public void deleteTemplateFile() {
        String templateToDelete = getSupportActionBar().getTitle().toString();
        FileIO.deleteTemplate(this, templateToDelete);
    }

    public void fillTemplateValues(String templateName) {
        // Fill in the edit fields with current template values
        //remove ArrayList<String> template = FileIO.getTemplateInfo(this, templateName);
        DBHandler dbHandler = new DBHandler(this);
        Template template = dbHandler.getTemplate(templateName);

        EditText name = findViewById(R.id.name_field);
        name.setText(templateName);

        EditText location = findViewById(R.id.location_field);
        location.setText(template.getLocation());

        EditText description = findViewById(R.id.description_field);
        description.setText(template.getDescription());

        EditText startTime = findViewById(R.id.start_time_field);
        startTime.setText(template.getStartTime().getTimeString());

        EditText endTime = findViewById(R.id.end_time_field);
        endTime.setText(template.getEndTime().getTimeString());

        EditText color = findViewById(R.id.color_field);
        color.setText(template.getColour().name());
    }

    public void colorOnClick(View view) {
        final EditText editText = (EditText) view;

        //remove final String[] colors = {"Lavendar", "Sage", "Grape", "Flamingo", "Banana", "Tangerine",
        //remove         "Peacock", "Graphite", "Blueberry", "Basil", "Tomato"};
        //remove final String[] colorIDs = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
        Colour[] colours = Colour.values();
        final String[] names = new String[colours.length];
        for (int i = 0; i < colours.length; i++) {
            names[i] = colours[i].name();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText.setText(names[which]);
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
