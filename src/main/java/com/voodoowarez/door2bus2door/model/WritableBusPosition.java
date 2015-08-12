package com.voodoowarez.door2bus2door.model;

import java.util.Date;

public interface WritableBusPosition extends BusPosition {
	void setRouteId(String routeId);
	void setTripId(long tripId);
	void setVehicleId(int vehicleId);
	void setDate(Date date);
	void setDerivation(int derivation);
	void setDirection(Direction direction);
	void setLat(double lat);
	void setLog(double lon);
	void setTripHeadsign(String tripHeadsign);
	void setTripStartTime(Date tripStartTime);
	void setTripEndTime(Date tripEndTime);
}
