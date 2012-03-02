package com.smartgrid.app;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Claudio Scandura (claudio.scandura@kcl.ac.uk)
 *
 */
public class Settings {

	private Map<String, Configuration> configurations;
	
	public Settings() {
		configurations= new HashMap<String, Configuration>();
	}
	
	public void add(Configuration c) {
		if (configurations.containsKey(c.target))
			throw new IllegalArgumentException("Duplicated configuration!");
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
