package com.scriptbuilder;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.scriptbuilder.command.Command;

public class Analizer {

	public static String command_start = "@@[";
	public static String command_end = "]@@";
	public static String command_separator = ":";
	public static String parameter_separator = "@@;";
	public static String rules_separator = ",";
	public static String filter_separator = "filter:";
	
	private static String template = "";
	private static String project = "";
	private static String version = "";
	private static String author = "";
	private static String licence = "";
	private static String sheetName = "";
	private static String startRow = "";
	private static String endRow = "";
	private static String tagetName = "";

	private static final String PACKAGE_NAME = "com.scriptbuilder.command.";
	
	private static String [][] commands = new String[][] {
		{"template", "required:String,not null"}
		, {"project", "required:String,not null"}
		, {"version", "required:String,not null"}
		, {"author", "required:String,not null"}
		, {"licence", "required:String,not null"}
		, {"sheetName", "required:String,not null"}
		, {"startRow", "required:String,not null"}
		, {"endRow", "required:String,not null"}
		, {"tagetName", "required:String,null"}
		, {"content", "required:multiline,null", PACKAGE_NAME + "Content"}
		, {"rows", "optional:not null", PACKAGE_NAME + "Rows"}
		, {"colDateTime", "optional:not null@@;String,null", PACKAGE_NAME + "ColDateTime"}
		, {"colDecimal", "optional:not null@@;String,null", PACKAGE_NAME + "ColDecimal"}
		, {"colInteger", "optional:not null", PACKAGE_NAME + "ColInteger"}
		, {"colString", "optional:not null", PACKAGE_NAME + "ColString"}
		, {"multiply", "optional:not null@@;String,null", PACKAGE_NAME + "Multiply"}
		, {"round", "optional:not null@@;String,null", PACKAGE_NAME + "Round"}
	};

	public static boolean analize (StringBuffer template) {
		
		boolean valid = validateRequired(template);
		
		return valid;
		
	}
	
	public static StringBuffer build (StringBuffer template, Workbook workbook) {
		
		int i=0;
		StringBuffer result = new StringBuffer(0);
		
        Sheet sheet = workbook.getSheet(sheetName);

		while (hasMoreCommand(template, i)) {
			
			StringBuffer cmdResult = new StringBuffer(0);
			
			i = nextCommand(template, i, cmdResult);
			
			result.append(getContent(cmdResult.toString(), sheet, null));
			
			System.out.format("%s%n", cmdResult.toString());
			
		}
		
		return result;
		
	}
	
	private static boolean validateRequired (StringBuffer template) {

		boolean valid = true;
		
		for (String[] theCommand : commands) {
			
			String params[] = theCommand[1].split(command_separator);
			
			if (params[0].equals("required")) {
				
				String command = command_start + theCommand[0] + command_separator;
				
				int start = template.indexOf(command);
				
				if (start > -1) {
					
					StringBuffer commandResult = new StringBuffer(0);
					
					nextCommand(template, start, commandResult);
					
					if (validateContent(commandResult.toString(), params[1])) {
						setContent(commandResult.toString(), theCommand[0]);
					} else {
						System.err.format("Invalid content of the command %s.%n", command);
						valid = false;
					}
					
				} else {
					System.err.format("Command %s not found.%n", command);
					valid = false;
				}
				
			}
			
		}

		return valid;
	}

	private static boolean validateContent (String content, String params) {
		String rules[] = params.split(rules_separator);
		
		content = getContent(content);
		
		for (String rule : rules) {
			if (rule.equals("not null") && content.equals("")) {
				System.err.format("Required content error.%n");
				return false;
			}
		}
		
		System.out.format("Content \"%s\" is valid.%n", content);
		
		return true;
	}

	private static boolean hasMoreCommand (StringBuffer content, int i) {
		return content.indexOf(command_start, i) > -1;
	}
	
	private static int nextCommand (StringBuffer content, int i, StringBuffer result) {
		int start = content.indexOf(command_start, i);
		int separator = content.indexOf(command_separator, start);
		int lastStart = start;
		int end = i - 1;
		int cstart = 1;
		int cend = 0;
		
		while (start > -1 && cstart > cend) {
			
			end = content.indexOf(command_end, end + command_end.length());
			lastStart = content.indexOf(command_start, lastStart + command_start.length());
			cstart += lastStart > -1 && lastStart < end ? 1 : 0;
			cend += end > -1 ? 1 : 0;
			
			if (end == -1 && lastStart == -1 && cstart > cend) {
				System.err.format("Command %s is broken.", content.substring(start, separator + command_separator.length()));
			}

		}

		if (start > -1 && separator > -1 && end > -1) {
			result.append(content.substring(start, end + command_end.length()));
			return end + command_end.length();
		}
		
		return end;
	}
	
	private static String getContent (String content) {
		int start = content.indexOf(command_start);
		int separator = content.indexOf(command_separator, start);
		int lastStart = start;
		int end = -1;
		int cstart = 1;
		int cend = 0;
		
		while (start > -1 && cstart > cend) {
			
			end = content.indexOf(command_end, end + command_end.length());
			lastStart = content.indexOf(command_start, lastStart + command_start.length());
			cstart += lastStart > -1 && lastStart < end ? 1 : 0;
			cend += end > -1 ? 1 : 0;
			
			if (end == -1 && lastStart == -1 && cstart > cend) {
				System.err.format("Command %s is broken.", content.substring(start, separator + command_separator.length()));
			}
			
		}

		if (start > -1 && separator > -1 && end > -1) {
			
			String command = content.substring(start + command_start.length(), separator);
			
			String cmdContent = validateCommand(command, getContent(content.substring(separator + command_separator.length(), end)));
			
			return content.substring(0, start) + cmdContent + getContent(content.substring(end + command_end.length(), content.length()));
			
		}
		
		return content;
	}
	
	public static String getContent (String content, Object object, String[] commandParams) {
		int start = content.indexOf(command_start);
		int separator = content.indexOf(command_separator, start);
		int lastStart = start;
		int end = -1;
		int cstart = 1;
		int cend = 0;
		
		while (start > -1 && cstart > cend) {
			
			end = content.indexOf(command_end, end + command_end.length());
			lastStart = content.indexOf(command_start, lastStart + command_start.length());
			cstart += lastStart > -1 && lastStart < end ? 1 : 0;
			cend += end > -1 ? 1 : 0;
			
			if (end == -1 && lastStart == -1 && cstart > cend) {
				System.err.format("Command %s is broken.", content.substring(start, separator + command_separator.length()));
			}

		}

		if (start > -1 && separator > -1 && end > -1) {
			
			int aux = 0;
			String command = content.substring(start + command_start.length(), separator);
			
			String cmdContent = "";
			for (String[] theCommand : commands) {
				if (theCommand[0].equals(command)) {
					if (theCommand[0].equals("rows")) {
						Sheet sheet = (Sheet) object;
				        Iterator<Row> iterator = sheet.iterator();
				        while (iterator.hasNext()) {
				            Row row = iterator.next();
				            int rowNum = row.getRowNum();
				    		if (rowNum >= Analizer.getStartRow() && rowNum <= Analizer.getEndRow()) {
				    			cmdContent += executeCommand(theCommand, getContent(content.substring(separator + command_separator.length(), end), row, commandParams), row, commandParams);
				    		}
				        }
					} else {
						cmdContent = executeCommand(theCommand, getContent(content.substring(separator + command_separator.length(), end), object, commandParams), object, commandParams);
					}
					break;
				}
			}
			
			return content.substring(0, start + aux) + cmdContent + getContent(content.substring(end + command_end.length(), content.length()), object, commandParams);
			
		}
		
		return content;
	}

	private static String executeCommand (String[] theCommand, String content, Object object, String[] commandParams) {
		
		if (theCommand.length == 3) {
			
			try {
				Class<?> k = Class.forName(theCommand[2]);
				Object o = k.newInstance();
				Command cmd = Command.class.cast(o);
				return cmd.execute(content, commandParams, object);
			} catch (Exception e) {
				System.err.format("Command %s throw an error.%n", theCommand[2]);
			}
			
		}

		return "";
	}

	public static int countStart(String content, String[] chars, int aux) {
		
		if (chars != null) {
			for (String character : chars) {
				
				if (content.startsWith(character)) {
					return countStart(content.substring(character.length(), content.length()), chars, aux + character.length());
				}
				
			}
		}
		
		return aux;
	}
	
	public static int countEnd(String content, String[] chars, int aux) {

		if (chars != null) {
			for (String character : chars) {
				
				if (content.endsWith(character)) {
					return countEnd(content.substring(0, content.length() - character.length()), chars, aux + character.length() * -1);
				}
				
			}
		}
		
		return aux;
	}
	
	private static String validateCommand (String command, String content) {
		
		for (String[] theCommand : commands) {
			if (theCommand[0].equals(command)) {
				return validateCommand(theCommand, content);
			}
		}
		
		System.out.format("Warning: Command %s not found.%n", command);
		
		return "";
	}

	private static String validateCommand (String[] theCommand, String content) {
		
		if (theCommand.length == 3) {
			
			String params[] = theCommand[1].split(command_separator)[1].split(parameter_separator);
			
			try {
				Class<?> k = Class.forName(theCommand[2]);
				Object o = k.newInstance();
				Command cmd = Command.class.cast(o);
				return cmd.validate(content, params);
			} catch (Exception e) {
				System.err.format("Command %s throw an error.%n", theCommand[2]);
			}
			
		} else {
			return content;
		}

		return "";
	}

	private static void setContent (String content, String command) {
		if (command.equals("template")) template = getContent(content);
		if (command.equals("project")) project = getContent(content);
		if (command.equals("version")) version = getContent(content);
		if (command.equals("author")) author = getContent(content);
		if (command.equals("licence")) licence = getContent(content);
		if (command.equals("sheetName")) sheetName = getContent(content);
		if (command.equals("startRow")) startRow = getContent(content);
		if (command.equals("endRow")) endRow = getContent(content);
		if (command.equals("tagetName")) tagetName = getContent(content);
	}
	
	public static String getTagetName() {
		return tagetName;
	}

	public static String getSheetName() {
		return sheetName;
	}

	public static Integer getStartRow() {
		if (StringUtils.isEmpty(startRow)) {
			return null;
		}
		return new Integer(startRow);
	}

	public static Integer getEndRow() {
		if (StringUtils.isEmpty(endRow)) {
			return null;
		}
		return new Integer(endRow);
	}

	public static String getTemplate() {
		return template;
	}
	
	public static String getProject() {
		return project;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getAuthor() {
		return author;
	}
	
	public static String getLicence() {
		return licence;
	}

}
