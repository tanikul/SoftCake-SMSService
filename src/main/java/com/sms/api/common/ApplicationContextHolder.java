package com.sms.api.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {
	
	private static ApplicationContext context;

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	ApplicationContextHolder.context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return ApplicationContextHolder.context;
    }
}
