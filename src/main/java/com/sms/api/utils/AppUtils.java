package com.sms.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.SecureRandom;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sms.api.common.ApplicationContextHolder;
import com.sms.api.dao.ErrorMessageDao;
import com.sms.api.model.ErrorMessage;
import com.sms.api.model.MapErrorMsg;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AppUtils {

	private static final Logger logger = Logger.getLogger(AppUtils.class);
	
	public String generateToken() {
		String token = "";
		try {
			String id = UUID.randomUUID().toString().replace("-", "");
	        token = Jwts.builder()
	            .setId(id)
	            /*.setSubject(user.getUsername())
	            .claim("role", user.getRoleJson())
	            .claim("firstName", user.getFirstName())
	            .claim("lastName", user.getLastName())
	            .setIssuedAt(now)
	            .setNotBefore(now)*/
	            .setExpiration(new Date(System.currentTimeMillis() + (60000 * 1)))
	            .signWith(SignatureAlgorithm.HS256, Constants.BASE64SECRETBYTES)
/*	            .claim("sys", user.getSystemConfig())
	            .claim("right", user.getPrivilegeMsts())*/
	            .compact();
	        
        } catch(Exception ex){
        	logger.error(ex);
        	throw ex;
        }
        return token;
    }

	public void verifyToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(Constants.BASE64SECRETBYTES)
            .parseClaimsJws(token).getBody();
        logger.info("ID: " + claims.getId());
        logger.info("Subject: " + claims.getSubject());
        logger.info("role: " + claims.get("role"));
        logger.info("Issuer: " + claims.getIssuer());
        logger.info("Expiration: " + claims.getExpiration());
    }
	
	public String checkNull(Object obj){
		if(obj == null){
			return "";
		}else{
			return obj.toString();
		}
	}
	
	public HttpsUrlConnectionMessageSender connectSSLWebService(String keyStore, String keyStorePassword, String trustStore, String trustStorePassword, String SSLcontext){
		HttpsUrlConnectionMessageSender messageSender = null;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
	        
	        File pKeyFile = new File(keyStore);
	        InputStream keyInput = new FileInputStream(pKeyFile);
	        ks.load(keyInput, keyStorePassword.toCharArray());
	        logger.info("Loaded keystore: " + keyInput);
	        
	        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        keyManagerFactory.init(ks, keyStorePassword.toCharArray());
	
	        KeyStore ts = KeyStore.getInstance("JKS");
	        File tKeyFile = new File(trustStore);
	        InputStream tKeyInput = new FileInputStream(tKeyFile);
	        ts.load(tKeyInput, trustStorePassword.toCharArray());
	        logger.info("Loaded trustStore: " + tKeyInput);
	       
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        trustManagerFactory.init(ts);
	
	        messageSender = new HttpsUrlConnectionMessageSender();
	        messageSender.setKeyManagers(keyManagerFactory.getKeyManagers());
	        messageSender.setTrustManagers(trustManagerFactory.getTrustManagers());
	
	        SSLContext context = SSLContext.getInstance(SSLcontext);
	        context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
	        SSLSocketFactory sockFact = context.getSocketFactory();
	        messageSender.setSslSocketFactory(sockFact);
	        /*messageSender.setHostnameVerifier((hostname, sslSession) -> {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        });*/
		} catch(Exception ex){
			logger.error(ex);
		}
		return messageSender;
	}
	
	
	public static void disableSslVerification() {
        try {
            /** Create a trust manager that does not validate certificate chains
             * 
             */
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            	@Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

            	@Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

            	@Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            /**
             *  Install the all-trusting trust manager
             */
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            /**
             *  Create all-trusting host name verifier
             */
            HostnameVerifier allHostsValid = new HostnameVerifier() {
            	@Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            /**
             *  Install the all-trusting host verifier
             */
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException ex) {
	        logger.error(ex);
        } catch (KeyManagementException ex) {
	        logger.error(ex);
        }
    }
	
	public SSLContext connectSSLRestService(String keyStore, String keyStorePassword, String trustStore, String trustStorePassword, String SSLcontext){
		SSLContext context = null;
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
	        
	        File pKeyFile = new File(keyStore);
	        InputStream keyInput = new FileInputStream(pKeyFile);
	        ks.load(keyInput, keyStorePassword.toCharArray());
	        logger.info("Loaded keystore: " + keyInput);
	        
	        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        keyManagerFactory.init(ks, keyStorePassword.toCharArray());
	
	        KeyStore ts = KeyStore.getInstance("JKS");
	        File tKeyFile = new File(trustStore);
	        InputStream tKeyInput = new FileInputStream(tKeyFile);
	        ts.load(tKeyInput, trustStorePassword.toCharArray());
	        logger.info("Loaded trustStore: " + tKeyInput);
	       
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        trustManagerFactory.init(ts);
	
	       /* messageSender = new HttpsUrlConnectionMessageSender();
	        messageSender.setKeyManagers(keyManagerFactory.getKeyManagers());
	        messageSender.setTrustManagers(trustManagerFactory.getTrustManagers());*/
	
	        context = SSLContext.getInstance(SSLcontext);
	        context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
	       
		} catch(Exception ex){
			logger.error(ex);
		}
		return context;
	}
	
	public void jsonResponse(HttpServletResponse response, boolean success, String errorCode){
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setStatus(HttpStatus.OK.value());
		Map<String, Object> obj = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setDateFormat(dateFormat);
		PrintWriter out;
		try {
			out = response.getWriter();
			if (success) {
				obj.put("success", success);
				obj.put("message", errorCode);
			} else {
				obj.put("success", success);
				ErrorMessage error = null;
				if(this.isInteger(errorCode)){
					ErrorMessageDao errorMessageDao = ApplicationContextHolder.getContext().getBean(ErrorMessageDao.class);
					error = errorMessageDao.getErrorMsgByCode(errorCode);
				}else{
					error = new ErrorMessage();
					error.setErrorDescription(errorCode);
				}
				if(error != null){
					obj.put("message", error.getErrorDescription());
				}else{
					obj.put("message", errorCode);
				}
			}
			out.println(mapper.writeValueAsString(obj));
			out.close();
		} catch (IOException e1) {
			logger.error("outputFormJson-IOException => " + e1);
		}
	}
	
	public void jsonResponse(HttpServletResponse response, boolean success, String errorCode, MapErrorMsg mapError){
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setStatus(HttpStatus.OK.value());
		Map<String, Object> obj = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setDateFormat(dateFormat);
		PrintWriter out;
		try {
			out = response.getWriter();
			if (success) {
				obj.put("success", success);
				obj.put("message", errorCode);
			} else {
				obj.put("success", success);
				ErrorMessageDao errorMessageDao = ApplicationContextHolder.getContext().getBean(ErrorMessageDao.class);
				ErrorMessage error = errorMessageDao.getErrorMsgByCode(errorCode);
				if(error != null){
					String msg = error.getErrorDescription();
					msg = msg.replaceAll(mapError.getVariable(), (mapError.getText() == null) ? "" : mapError.getText());
					obj.put("message", msg);
				}else{
					obj.put("message", errorCode);
				}
			}
			out.println(mapper.writeValueAsString(obj));
			out.close();
		} catch (IOException e1) {
			logger.error("outputFormJson-IOException => " + e1);
		}
	}
	
	public void jsonResponse(HttpServletResponse response, boolean success, String errorCode, List<MapErrorMsg> mapError){
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setStatus(HttpStatus.OK.value());
		Map<String, Object> obj = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setDateFormat(dateFormat);
		PrintWriter out;
		try {
			out = response.getWriter();
			if (success) {
				obj.put("success", success);
				obj.put("message", errorCode);
			} else {
				obj.put("success", success);
				ErrorMessageDao errorMessageDao = ApplicationContextHolder.getContext().getBean(ErrorMessageDao.class);
				ErrorMessage error = errorMessageDao.getErrorMsgByCode(errorCode);
				if(error != null){
					String msg = error.getErrorDescription();
					for(MapErrorMsg err : mapError){
						msg = msg.replaceAll(err.getVariable(), (err.getText() == null) ? "" : err.getText());
					}
					obj.put("message", msg);
				}else{
					obj.put("message", errorCode);
				}
			}
			out.println(mapper.writeValueAsString(obj));
			out.close();
		} catch (IOException e1) {
			logger.error("outputFormJson-IOException => " + e1);
		}
	}
	
	public boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	public String getErrorMsgWs(String errorCode, String errorMsg){
		return "[" + errorCode + "] " + errorMsg;	
	}
	
	public String getErrorMsgWs(Integer errorCode, String errorMsg){
		return "[" + errorCode + "] " + errorMsg;	
	}
	
	public static boolean validateNormalCharactersAndNumberOnly(String str) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
		 
	      Matcher matcher = pattern.matcher(str);
	 
	      if (!matcher.matches()) {
	           //logger.info("string '"+str + "' contains special character");
	           return matcher.matches();
	      } else {
	           //logger.info("string '"+str + "' doesn't contains special character");
	           return matcher.matches();
	      }
	}
	
	public static boolean validateSpecialCharacter(String str) {
		if(StringUtils.isEmpty(str)){
			return true;
		}else if(str.indexOf("\"") > -1){
			return false;
		}else if(str.indexOf("'") > -1){
			return false;
		}else if(str.indexOf("&") > -1){
			return false;
		}else if(str.indexOf("<") > -1){
			return false;
		}else if(str.indexOf(">") > -1){
			return false;
		}
		return true;
	}	
}
