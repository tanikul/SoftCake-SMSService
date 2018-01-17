package com.sms.api.utils;

import java.security.Key;
import java.util.Base64;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class Constants {
	
	public static final String SUCCESS = "1";
	public static final Key SECRET = MacProvider.generateKey(SignatureAlgorithm.HS256);
	private static final byte[] SECRETBYTES = SECRET.getEncoded();
	public static final String BASE64SECRETBYTES = Base64.getEncoder().encodeToString(getSecretBytes());
	
	private Constants() {
	}
	
	public static byte[] getSecretBytes() {
	    return SECRETBYTES;
	}
	
	
}
