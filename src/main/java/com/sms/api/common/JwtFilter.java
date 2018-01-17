package com.sms.api.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import com.sms.api.dao.UserDao;
import com.sms.api.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtFilter extends GenericFilterBean {

	private static final String AUTHORIZATION = "Authorization";
	private static final Logger logger = Logger.getLogger(JwtFilter.class);
	
    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
    	
    	final HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest) req) {
            @Override
            public StringBuffer getRequestURL() {
                return new StringBuffer(decodeURL(((HttpServletRequest) getRequest()).getRequestURL().toString()));
            }
            @Override
            public String getPathInfo() {
                return decodeURL(((HttpServletRequest) getRequest()).getPathInfo());
            }
            @Override
            public String getRequestURI() {
            	return decodeURL(((HttpServletRequest) getRequest()).getRequestURI());
            }
            @Override
            public String getMethod() {
                return ((HttpServletRequest) getRequest()).getMethod();
            }
        };
        
        String authHeader = null;
        if(request.getHeader(AUTHORIZATION) != null){
        	authHeader = request.getHeader(AUTHORIZATION);
        }else if(request.getParameter(AUTHORIZATION) != null){
        	authHeader = request.getParameter(AUTHORIZATION);
        }
        if (authHeader == null) {
        	if("OPTIONS".equals(request.getMethod())){
        		HttpServletResponse response = (HttpServletResponse) res;
        	    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        	    response.setHeader("Access-Control-Allow-Credentials", "true");
        	    response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        	    response.setHeader("Access-Control-Max-Age", "3600");
        	    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, Authorization, programid");
        	    chain.doFilter(req, res);
        	    return;
        	}
        	throw new ServletException("Missing or invalid Authorization header.");
        }

        final String token = authHeader;
    	Claims claims = Jwts.claims();
        UserDao userDao = ApplicationContextHolder.getContext().getBean(UserDao.class);
        User user =  userDao.checkValidateToken(token);
        if(user == null){
    		throw new ServletException("InvalidAuthorizationToken");
    	}
        request.setAttribute("claims", claims);
        chain.doFilter(request, res);
    }
    
    private String decodeURL(String path){
    	String result = "";
    	try {
			String[] arrPath = path.split("/");
			for(String str : arrPath){
				if(!"".equals(str)){
					if(str.contains("?")){
						String param = str.substring(str.indexOf("?") + 1, str.length());
						String[] params = param.split("&");
						result +=  "/" + URLDecoder.decode(str.substring(0, str.indexOf("?")), "UTF-8") + "?";
						for(String p : params){
							String key = p.substring(0, p.indexOf("=") + 1);
							String value = p.substring(p.indexOf("=") + 1, p.length());
							result += key + URLDecoder.decode(value, "UTF-8") + "&";
						}
						result = result.substring(0, result.length() - 1);
					}else{
						result += "/" + URLDecoder.decode(str, "UTF-8");
					}
				}
			}
    	} catch (UnsupportedEncodingException e) {
    		logger.error(e);
    	}	
		return result;
	}
}