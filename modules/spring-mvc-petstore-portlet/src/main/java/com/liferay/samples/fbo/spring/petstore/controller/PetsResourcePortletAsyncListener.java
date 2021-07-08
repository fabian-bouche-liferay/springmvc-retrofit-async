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

	@Override
	public void onTimeout(PortletAsyncEvent evt) throws IOException {
		LOG.debug("Async timeout");
		_task.terminate();
		Writer writer = evt.getPortletAsyncContext().getResourceResponse().getWriter();
		writer.append("<h1 style='color: red'>Timeout error</h1>");
		evt.getPortletAsyncContext().complete();		
	}
	

}
