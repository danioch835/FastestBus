package com.example.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SelectBusStopsActivity extends AppCompatActivity {

    private ArrayList<CheckBox> busStops ;
    private String fileName = "SelectedBusStops";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bus_stops);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.selectBusStopLayout);
        busStops = new ArrayList<CheckBox>();
        BusFinder busFinder = new BusFinder();
        String[] busStopsNames = busFinder.getAvailableBusStopsNames();

        for (String busStopName : busStopsNames) {
            CheckBox option = new CheckBox(this);
            option.setText(busStopName);
            busStops.add(option);
            linearLayout.addView(option);
        }
        Button saveButton = new Button(this);
        saveButton.setText("Save");
        linearLayout.addView(saveButton);
        saveButton.setOnClickListener(new ButtonListener());

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(fileName)));

            String line = "";
            ArrayList<String> selectedBuses = new ArrayList<String>();
            while((line = reader.readLine()) != null) {
                selectedBuses.add(line);
            }
            reader.close();
            for(CheckBox option : busStops) {
                String busStopName = option.getText().toString();
                if(selectedBuses.contains(busStopName)) {
                    option.setChecked(true);
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            ArrayList<String> busStopsNames = new ArrayList<String>();
            try {
                BufferedOutputStream writer = new BufferedOutputStream(openFileOutput(fileName, Context.MODE_PRIVATE));
                for(CheckBox busStop : busStops) {
                    if(busStop.isChecked()) {
                        busStopsNames.add(busStop.getText().toString());
                        String dataToWrite = busStop.getText().toString() + "\r\n";
                        writer.write(dataToWrite.getBytes());
                    }
                }
                writer.close();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.putExtra("selectedBusStops", busStopsNames.toArray(new String[busStopsNames.size()]));
                startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}