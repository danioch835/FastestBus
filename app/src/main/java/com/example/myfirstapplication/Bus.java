package com.example.myfirstapplication;

import java.util.ArrayList;

public class Bus {
	private String number;
	private String id;
	private ArrayList<String> departures;
	
	public Bus(String number, String id) {
		this.number = number;
		this.id = id;
		departures = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}
	
	public String getNumber () {
		return number;
	}
	
	public void addDeparture(String departure) {
		departures.add(departure);
	}
	
	public ArrayList<String> getDepartures() {
		return departures;
	}
	
}
