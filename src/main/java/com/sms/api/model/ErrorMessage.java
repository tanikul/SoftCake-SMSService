package com.sms.api.model;

import java.io.Serializable;

public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = -5965865437676529271L;
	
	private String errorCode;
	private String errorDescription;
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
		
}
