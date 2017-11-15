package com.scriptbuilder.command;


public class Content implements Command {

	@Override
	public String validate(String content, String[] params) {
		return content;
	}

	@Override
	public String execute(String content, String[] params, Object object) {
		if (content.startsWith("\n")) return content.substring("\n".length(), content.length());
		return content;
	}

}
