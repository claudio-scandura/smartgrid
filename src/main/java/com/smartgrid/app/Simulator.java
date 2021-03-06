package com.smartgrid.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import com.smartgrid.app.Aggregator;
import com.smartgrid.messenger.Message;
import com.smartgrid.messenger.Messenger;
import com.smartgrid.messenger.MessengerBasic;

public class Simulator {
	private Long tick = 0L; // current tick
	private Long iterations; // total number of iterations
	private Long startTime; // start time epoch (in milliseconds)
	private Long currentTime; // end time epoch (in milliseconds)
	private Messenger<Household> messenger;
	private  Integer granularity; // seconds per tick
	private AggregatorPolicy aggregatorPolicy;
	private Aggregator aggregator;
	private Logger logger;
	
	public Simulator(HashMap<Integer,Household> households,
			Long iterations,
			Integer granularity,
			AggregatorPolicy aggregatorPolicy,
			Logger logger
			) {
		this.iterations  = iterations;
		this.granularity = granularity;
		this.aggregatorPolicy = aggregatorPolicy;
		
		messenger = new MessengerBasic<Household>(households);
		aggregator = new Aggregator(messenger, logger);
		
		// store supply and notify households of initial price
		aggregator.setElectricitySupply(aggregatorPolicy.getSupply());
		aggregator.setElectricityPrice(aggregatorPolicy.getPrice());
		//retrieve list of appliances
		aggregator.updateApplianceMap();
		this.logger = logger;
	}

	
	// call tick on households and aggregator
	// update list of demands
	private void tick(Date date) {
		System.out.println("Broadcasting message to: "+Arrays.toString(messenger.memberIds()));
		messenger.<Void,Date>messageMany(messenger.memberIds(), new Message<Date>("tick", date));
		//aggregator.updateApplianceMap();
		Double overallDemand  = aggregator.updateHouseholdDemands(date);
		
		logger.logAggregator(date, aggregator.getElectricitySupply(), overallDemand, aggregator.getElectricityPrice());
		aggregatorPolicy.tick(date, aggregator);
	}
	
	public void run() throws Exception {
		System.out.println("Starting simulation...");
		currentTime = (new Date ()).getTime();
		Date tmp = new Date();
		while (tick < iterations) {
			tick++;
			System.out.println(tick);
			currentTime += 1000L * granularity*60;
			tmp.setTime(currentTime);
			tick(tmp);
		}
	}
}
