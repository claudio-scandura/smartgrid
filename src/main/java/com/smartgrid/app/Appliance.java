package com.smartgrid.app;

public interface Appliance {
	public Double getDemand();

	public String getName();
	
	public Integer getId();
	
	/**
	 * Get the status of the appliance
	 * 
	 * @return true if the appliance is on
	 * @return false if the appliance is off
	 */
	public boolean getStatus();
}
