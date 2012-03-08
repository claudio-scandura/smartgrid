package com.smartgrid.policies;

import com.smartgrid.app.Appliance;

public class GenericAppliance implements Appliance {
	private Integer id;
	private Double	demand;
	private boolean status;
	
	public GenericAppliance (Integer id, Double demand) {
		this.id = id;
		this.demand = demand;
	}
	
	public Double getDemand() {
		return this.demand;
	}
	
	public String getName() {
		return "Generic Appliance";
	}

	public Integer getId() {
		return this.id;
	}

	public boolean getStatus() {
		// TODO Auto-generated method stub
		return status;
	}
}
