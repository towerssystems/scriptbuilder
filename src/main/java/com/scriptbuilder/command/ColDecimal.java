package com.scriptbuilder.command;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.scriptbuilder.Analizer;

public class ColDecimal implements Command {

	protected static final Logger log = Logger.getLogger(ColDecimal.class.getSimpleName());

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		
		String[] parameters = content.split(Analizer.parameter_separator);

		Integer decimals = null;
		if (parameters.length > 1 && StringUtils.isNotEmpty(parameters[1])) {
			decimals = new Integer(parameters[1]);
		}
		
		Row row = (Row) object;

		if (parameters.length > 0 && !StringUtils.isEmpty(parameters[0])) {
			Integer i = new Integer(parameters[0]);
			Cell cell = row.getCell(i);
			
			if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC) {
				double value = cell.getNumericCellValue();
				BigDecimal bd = new BigDecimal(value);
				if (decimals != null) {
					bd = bd.setScale(decimals, RoundingMode.HALF_UP);
				}
				log.log(Level.INFO, "Cell: " + value);
				return "" + bd.toString();
			}
			
		}
		
		return "";
		
	}
	
}
