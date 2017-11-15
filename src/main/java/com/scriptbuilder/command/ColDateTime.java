package com.scriptbuilder.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.scriptbuilder.Analizer;

public class ColDateTime implements Command {

	protected static final Logger log = Logger.getLogger(ColDateTime.class.getSimpleName());
	
	private static DateFormat dateFormat;

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		
		String[] parameters = content.split(Analizer.parameter_separator);

		String format = "yyyy-MM-dd HH:mm:ss";
		
		if (parameters.length > 1 && StringUtils.isNotEmpty(parameters[1])) {
			format = parameters[1];
		}
		
		dateFormat = new SimpleDateFormat(format);
		
		Row row = (Row) object;

		if (parameters.length > 0 && !StringUtils.isEmpty(parameters[0])) {
			Integer i = new Integer(parameters[0]);
			Cell cell = row.getCell(i);
			if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
				Date date = cell.getDateCellValue();
				if (date != null) {
					String value = dateFormat.format(date);
					log.log(Level.INFO, "Cell: " + value);
					return value;
				}
			}
			
		}
		
		return "";
		
	}
	
}
