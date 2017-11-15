package com.scriptbuilder.command;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.scriptbuilder.Analizer;

public class Round implements Command {

	protected static final Logger log = Logger.getLogger(Round.class.getSimpleName());

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
		
		if (parameters.length > 0 && !StringUtils.isEmpty(parameters[0])) {
			
			BigDecimal bd = new BigDecimal(parameters[0]);
			if (decimals != null) {
				bd = bd.setScale(decimals, RoundingMode.HALF_UP);
			}
			log.log(Level.INFO, "Cell: " + bd.doubleValue());
			return "" + bd.toString();
			
		}
		
		return "";
		
	}
	
}
