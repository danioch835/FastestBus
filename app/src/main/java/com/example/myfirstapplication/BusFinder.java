package com.example.myfirstapplication;

import android.widget.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BusFinder {

    private ProgressBar progressBar;
    private HashMap<String, String> busStopsIDs;
    private final String basePageURL = "http://www.ztz.rybnik.pl/rj/index.php";

    public BusFinder() {
        busStopsIDs = new HashMap<String, String>();
        busStopsIDs.put("Maroko Nowiny", "110");
        busStopsIDs.put("Policja", "259");
        busStopsIDs.put("Chwałowice Kopalnia", "185");
        busStopsIDs.put("Plac Wolności", "135");
        busStopsIDs.put("Dworzec Autobusowy", "58");
        busStopsIDs.put("Rybnicka Kuźnia Maksymiliana", "74");
        busStopsIDs.put("Sąd", "112");
        busStopsIDs.put("Meksyk Kamyczek", "187");
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public String[] getAvailableBusStopsNames() {
        String[] stopsNames = busStopsIDs.keySet().toArray(new String[busStopsIDs.size()]);
        return stopsNames;
    }

    public ArrayList<Bus> loadNearestBuses(String startBusStop, String destinationBusStop) throws IOException {

        ArrayList<Bus> selectedBuses = loadBuses(startBusStop, destinationBusStop);

        double progressValue = 50.0/selectedBuses.size();
        double actualProgress = progressBar.getProgress();

        for (Bus bus : selectedBuses) {

            Document page = Jsoup.connect(basePageURL).data("id", "przystanek", "prz_id", busStopsIDs.get(startBusStop), "tab_id", bus.getId()).get();

            Element plate = page.select("table.odjazdy").first();

            Elements rows = plate.select("tr");
            Element hours = rows.get(1);
            Elements columns = hours.select("td");
            Element workingDays = columns.get(0);
            Element saturdays = columns.get(1);
            Element sundays = columns.get(2);

            Calendar calendar = new GregorianCalendar();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Elements odjazdy;

            switch (dayOfWeek) {
                case Calendar.SATURDAY:
                    odjazdy = saturdays.select(".odj b");
                    break;
                case Calendar.SUNDAY:
                    odjazdy = sundays.select(".odj b");
                    break;
                default:
                    odjazdy = workingDays.select(".odj b");
                    break;
            }

            List<Element> nearestDepartures = odjazdy.subList(0, Math.min(5, odjazdy.size()));

            for (Element departureElement : nearestDepartures) {
                String departure = departureElement.text();
                bus.addDeparture(departure);
            }

            actualProgress += progressValue;
            progressBar.setProgress((int) actualProgress);

        }
        return selectedBuses;
    }

    private ArrayList<Bus> loadBuses(String startBusStopName, String destinationBusStopName) throws IOException {

        ArrayList<Bus> selectedBuses = new ArrayList<Bus>();
        String startBusStopID = busStopsIDs.get(startBusStopName);
        //load html page for selected bus stop
        Document busStopPage = Jsoup.connect(basePageURL).data("id", "przystanek", "prz_id", startBusStopID).get();
        // load buses driving from start bus stop
        Elements busesList = busStopPage.select("[name=tab_id]").first().select("option");

        double progressValue = 50.0/busesList.size();
        double actualProgress = progressBar.getProgress();

        for (Element bus : busesList) {
            String tab_id = bus.attr("value");
            Document busPage = Jsoup.connect(basePageURL).data("id", "przystanek", "prz_id", startBusStopID, "tab_id", tab_id).get();

            Element busRouteTable = busPage.select(".tabliczka > tbody > tr").get(1).select("td table").get(1);
            Elements busStopsList = busRouteTable.select("tr");

            boolean startBusStopFound = false;
            boolean isBusWanted = false;

            for (Element busStop : busStopsList) {
                String busStopName = busStop.select("a").text();
                if (busStopName.contains(startBusStopName)) {
                    if(busStop.select("td").get(1).className().equals("invert")) {
                        startBusStopFound = true;
                    }
                } else if (startBusStopFound && busStopName.contains(destinationBusStopName)) {
                    isBusWanted = true;
                }

            }
            if (isBusWanted) {
                String optionText = bus.text();
                String[] nameParts = optionText.split("\\s*-\\s*");
                String number = nameParts[0];
                Bus wantedBus = new Bus(number, tab_id);
                selectedBuses.add(wantedBus);
            }

            actualProgress += progressValue;
            progressBar.setProgress((int) actualProgress);

        }

        return selectedBuses;
    }
}
