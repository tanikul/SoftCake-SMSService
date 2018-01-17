package com.sms.api.model;

import java.io.Serializable;

public class MapErrorMsg implements Serializable {

	private static final long serialVersionUID = 1886270552813664684L;
	
	private String variable;
	private String text;
	
	public MapErrorMsg(String variable, String text){
		this.setVariable(variable);
		this.setText(text);
	}
	
	public String getVariable() {
		return variable;
	}
	public void setVariable(String variable) {
		this.variable = variable;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
