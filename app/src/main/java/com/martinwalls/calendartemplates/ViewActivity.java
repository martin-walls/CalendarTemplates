package com.martinwalls.calendartemplates;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {
    private LinearLayout viewLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        viewLinearLayout = findViewById(R.id.view_linear_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get template name and set title
        Intent intent = getIntent();
        String templateName = intent.getStringExtra(MainActivity.TEMPLATE_NAME);
        getSupportActionBar().setTitle(templateName);

        showTemplateInfo(templateName);
    }

    private void showTemplateInfo(String templateName) {
        ArrayList<String> template = FileIO.getTemplateInfo(this, templateName);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String values[] = {getResources().getString(R.string.location),
                getResources().getString(R.string.description),
                getResources().getString(R.string.start_time),
                getResources().getString(R.string.end_time),
                getResources().getString(R.string.color_id)};
        for (int i=0; i<values.length; i++) {
            View rowView = inflater.inflate(R.layout.template_view, null);
            TextView attrName = rowView.findViewById(R.id.text_location);
            attrName.setText(values[i]);
            TextView value = rowView.findViewById(R.id.template_value);
            value.setText(template.get(i+1));
            viewLinearLayout.addView(rowView, viewLinearLayout.getChildCount()-1);
        }
        viewLinearLayout.removeView(findViewById(R.id.no_attr));
    }
}
