package com.sms.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sms.api.model.User;

public class UserRowMapper implements RowMapper<User>  {

	@Override
	public User mapRow(ResultSet rs, int arg1) throws SQLException {
		User user = new User();
		user.setUserId(rs.getString("USER_ID"));
		user.setFirstName(rs.getString("FIRST_NAME"));
		user.setLastName(rs.getString("LAST_NAME"));
		user.setPrefix(rs.getInt("PREFIX"));
		user.setPrefixName(rs.getString("PREFIX_NAME"));
		user.setActiveStatus(rs.getString("ACTIVE_STATUS"));
		user.setLastLoggedOn(rs.getTimestamp("LAST_LOGGED_ON"));
		user.setCreatedBy(rs.getString("CREATED_BY"));
		user.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
		user.setLastUpdateBy(rs.getString("LAST_UPDATED_BY"));
		user.setLastUpdateDate(rs.getTimestamp("LAST_UPDATED_DATE"));
		return user;
	}

}
