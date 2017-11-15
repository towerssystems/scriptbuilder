package com.scriptbuilder.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Row;

public class Rows implements Command {

	protected static final Logger log = Logger.getLogger(Rows.class.getSimpleName());

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		
		Row currentRow = (Row) object;

		int rowNum = currentRow.getRowNum();
		
		log.log(Level.INFO, "Row.: " + rowNum);
		
		return content;

	}

}
