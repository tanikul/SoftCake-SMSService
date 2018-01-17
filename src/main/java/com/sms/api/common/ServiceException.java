package com.sms.api.common;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sms.api.model.MapErrorMsg;
import com.sms.api.utils.DBConstants;

public class ServiceException  extends Exception implements Serializable {

	private static final long serialVersionUID = 3927473807823586178L;
	private int errorCode;
	private String errorMessageEn;
	private String errorMessageTh;
	private int errorLineNumber;
	
	public ServiceException() {
        super();
    }
	
	public ServiceException(String message) {
	    super(message);
	    this.setErrorMessageTh(message);
	}
	
	public ServiceException(int errorCode) {
		this.errorCode = errorCode;
		setErrorMessageFromDB();
	}
	
	public ServiceException(String message, int errorCode) {
	    super(message);
	    this.errorCode = errorCode;
	}
	 
	public ServiceException(Exception exception) {
		if(exception instanceof ServiceException){
			this.setErrorMessageTh(exception.getMessage());
		}else if(exception instanceof WebServiceException){
			this.setErrorMessageTh(exception.getMessage());
		}else{
			this.setErrorCode(exception);
			setErrorMessageFromDB();
		}
	}
	
	public ServiceException(List<MapErrorMsg> maps, int errorCode){
		this.errorCode = errorCode;
		setErrorMessageFromDB();
		String msg = this.getErrorMessageTh();
		for(MapErrorMsg error : maps){
			msg = msg.replaceAll(error.getVariable(), (error.getText() == null) ? "" : error.getText());
		}
		this.setErrorMessageTh(msg);
	}
	
	public ServiceException(MapErrorMsg map, int errorCode){
		this.errorCode = errorCode;
		setErrorMessageFromDB();
		String msg = this.getErrorMessageTh();
		msg = msg.replace(map.getVariable(), (map.getText() == null) ? "" : map.getText());
		this.setErrorMessageTh(msg);
	}
	
	private void setErrorMessageFromDB(){
		if(this.getErrorCode() == 0000) return;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ERROR_CODE, ERROR_DESCRIPTION FROM ");
		sql.append(DBConstants.ERROR_MESSAGE);
		sql.append(" WHERE ERROR_CODE = ?");
		DataSourceBean dataSource = ApplicationContextHolder.getContext().getBean(DataSourceBean.class);
		JdbcTemplate jdbcTemplate =  (JdbcTemplate) dataSource.getNamedParameterJdbcTemplate().getJdbcOperations();
		List<ServiceException> obj = jdbcTemplate.query(sql.toString(), new Object[] { this.getErrorCode() }, new RowMapper<ServiceException>() {
			 
            public ServiceException mapRow(ResultSet rs, int rowNum) throws SQLException {
            	ServiceException ex = new ServiceException();
            	ex.setErrorMessageEn(rs.getString("ERROR_MSG_EN"));
            	ex.setErrorMessageTh(rs.getString("ERROR_MSG_TH"));
                return ex;
            }
        });
		if(obj.size() > 0){
			this.setErrorMessageEn(obj.get(0).getErrorMessageEn());
			this.setErrorMessageTh(obj.get(0).getErrorMessageTh());
		}
	}
	

	@Override
	public String getMessage() {
		return this.getErrorMessageTh();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Exception ex) {
		if(ex instanceof NullPointerException){
	    	this.errorCode = 999;
	    } else if(ex instanceof IOException){
			this.errorCode = 999;
		} else if (ex instanceof BadSqlGrammarException){ 
			this.errorCode = 999;
	    } else if (ex instanceof SQLException){
	    	this.errorCode = 999;
	    }  else if(ex instanceof ServletException){
	    	this.errorCode = 999;
	    } else if(ex instanceof JsonProcessingException){
	    	this.errorCode = 999;
	    } else{
	    	this.errorCode = 999;
	    }
	}
	
	public String getErrorMessageEn() {
		return errorMessageEn;
	}

	public void setErrorMessageEn(String errorMessageEn) {
		this.errorMessageEn = errorMessageEn;
	}

	public String getErrorMessageTh() {
		return errorMessageTh;
	}

	public void setErrorMessageTh(String errorMessageTh) {
		this.errorMessageTh = errorMessageTh;
	}

	public int getErrorLineNumber() {
		return errorLineNumber;
	}

	public void setErrorLineNumber(int errorLineNumber) {
		this.errorLineNumber = errorLineNumber;
	}
}