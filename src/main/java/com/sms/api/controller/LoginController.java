package com.sms.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sms.api.common.ServiceException;
import com.sms.api.model.User;
import com.sms.api.service.UserService;
import com.sms.api.utils.AppUtils;
import com.sms.api.utils.Constants;

import io.jsonwebtoken.Jwts;


@RestController
public class LoginController {
	
	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	UserService userService; 
	
	@Autowired
	private AppUtils app;
	
	 @RequestMapping(value = "/test",method = RequestMethod.GET)
	 public String test() throws ServiceException {
		 return "^_^  Welcome to MasterSetup Service !!!!!!!!!!!!! \n";
	 }
	 
	 @RequestMapping(value = "/login", method = RequestMethod.POST)
	 public User login(HttpServletRequest request , @RequestBody final User user) throws ServiceException {
		 User u = null;
		 try{
			u = userService.checkLogin(user);
		 }catch(Exception ex){
			 logger.error(ex);
			 throw new ServiceException(ex);
		 }
        return u;
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logOut(HttpServletRequest request ,@RequestBody final String userId) throws ServletException {
    	try {	
             // this.logOutSystem(request, userId,Constants.ACTION_LOGOUT);
		}catch(Exception ex){
			 logger.error(ex);
		 }
    	return Constants.SUCCESS;
	}
    
    @SuppressWarnings("unused")
    private static class LoginResponse {
        
		private String token;

		public LoginResponse(final String token) {
            this.token = token;
        }
		
        public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
    }
	    
}