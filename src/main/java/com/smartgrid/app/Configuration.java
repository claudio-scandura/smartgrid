/**
 * 
 */
package com.smartgrid.app;

/**
 * @author Claudio Scandura (claudio.scandura@kcl.ac.uk)
 * 
 */
public class Configuration {
	final static byte H_POLICY = 'h', A_POLICY = 'a', C_OBJECT = 'c';
	byte type;
	String target;
	double distributionFactor;

	public Configuration(String type, String target, String distributionFactor)
			throws IllegalArgumentException {
		super();
		if (type.equalsIgnoreCase(SimulationLauncher.HOUSEHOLD_POLICY_TYPE_ID))
			this.type = H_POLICY;
		else
			throw new IllegalArgumentException("Wrong element type");
		if (!target.matches("[A-Za-z0-9]+"))
			throw new IllegalArgumentException("Bad class name");
		this.target = target;
		int factorSize;
		if (distributionFactor
				.charAt(factorSize = distributionFactor.length() - 1) != '%')
			throw new IllegalArgumentException();
		try {
			this.distributionFactor = Double.parseDouble(distributionFactor
					.substring(0, factorSize));
			if (this.distributionFactor<0 || this.distributionFactor>100)
				throw new IllegalArgumentException("The distribution factor must be in the range [0, 100]");
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Cannot parse distributio factor");
		}
	}

	public Configuration(String type, String target)
			throws IllegalArgumentException {
		super();
		if (type.equalsIgnoreCase(SimulationLauncher.AGGREGATOR_POLICY_TYPE_ID))
			this.type = A_POLICY;
		else if (type.equalsIgnoreCase(SimulationLauncher.CUSTOM_CLASS_TYPE_ID))
			this.type = C_OBJECT;
		else
			throw new IllegalArgumentException();
		if (!target.matches("[A-Za-z0-9]+"))
			throw new IllegalArgumentException("Bad class name");
		this.target = target;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public double getDistributionFactor() {
		return distributionFactor;
	}

	public void setDistributionFactor(int distributionFactor) {
		this.distributionFactor = distributionFactor;
	}
}
