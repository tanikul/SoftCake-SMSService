package com.sms.api.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

import com.sms.api.model.ErrorMessage;
import com.sms.api.utils.DBConstants;

@Repository("errorMessageDao") 
public class ErrorMessageDaoImpl implements ErrorMessageDao {

	private JdbcTemplate jdbcTemplate; 
	private static final Logger logger = Logger.getLogger(ErrorMessageDaoImpl.class);
	
	@Autowired 
    @Qualifier("transactionManager") 
    private PlatformTransactionManager transactionManager;
	
	@Autowired
	private void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
	}
	
	@Override
	public ErrorMessage getErrorMsgByCode(String errorCode) {
		ErrorMessage result = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT ERROR_CODE, ERROR_DESCRIPTION FROM ").append(DBConstants.ERROR_MESSAGE).append(" WHERE ERROR_CODE = ? ");
			result = jdbcTemplate.queryForObject(sql.toString(), new Object[] { errorCode }, new RowMapper<ErrorMessage>() {
			 
				@Override
				public ErrorMessage mapRow(ResultSet rs, int numRow) throws SQLException {
					ErrorMessage error = new ErrorMessage();
					error.setErrorCode(rs.getString("ERROR_CODE"));
					error.setErrorDescription(rs.getString("ERROR_DESCRIPTION"));
					return error;
				}
		    });
		} catch (Exception e) {
    		logger.error(e);
    		throw e;
        }
		return result;
	}

}
