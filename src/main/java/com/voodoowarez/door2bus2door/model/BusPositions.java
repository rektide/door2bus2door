package com.voodoowarez.door2bus2door.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public interface BusPositions {
	@JsonProperty("BusPositions")
	public List<? extends BusPosition> getPositions();
}
