package com.voodoowarez.door2bus2door;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class CapitalizedNamingStrategy extends PropertyNamingStrategy {
	@Override
	public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return convert(defaultName);
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return convert(defaultName);
	}

	static public String convert(String defaultName) {
		if(defaultName.endsWith("Id")){
			return defaultName.substring(0, 1).toUpperCase() + defaultName.substring(1, defaultName.length() - 2) + "D";
		} else {
			return defaultName.substring(0, 1).toUpperCase() + defaultName.substring(1);
		}
	}
}
