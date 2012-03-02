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
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author claudioscandura
 *
 */
public class Main {

	static String HOUSEHOLD_POLICY_TYPE_ID="HouseholdPolicy";
	static String AGGREGATOR_POLICY_TYPE_ID="AggregatorPolicy";
	static String CUSTOM_CLASS_TYPE_ID="CustomObject";
	static String TMP_DIR="dir";
	static ClassLoader loader;
	static Simulator simulation;
	static int simulationGranularity;
	static long iterations;
	static int numOfHouseholds;
	static String jarFilePath="/Users/claudioscandura/Dropbox/KCL/Workspace/Java/ClassLoadingSample/testJar.jar",
			configurationFilePath="/Users/claudioscandura/Dropbox/KCL/Workspace/Java/Simulator/src/com/smartgrid/simulator/settings.cfg",
			aggregatorPolicyToLoad="";
	static List<String> customClassesToLoad=new ArrayList<String>();
	static Map<String, Double> householdPoliciesToLoad=new HashMap<String, Double>();
	
				
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//get parameters..
		
		
		
		
		//parse configuration file and brief check against the configurations 
		Settings configurations=null;
		try {
			configurations = parseConfigurationFile();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Configuration c : configurations.getMap().values())
			System.out.println("Type: "+c.getType()+", Target: "+c.getTarget()+
					", Distribution factor: "+c.getDistributionFactor()+"%");
		
		//extract jar and load classes 
		List<String> classes =null;
		classes=unpackJar();
		for (String s : classes)
			System.out.println(s);
		
		splitClasses(classes, configurations);
		System.out.println("Aggregator policy class: "+aggregatorPolicyToLoad);
		for (String s: customClassesToLoad)
			System.out.println("Custom class: "+s);
		for (Map.Entry<String, Double> e: householdPoliciesToLoad.entrySet())
			System.out.println("Household Policy: "+ e.getKey() + ", distribution: "+ e.getValue());
		
		
		//check parameters and household distribution and finally create and run simulation
	}
	
	private static void getParameters() {
		
	}
	
	private static void splitClasses(List<String> classes, Settings configurations) {
		for (String s: classes) {
			int indexOfSeparator;
			if ((indexOfSeparator=s.lastIndexOf(File.separatorChar))==-1)
				indexOfSeparator=0;
			String fileName=s.substring(indexOfSeparator+1, s.length()-6);
			Configuration config;
			if ((config=configurations.getConfiguration(fileName))!=null) {
				if (config.getType()==Configuration.A_POLICY)
					aggregatorPolicyToLoad=s.replace(File.separatorChar, '.').substring(0, s.length()-6);
				else if (config.getType()==Configuration.H_POLICY) {
					householdPoliciesToLoad.put(
							s.replace(File.separatorChar, '.').substring(0, s.length()-6),
							config.getDistributionFactor());
				}
				else
					customClassesToLoad.add(
							s.replace(File.separatorChar, '.').substring(0, s.length()-6));
			}
		}
	}
	
	/**
	 * Unpack the jar archive containing the user-defined classes to load for the simulation
	 * @return a list containing the relative paths to each of the classes 
	 */
	public static ArrayList<String> unpackJar() {
		ArrayList<String> res= new ArrayList<String>();
		new File(TMP_DIR).mkdir();
		try {
			JarFile jar = new JarFile(jarFilePath);
			Enumeration<JarEntry> enumerator = jar.entries();
			while (enumerator.hasMoreElements()) {
				JarEntry file = (JarEntry) enumerator
						.nextElement();
				
				if (file.getName().endsWith(".class")) {//System.out.println(file.getName());
					int lastIndex;
					if ((lastIndex=file.getName().lastIndexOf(File.separator))!=-1) {
						String tmpFolder=file.getName().substring(0, lastIndex);
						(new File(TMP_DIR
							+ File.separator +tmpFolder)).mkdirs();
					}
					File f = new File(TMP_DIR
							+ File.separator + file.getName());//System.out.println("1"+f.getName());
				
					InputStream is = jar.getInputStream(file); // get
																		// the
																		// input
																		// stream
					//System.out.println(f.getName());
					FileOutputStream fos = new FileOutputStream(
							f);
					while (is.available() > 0) { // write contents of 'is' to
													// 'fos'
						fos.write(is.read());
					}
					res.add(file.getName());
					fos.close();
					is.close();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return res;
	}

	public static Settings parseConfigurationFile() throws ParseException {
		BufferedReader in=null;
		Settings result = new Settings();
		try {
			in = new BufferedReader(new FileReader(configurationFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String tmp;
		try {
			int lineIndex=0;
			while ((tmp=in.readLine())!=null) {
				lineIndex++;
				String[] tmpArr = tmp.split("::");
				if (tmpArr.length<2)
					throw new ParseException("Bad configration file!", lineIndex);
				else if (tmpArr.length==2)
					result.add(new Configuration(tmpArr[0], tmpArr[1]));
				else
					result.add(new Configuration(tmpArr[0], tmpArr[1], tmpArr[2]));

			}
			in.close();
		}
		catch (IllegalArgumentException iae) {
			throw new ParseException("Bad configuration file", -1);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
