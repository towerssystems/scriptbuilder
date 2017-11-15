package com.scriptbuilder.command;


public interface Command {

	String validate(String content, String[] params);
	String execute(String content, String[] params, Object object);
	
}
