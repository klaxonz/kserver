package com.klaxon.kserver.aop;

import javax.websocket.server.ServerEndpointConfig;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

	private static volatile BeanFactory context;

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) {
		return context.getBean(endpointClass);
	}

	@Override
	public void setApplicationContext(@NotNull final ApplicationContext context) throws BeansException {
		CustomSpringConfigurator.context = context;
	}
}