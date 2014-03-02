package com.mfgeek.gb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoGenerator {
	private static ProtoGenerator instance = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static ProtoGenerator newInstance() {
		if(instance == null)
			instance = new ProtoGenerator();
		return instance;
	}

	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("int", "int32");
		map.put("integer", "int32");
		map.put("short", "int32");
		map.put("long", "int64");// need to manually convert from pojo date value to long value
		map.put("string", "string");
		map.put("float", "float");
		map.put("double", "double");
		map.put("date", "int64");//convert date value to int64/long  TODO
		map.put("boolean", "bool");
		map.put("byte", "int32");
		//TODO add Collection support here
	}

	public List<ProtoFile> parse(String dir, String pack, String opackageName) {
		System.out.println("parsing java classes in package:" + dir);
		
		List<String> ignores = getIgnores();
		
		List<ProtoFile> protos = new ArrayList<ProtoFile>();
		File f = new File(dir + packToDir(pack));
		if(f.exists() && f.isDirectory() && f.list().length > 0){
			String [] javaFiles = f.list();
			System.out.println("found " + javaFiles.length + " java classes in package ");
			for(String j: javaFiles){
				System.out.print("processing class " + j);
				//skip **Example.java classes
				if(j.endsWith("Example.java")){
					System.out.println(" -- skipped **Example.java class");
					continue;
				}
				
				if(ignores.contains(j.substring(0,j.indexOf(".java")))){
					System.out.println(" -- skipped ignored files defined in ignore.txt");
					continue;
				}
				System.out.println(j );
				Class c;
				try {
					c = Class.forName(pack + "." + j.substring(0, j.indexOf(".")));
				
					String name = c.getSimpleName();
					String packageName = c.getPackage().toString();
					
					ProtoFile pf = new ProtoFile();
					pf.setJavaOuterClassName(name + "Obj");
					pf.setName(name);
					pf.setPackageName(opackageName);

					for(Field field: c.getDeclaredFields()){
						//skip serialVersionUID
						if(field.getName().equals("serialVersionUID"))
							continue;
//						System.out.println("add field in " + j + ":" + field.getName());
						String type = field.getType().getSimpleName().toLowerCase();
						if(map.containsKey(type))
							pf.appendField(true, getProtoType(type), field.getName(), null);
						else
							System.out.println("WARNING: failed to handle field [" 
										+ field.getName() +"] with type ["+ type+"]. Skipped.");
					}
					if((c.getName() + "Key").equals(c.getSuperclass().getName()))
					for(Field field: c.getSuperclass().getDeclaredFields()){
						//skip serialVersionUID
						if(field.getName().equals("serialVersionUID"))
							continue;
//						System.out.println("add field in " + j + ":" + field.getName());
						String type = field.getType().getSimpleName().toLowerCase();
						if(map.containsKey(type))
							pf.appendField(true, getProtoType(type), field.getName(), null);
						else
							System.out.println("WARNING: failed to handle field [" 
										+ field.getName() +"] with type ["+ type+"]. Skipped.");
					}
					
					protos.add(pf);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		else{
			
		}
		return protos;
	}

	public List<String> getIgnores() {
		List<String> ignoreFiles = new ArrayList<String>();
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("ignore.txt");
			InputStreamReader isr = new InputStreamReader(is);
			if(is != null && isr.ready()){
				BufferedReader br = new BufferedReader(isr); 
				String s; 
				while((s = br.readLine()) != null) { 
					ignoreFiles.add(s); 
//					System.out.println(s);
				} 
				isr.close();
				is.close(); 
			}
			else
				System.out.println("ignore.txt not found");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ignoreFiles;
	}

	private String getProtoType(String type) {
		
		return map .get(type);
	}

	private String dirToPack(String dir) {
		return dir.replace("/", ".");
	}
	
	private String packToDir(String packageName) {
		return packageName.replace(".", "/");
	}
	

}
