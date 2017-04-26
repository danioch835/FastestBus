package com.example.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {

    private BusFinder busFinder;
    private ArrayList<Bus> nearestBuses;
    private Context context = this;
    private String startName;
    private String destinationName;
    private ConnectTask task = null;

    class ConnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("Background");
            try {
                Intent intent = getIntent();
                startName = intent.getStringExtra("startBusStop");
                destinationName = intent.getStringExtra("destinationBusStop");
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                busFinder.setProgressBar(progressBar);
                nearestBuses = busFinder.loadNearestBuses(startName, destinationName);
                System.out.println(nearestBuses.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            showDepartures();
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        if(task != null) {
            TextView loadText = (TextView) findViewById(R.id.textWczytywanie);
            loadText.setText("Wait...");
            showDepartures();
        } else {
            busFinder = new BusFinder();
            nearestBuses = new ArrayList<Bus>();
            task = new ConnectTask();
            task.execute();
        }
    }

    private void showDepartures() {
        TableLayout layout = (TableLayout) findViewById(R.id.table_layout);
        TextView loadText = (TextView) findViewById(R.id.textWczytywanie);
        loadText.setText("From: " + startName + " \nTo: " + destinationName);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        for (Bus bus : nearestBuses) {
            TableRow busNumberRow = new TableRow(context);
            TextView busNumber = new TextView(context);
            TableRow.LayoutParams tr2 = new TableRow.LayoutParams();
            tr2.span = 5;
            busNumber.setLayoutParams(tr2);
            Drawable header = getResources().getDrawable(R.drawable.headers, context.getTheme());
            busNumber.setBackground(header);
            busNumber.setText(bus.getNumber());
            busNumber.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            busNumber.setTextSize(16);
            busNumber.setTextColor(Color.WHITE);
            System.out.println(bus.getNumber());
            busNumberRow.addView(busNumber);

            ArrayList<String> busDepartures = bus.getDepartures();
            if(busDepartures.size() > 0) {
                TableRow dataRow = new TableRow(context);
                TableRow.LayoutParams tr1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                dataRow.setLayoutParams(tr1);

                for (String departure : busDepartures) {
                    TextView busDeparture = new TextView(context);
                    Drawable cell = getResources().getDrawable(R.drawable.cells, context.getTheme());
                    busDeparture.setBackground(cell);
                    TableRow.LayoutParams tr= new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    tr.setMargins(5, 5, 5, 45);
                    String processedTeparture = departure.replaceAll("\\s","");
                    busDeparture.setText(processedTeparture);
                    busDeparture.setTextSize(14);
                    busDeparture.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    busDeparture.setTextColor(Color.BLACK);
                    busDeparture.setPadding(5, 5, 5, 5);
                    busDeparture.setLayoutParams(tr);
                    //System.out.println(departure);
                    dataRow.addView(busDeparture);
                }
                layout.addView(busNumberRow);
                layout.addView(dataRow);
            }

        }
        /*Button backButton = new Button(context);
        backButton.setText("Back");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.activity_display_message);
        mainLayout.addView(backButton);*/
    }

}
