package com.sms.api.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import com.sms.api.mapper.UserRowMapper;
import com.sms.api.model.User;
import com.sms.api.utils.DBConstants;

@Repository("userDaoImpl") 
public class UserDaoImpl implements UserDao {

	private JdbcTemplate jdbcTemplate; 
	private static final Logger logger = Logger.getLogger(UserDaoImpl.class);
	
	@Autowired 
    @Qualifier("transactionManager") 
    private PlatformTransactionManager transactionManager;
	
	@Autowired
	private void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
	}
	
	@Override
	public User checkValidateToken(String tokenId) {
		User result = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT a.*, b.DESCRIPTION PREFIX_NAME FROM ");
			sql.append(DBConstants.USER).append(" a LEFT JOIN ").append(DBConstants.MASTER_SETUP);
			sql.append(" b ON b.GROUP = 'PREFIX' AND a.PREFIX = b.ID WHERE a.TOKEN_ID = ? AND a.ACTIVE_STATUS = 'Y'");
			result = jdbcTemplate.queryForObject(sql.toString(),  new Object[] { tokenId }, new UserRowMapper());
		} catch(EmptyResultDataAccessException e){
			logger.debug(e);
		} catch (Exception e) {
    		logger.error(e);
    		throw e;
        }
		return result;
	}

	@Override
	public User checkLogin(User user) {
		User result = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT a.*, b.DESCRIPTION PREFIX_NAME FROM ");
			sql.append(DBConstants.USER).append(" a LEFT JOIN ").append(DBConstants.MASTER_SETUP);
			sql.append(" b ON b.GROUP = 'PREFIX' AND a.PREFIX = b.ID WHERE a.USER_ID = ? AND a.PASSWORD = ? AND a.ACTIVE_STATUS = 'Y'");
			result = (User) jdbcTemplate.queryForObject(sql.toString(),  new Object[] { user.getUserId(), user.getPassword() }, new UserRowMapper());
		} catch(EmptyResultDataAccessException e){
			logger.debug(e);
		} catch (Exception e) {
    		logger.error(e);
    		throw e;
        }
		return result;
	}
	
	@Override
	public void updateTokenLogin(User user) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE ").append(DBConstants.USER).append(" SET TOKEN_ID = ? WHERE USER_ID = ?");
			jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
		        @Override
		        public void setValues(PreparedStatement ps) throws SQLException {
		        	ps.setString(1, user.getTokenId());
		        	ps.setString(2, user.getUserId());
		        }
		    });
		} catch (Exception e) {
    		logger.error(e);
    		throw e;
        }
	}
}
