package com.liferay.samples.fbo.spring.petstore.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.portlet.PortletAsyncContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.client.model.Pet;

public class PetsResourceRequestRunnable implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(PetsResourceRequestRunnable.class);

	private PortletAsyncContext _portletAsyncContext;
	private CompletableFuture<List<Pet>> _future;
	
	public PetsResourceRequestRunnable(PortletAsyncContext portletAsyncContext, CompletableFuture<List<Pet>> future) {
		this._portletAsyncContext = portletAsyncContext;
		this._future = future;
	}
	
	@Override
	public void run() {
		
		PortletAsyncContext portletAsyncContext = this._portletAsyncContext;
		
		this._future.exceptionally(exception -> {
			
			LOG.error("Failed to get Pet", exception);
			return new ArrayList<>();
		
		}).thenAcceptAsync(list -> {

			try {
				
				Writer writer = portletAsyncContext.getResourceResponse().getWriter();
				StringBuilder sb = new StringBuilder();
				list.forEach(item -> {
					sb.append(item.getName() + "\n");
				});
				writer.write(sb.toString());
				writer.close();
				
			} catch (IllegalStateException | IOException e) {
				LOG.error("Exception writing async response", e);
			}
			
			portletAsyncContext.complete();
			
		});
		
		
	}

}
