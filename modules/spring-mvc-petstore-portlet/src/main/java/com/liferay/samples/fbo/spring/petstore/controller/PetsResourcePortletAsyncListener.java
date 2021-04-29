package com.liferay.samples.fbo.spring.petstore.controller;

import java.io.IOException;

import javax.portlet.PortletAsyncEvent;
import javax.portlet.PortletAsyncListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetsResourcePortletAsyncListener implements PortletAsyncListener {

	private static final Logger LOG = LoggerFactory.getLogger(PetsResourcePortletAsyncListener.class);
	
	@Override
	public void onComplete(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async complete");
	}

	@Override
	public void onError(PortletAsyncEvent evt) throws IOException {
		LOG.error("Async error");
	}

	@Override
	public void onStartAsync(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async start");
	}

	@Override
	public void onTimeout(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async timeout");
		evt.getPortletAsyncContext().complete();		
	}
	

}
