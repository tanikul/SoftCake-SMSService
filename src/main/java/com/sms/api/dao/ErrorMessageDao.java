package com.sms.api.dao;

import com.sms.api.model.ErrorMessage;

public interface ErrorMessageDao {

	public ErrorMessage getErrorMsgByCode(String errorCode);
}
