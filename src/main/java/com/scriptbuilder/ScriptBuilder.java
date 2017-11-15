package com.scriptbuilder;

import java.io.File;
import java.util.Properties;

public class ScriptBuilder {
	
	private static final Package PKG = ScriptBuilder.class.getPackage();
	
    public static void main (String[] args) throws Exception {
    	
    	System.out.format("Starting %s - Version %s.%n"
    			, PKG.getSpecificationTitle()
    			, PKG.getSpecificationVersion());
    	
    	initialize(args);
    	
    }

    private static void initialize (String[] args) throws Exception {
    	
    	System.out.format("Initializing %s.%n", PKG.getSpecificationTitle());
    	
    	Properties properties = checkParameter(args);

		Engine engine = Engine.getInstance(properties);
		engine.process();
		
    }
    
    private static Properties checkParameter (String[] args) {
    	
    	System.out.format("Checking for parameters.%n");
    	
    	if (args == null || (args.length != 3)) {
    		System.err.format("Argument error: Number of parameters is invalid.%n");
    		System.err.format("Please type: scriptbuilder sourceSheetFile sqlTemplateFolder/sqlTemplateFile targetScriptFolder.%n");
    		System.exit(1);
    	}
    	
    	System.out.format("Checking argument [%s].%n", args[0]);
    	System.out.format("Checking argument [%s].%n", args[1]);
    	System.out.format("Checking argument [%s].%n", args[2]);
    	
    	Properties properties = new Properties();
    	properties.put ("sourceFile", args[0]);
    	if (isFolder(args[1])) {
    		properties.put ("templateFolder", args[1]);
    	} else {
    		properties.put ("templateFile", args[1]);
    	}
    	properties.put ("targetFolder", args[2]);
    	
    	System.out.format("Parameters is ok.%n");

    	return properties;
    	
    }

    private static boolean isFolder (String templatePath) {
    	File path = new File(templatePath);
    	return path.isDirectory();
    }
    
}
