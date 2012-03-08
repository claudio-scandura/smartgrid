/**
 * 
 */
package com.smartgrid.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author Claudio Scandura (claudio.scandura@kcl.ac.uk)
 * 
 * Runs a Smart-Grid simulation according to the parameters specified in the command line
 * 
 */
public class SimulationLauncher {

	/**
	 * The name to be written in the configuration file to define a household policy
	 */
	static String HOUSEHOLD_POLICY_TYPE_ID = "HouseholdPolicy";
	
	/**
	 * The name to be written in the configuration file to define an aggregator policy
	 */
	static String AGGREGATOR_POLICY_TYPE_ID = "AggregatorPolicy";
	
	/**
	 * The name to be written in the configuration file to define a custom object
	 */
	static String CUSTOM_CLASS_TYPE_ID = "CustomObject";
	
	/**
	 * The directory where the .jar file gets unzipped (this folder is removed right after the classes are loaded)
	 */
	static String TMP_DIR = "tmp";
	
	/**
	 * The class loader used to load the classes inside the .jar archive
	 */
	static ClassLoader loader;
	
	/**
	 * The object simulator to initialize, set up, and run
	 */
	static Simulator simulation;
	
	/**
	 * The simulation granularity in minutes. Each iteration will be equivalent to "simulationGranularity" minutes in the simulator
	 */
	static Integer simulationGranularity=5;
	
	/**
	 * The number of iterations that need to be executed 
	 */
	static Long iterations=1L;
	
	/**
	 * The number of households to be simulated
	 */
	static int numOfHouseholds=10;
	
	/**
	 * The path to the .jar file where the user-defined classes are stored
	 */
	static String jarFilePath = "CustomClasses.jar";
	
	/**
	 * The path to the configuration file 
	 */
	static String configurationFilePath = "settings.cfg";
	
	/**
	 * The Java name (i.e java.lang.String) of the class that implements the AggregatorPolicy interface, defined in the .jar file
	 */
	static String aggregatorPolicyToLoad = "";
	
	/**
	 * The list of Java names (i.e java.lang.String) of the custom classes that the user defined in the .jar file
	 */
	static List<String> customClassesToLoad = new ArrayList<String>();
	
	/**
	 * The Map of couple <HouseholdPolicyNames, distributionFactor> that contains all the custom policies Java names (i.e java.lang.String) of the classes that implement the HouseholdPolicy interface.
	 * The names are associated with their distribution factors according to what is defined into the configuration file
	 */
	static Map<String, Double> householdPoliciesToLoad = new HashMap<String, Double>();
	
	/**
	 * The list of custom household policies objects loaded from the classe defined in the .jar file.
	 * The multiplicity of each policy object in the list is calculated according to its distribution factor and the total number of household to be simulated
	 */
	static List<HouseholdPolicy> customHouseholdPolicies;
	
	/**
	 * The aggregator policy object loaded from the class defined in the .jar file.
	 */
	static AggregatorPolicy AP; //wee need the AggregatorPolicy class here

	/**
	 * Reads the command line arguments and check their consistency. Then loads and allocates, according
	 * to the configuration file, the policies and objects defined by the user and finally create and run a simulation
	 * with the loaded and specified parameters
	 * 
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// get parameters..
		setParameters(args);
		// parse configuration file and brief check against the configurations
		Settings configurations = null;
		try {
			configurations = parseConfigurationFile();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Something went wrong during the parsing of the configuration file: "+
					configurationFilePath);
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Check policies settings
		String err;
		if ((err=checkPoliciesConfig(configurations))!=null) {
			System.out.println("Error in the policy configuration: "+err);
			System.exit(-1);
		}
		
		
		//start testing
		for (Configuration c : configurations.getMap().values())
			System.out.println("Type: " + c.getType() + ", Target: "
					+ c.getTarget() + ", Distribution factor: "
					+ c.getDistributionFactor() + "%");
		//end testing

		// extract jar and load classes
		List<String> classes = null;
		try {
			classes = unpackJar();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			System.out.println("Error while extracting the jar archive");
			e2.printStackTrace();
		}
		
		//start testing
		for (String s : classes)
			System.out.println(s);
		//end testing

		//assign the custom objects, household and aggregator policies to their respective variables
		splitClasses(classes, configurations);
		//check is the policies outnumber the households
		if (householdPoliciesToLoad.size()>numOfHouseholds) {
			System.out.println("Error: the number of household policies cannot exceed the total number of households!\nQuitting...");
			System.exit(-1);
		}
			
		//start testing
		System.out
				.println("Aggregator policy class: " + aggregatorPolicyToLoad);
		for (String s : customClassesToLoad)
			System.out.println("Custom class: " + s);
		for (Map.Entry<String, Double> e : householdPoliciesToLoad.entrySet())
			System.out.println("Household Policy: " + e.getKey()
					+ ", distribution: " + e.getValue());
		//end testing

		try {
			//Load the classes into the jar archive
			loadUserDefinedClasses();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Could not load the classes in the file: "
					+jarFilePath+". Check that the archive contains all the classes needed and that they are properly listed into the configuration file: "
					+configurationFilePath);
			e1.printStackTrace();
		}
		
		//remove the folder where the jar file was extracted
		deleteDir(new File(TMP_DIR));
		// check parameters and household distribution and finally create and
		HashMap<Integer, Household> householdPolicies=new HashMap<Integer, Household>();
		int i=0;
		Logger log = new Logger("localhost", "smartgrid", "rob", "test123");
		for (HouseholdPolicy hp: customHouseholdPolicies) 
			householdPolicies.put(i++, new Household(i, hp));
		simulation=new Simulator(householdPolicies,
				iterations,
				simulationGranularity,
				AP, 
				null);

		// run simulation
		try {
			simulation.run();
		}
		catch (RuntimeException re) {
			System.out.println("Exception while running the simulation: "+re.getMessage()+"\nExecution will be aborted.");
			re.printStackTrace();
			System.exit(-1);
		}
		return;
	}

	/**
	 * Makes sure that sum of the distribution factors is 100 and that there
	 *  is at least one household policy and exactly one aggregator policy
	 *  
	 * @param configurations the settings defined in the configuration file
	 * @return null if there the policies match the specifications
	 * @return a string containing a brief description of the error in the settings
	 */
	private static String checkPoliciesConfig(Settings configurations) {
		double sum=0;
		int hPolicy=0, aPolicy=0; 
		for (Configuration c: configurations.getMap().values()) {
			if (c.getType()==Configuration.H_POLICY) {
				sum+=c.getDistributionFactor();
				hPolicy++;
			}
			else if (c.getType()==Configuration.A_POLICY)
				aPolicy++;
		}
		if (hPolicy==0)
			return "There must be at least 1 household policy class!";
		if (aPolicy!=1)
			return "There must be exactly 1 aggregator policy class!";
		if (sum!=100.0)
			return "The sum of th policy distribution factors must be 100!";
		return null;
	}

	/**ROB
	 * 
	 * Parses the arguments line and read the options with their respective parameters.
	 * This methods sets the static variables of this class so that the configuration file can be
	 * read, the user-defined classes can be loaded and finally, that the simulation can be ran.
	 * 
	 * @param args The arguments line
	 * @throws IllegalArgumentException
	 *  If the line contains inconsistent data (wrong option or parameter). The Exception should contain a brief description of what the error is.
	 */
	private static void setParameters(String []args) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Loads the classes the user defined in the .jar file provided
	 * 
	 * @throws Exception if an error occurs during the loading. 
	 * The Exception should contain a brief description of what the error is.
	 */
	private static void loadUserDefinedClasses() throws Exception{
		List<String> userDefinedClasses = new ArrayList<String>();
		userDefinedClasses.addAll(customClassesToLoad);
		userDefinedClasses.addAll(householdPoliciesToLoad.keySet());
		userDefinedClasses.add(aggregatorPolicyToLoad);
		loader = new PolicyLoader(PolicyLoader.class.getClassLoader(),
				userDefinedClasses);
		customHouseholdPolicies = new ArrayList<HouseholdPolicy>();
		Class<?> policyClass = null;
		try {
			policyClass = loader.loadClass(aggregatorPolicyToLoad);
			AP=(AggregatorPolicy) policyClass.newInstance();
			//System.out.println("Method invokation: "+AP.getString());
			for (Map.Entry<String, Double> e : householdPoliciesToLoad.entrySet()) {
				policyClass = loader.loadClass(e.getKey());
				List<HouseholdPolicy> tmp=instantiateHouseholdPolicies(policyClass, e.getValue());
				customHouseholdPolicies.addAll(tmp);
			}
		} catch (ClassNotFoundException cnfe) {
			// TODO Auto-generated catch block
			throw new Exception(cnfe);
		} catch (InstantiationException ie) {
			// TODO Auto-generated catch block
			throw new Exception(ie);
		} catch (IllegalAccessException iae) {
			throw new Exception(iae);
		}
		//for testing
		for (HouseholdPolicy p: customHouseholdPolicies)
			System.out.println("Class: "+p.getClass()+" Method invokation: "+ p.getPolicyAuthor());
		//end testing
	}
	
	/**
	 * Create instances of the household policies loaded from the .jar file provided.
	 * 
	 * @param cl The class to instantiate
	 * @param distributionFactor the percentage of objects that need to be created in respect to the total number of households
	 *
	 * @return A list of household policies
	 * @throws InstantiationException If an error occur during the allocation of an object
	 * @throws IllegalAccessException If an error occur during the allocation of an object
	 */
	private static List<HouseholdPolicy> instantiateHouseholdPolicies(Class<?> cl, double distributionFactor) 
	throws InstantiationException, IllegalAccessException{
		List<HouseholdPolicy> res = new ArrayList<HouseholdPolicy>();
		//!!!!!Careful here, you risk to leave less policies than households
		int numOfInstances = (int) ((numOfHouseholds*distributionFactor)/100);
		for (int k=0; k< numOfInstances; k++)
			res.add((HouseholdPolicy) cl.newInstance());
		return res;
	}

	/**
	 * Splits the given list and assigns the partitions to the static variables that hold the names for the
	 * household policies, aggregator policy and custom object classes
	 * 
	 * @param classes The list containing the path of the classes
	 * @param configurations The settings specified by the user into the configuration file
	 */
	private static void splitClasses(List<String> classes,
			Settings configurations) {
		for (String s : classes) {
			int indexOfSeparator;
			if ((indexOfSeparator = s.lastIndexOf(File.separatorChar)) == -1)
				indexOfSeparator = 0;
			String fileName = s.substring(indexOfSeparator + 1, s.length() - 6);
			Configuration config;
			if ((config = configurations.getConfiguration(fileName)) != null) {
				if (config.getType() == Configuration.A_POLICY)
					aggregatorPolicyToLoad = s.replace(File.separatorChar, '.')
							.substring(0, s.length() - 6);
				else if (config.getType() == Configuration.H_POLICY) {
					householdPoliciesToLoad.put(
							s.replace(File.separatorChar, '.').substring(0,
									s.length() - 6),
							config.getDistributionFactor());
				} else
					customClassesToLoad.add(s.replace(File.separatorChar, '.')
							.substring(0, s.length() - 6));
			}
		}
	}

	/**
	 * Unpack the jar archive containing the user-defined classes to load for
	 * the simulation
	 * 
	 * @return a list containing the relative paths to each of the classes
	 */
	public static ArrayList<String> unpackJar() throws IOException {
		ArrayList<String> res = new ArrayList<String>();
		new File(TMP_DIR).mkdir();
			JarFile jar = new JarFile(jarFilePath);
			Enumeration<JarEntry> enumerator = jar.entries();
			while (enumerator.hasMoreElements()) {
				JarEntry file = (JarEntry) enumerator.nextElement();

				if (file.getName().endsWith(".class")) {
					int lastIndex;
					if ((lastIndex = file.getName().lastIndexOf(File.separator)) != -1) {
						String tmpFolder = file.getName().substring(0,
								lastIndex);
						(new File(TMP_DIR + File.separator + tmpFolder))
								.mkdirs();
					}
					File f = new File(TMP_DIR + File.separator + file.getName());

					InputStream is = jar.getInputStream(file); // get
																// the
																// input
																// stream
					FileOutputStream fos = new FileOutputStream(f);
					while (is.available() > 0) { // write contents of 'is' to
													// 'fos'
						fos.write(is.read());
					}
					res.add(file.getName());
					fos.close();
					is.close();
				}
		} 
		return res;
	}

	/**
	 * Parses the configuration file defined by the user and creates a Settings object
	 * 
	 * @return A settings object
	 * @throws ParseException If an error occurs during the parsing
	 */
	private static Settings parseConfigurationFile() throws ParseException {
		BufferedReader in = null;
		Settings result = new Settings();
		try {
			in = new BufferedReader(new FileReader(configurationFilePath));
		} catch (FileNotFoundException e) {
			throw new ParseException("File does not exist!", -1);
		}
		String tmp;
		try {
			int lineIndex = 0;
			while ((tmp = in.readLine()) != null) {
				lineIndex++;
				if (!tmp.startsWith("#")) {
					String[] tmpArr = tmp.split("::");
					try {
						if (tmpArr.length < 2)
							throw new ParseException("Bad configuration file!",
									lineIndex);
						else if (tmpArr.length == 2)
							result.add(new Configuration(tmpArr[0], tmpArr[1]));
						else
							result.add(new Configuration(tmpArr[0], tmpArr[1],
									tmpArr[2]));
					}
					catch (IllegalArgumentException iae) {
						ParseException pe = new ParseException("Bad configuration file!.."+iae.toString() ,
								lineIndex);
						pe.setStackTrace(iae.getStackTrace());
						throw pe;
					}
				}
			}
			in.close();
		} catch (IllegalArgumentException iae) {
			throw new ParseException("Bad configuration file", -1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Delete a directory recursively.
	 * 
	 * @param dir The directory
	 * @return true If the directory was deleted successfully
	 * @return false Otherwise
	 */
	private static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
}
