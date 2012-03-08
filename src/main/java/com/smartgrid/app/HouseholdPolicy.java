package com.smartgrid.app;

import java.util.Date;
import java.util.List;

public interface HouseholdPolicy {
	public void tick(Date date);
	
	public String getPolicyAuthor();
	
	public String getPolicyName();
	
	public Double getPolicyVersion();
	
	public Double getElectricityDemand();
	
	public List<Appliance> getAppliances();
	
	public Integer turnOffAppliance(Appliance appliance);
	
	public Integer notifyPrice(Double newPrice);
	
	public CustomMessage handleMessage(CustomMessage m) throws Exception;
}
