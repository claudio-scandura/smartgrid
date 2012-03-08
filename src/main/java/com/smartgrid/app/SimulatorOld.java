package com.smartgrid.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Claudio Scandura (claudio.scandura@kcl.ac.uk)
 *
 */
public class SimulatorOld {
	
	public static int granularity;
	private long iterations;
	private Aggregator aggregator;
	private AggregatorPolicy aggregatorPolicy;
	private Map<Integer, Household> householdPolicies;

	public Simulator(List<HouseholdPolicy> policies) {
		householdPolicies=new HashMap<Integer, Household>();
		int i=0;
		for (HouseholdPolicy hp: policies)
			householdPolicies.put(i, new Household(i, hp));
	}
	
	public void runSimulation() throws RuntimeException {
		//run it!
		try {
			
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Map<Integer, Household> getHouseholdPolicies() {
		return householdPolicies;
	}



	public void setHouseholdPolicies(Map<Integer, Household> householdPolicies) {
		this.householdPolicies = householdPolicies;
	}



	public static int getGranularity() {
		return granularity;
	}

	public static void setGranularity(int granularity) {
		Simulator.granularity = granularity;
	}

	public long getIterations() {
		return iterations;
	}

	public void setIterations(long iterations) {
		this.iterations = iterations;
	}

	public Aggregator getAggregator() {
		return aggregator;
	}

	public void setAggregator(Aggregator aggregator) {
		this.aggregator = aggregator;
	}

	public AggregatorPolicy getAggregatorPolicy() {
		return aggregatorPolicy;
	}

	public void setAggregatorPolicy(AggregatorPolicy aggregatorPolicy) {
		this.aggregatorPolicy = aggregatorPolicy;
	}
	
	
}
