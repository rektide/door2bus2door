package com.voodoowarez.door2bus2door.model;

import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface BusPosition {
	String getRouteId();
	long getTripId();
	int getVehicleId();
	Date getDate();
	int getDerivation();
	Direction getDirection();
	double getLat();
	double getLon();
	String getTripHeadsign();
	Date getTripStartTime();
	Date getTripEndTime();
}
