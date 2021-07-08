package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.Writer;

import javax.portlet.PortletAsyncEvent;
import javax.portlet.PortletAsyncListener;

public class PetsResourcePortletAsyncListener implements PortletAsyncListener {

	private static Log LOG = LogFactoryUtil.getLog(PetsResourcePortletAsyncListener.class);
	
	private PetsResourceRequestRunnable _task;
	
	public PetsResourcePortletAsyncListener(PetsResourceRequestRunnable task) {
		this._task = task;
	}
	
	// This is executed once everything completes
	@Override
	public void onComplete(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async complete");
		Writer writer = evt.getPortletAsyncContext().getResourceResponse().getWriter();
		writer.close();
	}

	@Override
	public void onError(PortletAsyncEvent evt) throws IOException {
		LOG.error("Async error");
		Writer writer = evt.getPortletAsyncContext().getResourceResponse().getWriter();
		writer.append("<h1 style='color: red'>Error</h1>");
		evt.getPortletAsyncContext().complete();		
	}

	@Override
	public void onStartAsync(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async start");
	}

	// This is executed on async context timeout 
	@Override
	public void onTimeout(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async timeout");
		// We ask the Runnable task to terminate what was still running 
		_task.terminate();
		// We write a message to the output 
		Writer writer = evt.getPortletAsyncContext().getResourceResponse().getWriter();
		writer.append("<h1 style='color: red'>Timeout error</h1>");
		evt.getPortletAsyncContext().complete();		
	}
	

}
