package com.scriptbuilder.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ColInteger implements Command {

	protected static final Logger log = Logger.getLogger(ColInteger.class.getSimpleName());

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		
		Row row = (Row) object;

		if (!StringUtils.isEmpty(content)) {
			Integer i = new Integer(content);
			Cell cell = row.getCell(i);
			
			if (cell != null && (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA)) {
				Long value = (long)cell.getNumericCellValue();
				log.log(Level.INFO, "Cell: " + value);
				return "" + value;
			} else if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
				Long value = new Long(cell.getStringCellValue());
				log.log(Level.INFO, "Cell: " + value);
				return "" + value;
			}
			
		}
		
		return "";
		
	}
	
}
