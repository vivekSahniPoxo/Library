package com.example.library;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracking_form extends AppCompatActivity {
    Spinner spinner;
    String[] value =new String[]{"Title","Author","Branch","Selected Rack"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_form);
        spinner=findViewById(R.id.spinner2);
        final List<String> plantsList = new ArrayList<>(Arrays.asList(value));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, plantsList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}