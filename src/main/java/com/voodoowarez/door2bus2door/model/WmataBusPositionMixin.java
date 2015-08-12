package com.voodoowarez.door2bus2door.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

abstract public class WmataBusPositionMixin {

//	"DateTime": "2014-10-27T13:23:19",
//	"Deviation": -1,
//	"DirectionNum": "12",
//	"DirectionText": "SOUTH",
//	"Lat": 39.030003,
//	"Lon": -76.948868,
//	"RouteID": "B30",	
//	"TripEndTime": "2014-10-27T13:29:00",
//	"TripHeadsign": "GREENBELT STA",
//	"TripID": "6794867",
//	"TripStartTime": "2014-10-27T12:53:00",
//	"VehicleID": "6215"

	@JsonProperty("DirectionText")
	abstract Direction getDirection();
	@JsonProperty("DirectionText")
	abstract void setDirection();
}
