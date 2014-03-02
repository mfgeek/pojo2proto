package com.mfgeek.gb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class PojoToProto {
	private static String inputUsage = "root directory of the source code";
	private static String packageUsage = "specify the package name of the class " ;
	private static String outputUsage = "generated proto file output directory.";
	private static String opackageUsage = "specify the output package name of the proto" ;
	private static String usage = "usage: pojo2proto" + 
									"\n\t-i <dir>     " + inputUsage + 
									"\n\t-p <package>     " + packageUsage + 
									"\n\t-o <dir>     " + outputUsage +
									"\n\t-P <package>     " + opackageUsage + "\n";
	List<String> javaFiles = new ArrayList<String>();
	
	static String input = "src/main/resources/";
	static String output = "generated-proto";
	private static String packageName = "com.mfgeek.gb.pojo";
	private static String opackageName = "com.mfgeek.gb.proto";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create the command line parser
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		options.addOption( "i", "input", true, inputUsage);
		options.addOption( "p", "package", true, packageUsage);
		options.addOption( "o", "output", true, outputUsage );
		options.addOption( "P", "out-package", true, opackageUsage);
		try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        List<ProtoFile> protoFiles = null;
	        if(!line.hasOption("i") || !line.hasOption("o") || !line.hasOption("p"))
	        	usage();
		    if( line.hasOption( "i" ) && line.hasOption("p")) {
		    	input = line.getOptionValue( "i" ) ;
		    	packageName  = line.getOptionValue( "p" ) ;
		    	opackageName  = line.getOptionValue( "P" ) ;
		    	if(input != null && !input.trim().equals("")){
		    		protoFiles = ProtoGenerator.newInstance().parse(input, packageName, opackageName);
		    	}
		    	else
		    		usage();
		    }
		    if( line.hasOption( "o" ) ) {
		    	output = line.getOptionValue( "o" ) ;
		    	
		        if(protoFiles != null && protoFiles.size() > 0){
		        	for(ProtoFile pf: protoFiles)
		        		writeTo(output, pf);
		        }
		    }
	    }
	    catch( ParseException exp ) {
	        usage();
	        
	        exp.printStackTrace();
	    }
		
	}

	private static void writeTo(String dir, ProtoFile pf) {
		File out = new File(output);
		if(!out.exists()){
    		System.out.println(dir + " doesn't exist, will create a new directory with name:[" + dir + "]");
    		out = new File(output);
    		out.mkdirs();
    	}
		
		if(out.exists() && out.isFile()){
    		String dirName = out.exists()?output:(output + ".2");
    		System.out.println(dir + " is a file, create another directory with name:[" + dir + ".2]");
    		out = new File(dirName);
    		out.mkdirs();
    	}
		FileWriter writer = null;
		try {
			String name = out.getAbsolutePath() + "/" + pf.getName() + ".proto";
			System.out.println("writing proto file: " + name);
			writer = new FileWriter(name, false);
			writer.write(pf.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	private static void usage() {
		System.err.println( usage  );
	}
	
	

}
