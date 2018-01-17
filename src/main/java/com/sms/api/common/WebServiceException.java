package com.sms.api.common;

import java.io.Serializable;

public class WebServiceException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8057862502966291961L;

	public WebServiceException() {
        super();
    }
	
	public WebServiceException(String message) {
	    super(message);
	}
}
