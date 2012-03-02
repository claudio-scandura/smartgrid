package com.smartgrid.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Claudio Scandura (claudio.scandura@kcl.ac.uk)
 *
 */
public class PolicyLoader extends ClassLoader {
	
	private Set<String> classes;

	public PolicyLoader(ClassLoader parent, List<String> classes) {
		super(parent);
		this.classes=new HashSet<String>();
		for (String s: classes)
			this.classes.add(s);
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!classes.contains(name))
			return super.loadClass(name);

		try {
			String path=SimulationLauncher.TMP_DIR+File.separatorChar+name.replace('.', File.separatorChar)+".class";
			InputStream input = new FileInputStream(path);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();
			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}
			input.close();
			byte[] classData = buffer.toByteArray();
			return defineClass(name, classData, 0,
					classData.length);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return null;
	}
}
