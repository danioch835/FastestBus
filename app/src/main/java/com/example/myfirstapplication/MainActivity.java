package com.example.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private BusFinder busFinder;
    private Spinner startBusStopName;
    private  Spinner destinationBusStopName;
    private String fileName = "SelectedBusStops";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busFinder = new BusFinder();
        startBusStopName = (Spinner) findViewById(R.id.startBusStopSpinner);
        destinationBusStopName = (Spinner) findViewById(R.id.destinationBusStopSpinner);

        String[] files = fileList();
        boolean fileExist  = false;
        for(String filesName : files) {
            if(filesName.equals(fileName)) {
                fileExist = true;
            }
        }

        if(fileExist) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(fileName)));

                String line = "";
                ArrayList<String> selectedBuses = new ArrayList<String>();
                while((line = reader.readLine()) != null) {
                    selectedBuses.add(line);
                }
                reader.close();
                if(selectedBuses.size() > 0) {
                    String[] busStopsNames = selectedBuses.toArray(new String[selectedBuses.size()]);
                    Arrays.sort(busStopsNames);
                    String[] destinationBusStopsNames = busStopsNames.clone();
                    ArrayAdapter<String> busStopNamesArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, busStopsNames);
                    ArrayAdapter<String> destinationBusStopNamesArray = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, destinationBusStopsNames);
                    //destinationBusStopNamesArray.remove(destinationBusStopNamesArray.getItem(0));
                    startBusStopName.setAdapter(busStopNamesArray);
                    destinationBusStopName.setAdapter(destinationBusStopNamesArray);
                } else {
                    Intent intent = new Intent(this, SelectBusStopsActivity.class);
                    startActivity(intent);
                }

            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }

        } else {
            Intent intent = new Intent(this, SelectBusStopsActivity.class);
            startActivity(intent);
        }
    }

    public void onClick(View view) {
        if(isNetworkAvailable()) {
            RadioGroup daysButtons = (RadioGroup) findViewById(R.id.daysButtons);
            int selectedDayId = daysButtons.getCheckedRadioButtonId();
            int selectedDay = 0;

            switch(selectedDayId) {
                case R.id.buttonWorkingDay:
                    selectedDay = Calendar.MONDAY;
                    break;
                case R.id.buttonStaurday:
                    selectedDay = Calendar.SATURDAY;
                    break;
                case R.id.buttonSunday:
                    selectedDay = Calendar.SUNDAY;
                    break;
                default:
                    break;
            }

            Intent intent = new Intent(this, DisplayMessageActivity.class);
            intent.putExtra("startBusStop", startBusStopName.getSelectedItem().toString());
            intent.putExtra("destinationBusStop", destinationBusStopName.getSelectedItem().toString());
            intent.putExtra("selectedDay", selectedDay);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Internet is unavailable!", Toast.LENGTH_LONG).show();
        }
    }

    public void onSelectBusStops(View view) {
        Intent intent = new Intent(this, SelectBusStopsActivity.class);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            result = true;
        }
        return result;
    }
}
