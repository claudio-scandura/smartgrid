/**
 * 
 */
package com.smartgrid.app;

/**
 * @author claudioscandura
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
		if (type.equalsIgnoreCase(Main.HOUSEHOLD_POLICY_TYPE_ID))
			this.type = H_POLICY;
		else
			throw new IllegalArgumentException();
		if (!target.matches("[A-Za-z]+"))
			throw new IllegalArgumentException();
		this.target = target;
		int factorSize;
		if (distributionFactor
				.charAt(factorSize = distributionFactor.length() - 1) != '%')
			throw new IllegalArgumentException();
		try {
			this.distributionFactor = Double.parseDouble(distributionFactor
					.substring(0, factorSize));
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException();
		}
	}

	public Configuration(String type, String target)
			throws IllegalArgumentException {
		super();
		if (type.equalsIgnoreCase(Main.AGGREGATOR_POLICY_TYPE_ID))
			this.type = A_POLICY;
		else if (type.equalsIgnoreCase(Main.CUSTOM_CLASS_TYPE_ID))
			this.type = C_OBJECT;
		else
			throw new IllegalArgumentException();
		if (!target.matches("[A-Za-z]+"))
			throw new IllegalArgumentException();
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
