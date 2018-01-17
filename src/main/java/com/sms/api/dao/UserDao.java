package com.sms.api.dao;

import com.sms.api.model.User;

public interface UserDao {

	public User checkLogin(User user);
	public User checkValidateToken(String tokenId);
	public void updateTokenLogin(User user);
}
