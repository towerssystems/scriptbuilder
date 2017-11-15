package com.scriptbuilder.command;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.scriptbuilder.Analizer;

public class Multiply implements Command {

	protected static final Logger log = Logger.getLogger(Multiply.class.getSimpleName());

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		
		String[] parameters = content.split(Analizer.parameter_separator);

		BigDecimal factor = null;
		
		if (parameters.length > 1 && StringUtils.isNotEmpty(parameters[1])) {
			factor = new BigDecimal(parameters[1]);
		}
		
		if (parameters.length > 0 && !StringUtils.isEmpty(parameters[0])) {
			BigDecimal bd = new BigDecimal(parameters[0]);
			if (factor != null) {
				bd = bd.multiply(factor);
			}
			log.log(Level.INFO, "Cell: " + bd);
			return "" + bd.doubleValue();
			
		}
		
		return "";
		
	}
	
}
