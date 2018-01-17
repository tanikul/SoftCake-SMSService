package com.sms.api.service;

import com.sms.api.model.User;

public interface UserService {

	public User checkLogin(User user);
	public User checkValidateToken(String tokenId);
}
