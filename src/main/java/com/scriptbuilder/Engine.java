package com.scriptbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Engine {

	private static final String PATH_SEPARATOR = System.getProperty("file.separator");
	
	protected String templateFile;
	protected String templatePath;
	protected String sourceFile;
	protected String targetPath;
	
	public static Engine getInstance (Properties properties) {
		
		ExcelEngine engine = new ExcelEngine();
		engine.sourceFile = properties.get("sourceFile").toString();
		engine.templatePath = properties.get("templateFolder") != null ? properties.get("templateFolder").toString() : null;
		engine.templateFile = properties.get("templateFile") != null ? properties.get("templateFile").toString() : null;
		engine.targetPath = properties.get("targetFolder").toString();
		
		return engine;
	}
	
	public void process () throws IOException {
		
		File[] templateFiles = null;
		if (this.templatePath != null && this.templatePath.length() > 0) {
			File path = new File(this.templatePath);
			templateFiles = path.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".sb");
				}
			});
		} else if (StringUtils.isNotEmpty(this.templateFile)) {
			File templateFile = new File(this.templateFile);
			this.templatePath = templateFile.getParent();
			templateFiles = new File[] {templateFile}; 
		}

    	if (templateFiles == null) {
    		System.out.format("There is no template files found at path%s.%n", this.templatePath);
    		System.exit(1);
    	}

		File sheetFile = new File(this.sourceFile);
		
		if (sheetFile.exists() && sheetFile.isFile()) {
			
	    	// Getting external list of templates (from config.properties)
	    	List<String> templates = new ArrayList<String>();
	    	for (File file : templateFiles) {
	    		templates.add(file.getName());
	    	}

			System.out.format("Building target files to path %s.%n", this.targetPath);
			
			int count = 0;
			
			System.out.format("Compile templates to build files.%n");
			
			if (templates.size() == 0) {
	    		System.err.format("There is no template to build the files.%n");
	    		System.exit(1);
			}

			InputStream is = null;
			try {
				is = new FileInputStream(this.sourceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
	    		System.err.format("Error to load file %s.%n", this.sourceFile);
	    		System.exit(1);
			}
			
			Workbook workbook = null;
			try {
				workbook = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
	    		System.err.format("File %s cannot be loaded.%n", this.sourceFile);
	    		System.exit(1);
			}

			for (String templateName : templates) {
				String fileName = templatePath + PATH_SEPARATOR + templateName;
				
				File file = new File(fileName);
				
				if (file.exists() && file.isFile()) {
					
					StringBuffer template = getFile(file);
					
					if (template.length() == 0) {
						System.out.format("Template %s is empty.%n", fileName);
					} else {
						
						// Validate templates
						if (Analizer.analize(template)) {
							
							System.out.format("Template was successfully analized.%n");
							
							String targetFileName = this.targetPath + PATH_SEPARATOR +  Analizer.getTagetName();
							
							System.out.format("Deleting possible old file %s.%n", targetFileName);
							
							// Delete old file
							File scriptFile = new File(targetFileName);
							scriptFile.getParentFile().mkdirs();
							scriptFile.delete();
							
							// Compile
							StringBuffer result = Analizer.build(template, workbook);
							
							System.out.format("%s%n", result.toString());
							
							// Save the file
							saveFile(scriptFile, result);
							
							count++;

						}
						
					}
					
				}

			}
			
			workbook.close();
			
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			System.out.format("%s files has been built.%n", count);
			
		} else {
    		System.err.format("Sheet file not found: Cannot find the file %s to compile.%n", sheetFile);
    		System.exit(1);
		}
		
	}
	
	
	private StringBuffer getFile (File file) {
		
		System.out.format("Loading file %s.%n", file.getName());

		FileReader reader = null;
		BufferedReader buffer = null;
		
		try {
			
			reader = new FileReader(file);
			buffer = new BufferedReader(reader);
			
			StringBuffer fileToLoad = new StringBuffer((int)file.length());
			
			String line;
			while ((line = buffer.readLine()) != null) {
				fileToLoad.append(line).append("\n");
			}
			
			return fileToLoad;
			
		} catch (FileNotFoundException e) {
    		System.err.format("File not found: Cannot find the file %s to load.%n", file.getName());
		} catch (IOException e) {
			System.err.format("Error while loading file %s.%n", file.getName());
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		
		return new StringBuffer("");
		
	}
	
	private void saveFile (File file, StringBuffer content) {
		
		System.out.format("Saving file %s.%n", file.getName());

		FileWriter writer = null;
		BufferedWriter buffer = null;
		
		try {
			
			writer = new FileWriter(file);
			buffer = new BufferedWriter(writer);

			buffer.write(content.toString());
			buffer.flush();
			
		} catch (IOException e) {
			System.err.format("Error while saving file %s.%n", file.getName());
			e.printStackTrace();
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
		
}
