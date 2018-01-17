package com.sms.api.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sms.api.dao.UserDao;
import com.sms.api.model.User;
import com.sms.api.utils.AppUtils;

@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AppUtils app;

	@Override
	public User checkLogin(User user) {
		User result = null;
		try {
			result = userDao.checkLogin(user);
			result.setTokenId(app.generateToken());
			userDao.updateTokenLogin(result);
		}catch(Exception ex){
			logger.error(ex);
			throw ex;
		}
		return result;
	}

	@Override
	public User checkValidateToken(String tokenId) {
		User result = null;
		try {
			result = userDao.checkValidateToken(tokenId);
		}catch(Exception ex){
			logger.error(ex);
			throw ex;
		}
		return result;
	}

}
