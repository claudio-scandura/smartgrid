package com.smartgrid.app;

import java.util.HashMap;
import java.util.Map;

public class Settings {

	private Map<String, Configuration> configurations;
	
	public Settings() {
		configurations= new HashMap<String, Configuration>();
	}
	
	public void add(Configuration c) {
		configurations.put(c.getTarget(), c);
	}
	
	public void remove(String k) {
		configurations.remove(k);
	}
	
	public Configuration getConfiguration(String k) {
		return configurations.get(k);
	}
	
	public Map<String, Configuration> getMap() {
		return configurations;
	}
}
