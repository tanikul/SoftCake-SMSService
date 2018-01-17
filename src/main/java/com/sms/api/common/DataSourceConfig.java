package com.sms.api.common;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

@Configuration
public class DataSourceConfig {
	
	private static final Logger logger = Logger.getLogger(DataSourceConfig.class);
	
	@Autowired
	AppProperties prop;
	
    @Bean
    public DataSource dataSource() {
    	DataSource dataSource = null;
    	try {
	        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            dataSource = dataSourceLookup.getDataSource(prop.getJndiName());
    	} catch(Exception e){
    		logger.error(e);
    		throw e;
    	}
    	return dataSource;
    }
}
