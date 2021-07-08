package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.portlet.PortletAsyncContext;

import io.swagger.client.model.Pet;

public class PetsResourceRequestRunnable implements Runnable {
	
	private static Log LOG = LogFactoryUtil.getLog(PetsResourceRequestRunnable.class);

	private PortletAsyncContext _portletAsyncContext;
	private CompletableFuture<List<Pet>> _availablePetsFuture;
	private CompletableFuture<List<Pet>> _pendingPetsFuture;
	private CompletableFuture<List<Pet>> _soldPetsFuture;
	
	private boolean _gotAvailablePets = false;
	private boolean _gotPendingPets = false;
	private boolean _gotSoldPets = false;
	
	private boolean _isTerminated = false;
	
	public PetsResourceRequestRunnable(PortletAsyncContext portletAsyncContext, CompletableFuture<List<Pet>> availablePetsFuture, CompletableFuture<List<Pet>> pendingPetsFuture, CompletableFuture<List<Pet>> soldPetsFuture) {
		this._portletAsyncContext = portletAsyncContext;
		this._availablePetsFuture = availablePetsFuture;
		this._pendingPetsFuture = pendingPetsFuture;
		this._soldPetsFuture = soldPetsFuture;
	}

	public void terminate() {
		
		LOG.debug("Request to terminate");
		
		this._isTerminated = true;

		// Completable Futures can be canceled!
		_availablePetsFuture.cancel(true);
		_pendingPetsFuture.cancel(true);
		_soldPetsFuture.cancel(true);

		// If the Completable Future has already completed, cancel does nothing 
		
	}

	@Override
	public void run() {

		// We're going to run all 3 requests in parallel
		getPetsFromApi(this._availablePetsFuture, new MarkAvailablePets(), "available");
		getPetsFromApi(this._pendingPetsFuture, new MarkPendingPets(), "pending");
		getPetsFromApi(this._soldPetsFuture, new MarkSoldPets(), "sold");
		
	}

	public void getPetsFromApi(CompletableFuture<List<Pet>> future, MarkPets callback, String status) {

		// Look closely at the logs, you'll see the order in which they display depends on execution
		LOG.debug("Running petstore " + status + " request");

		future.exceptionally(exception -> {
			
			// In case of an error, I make the future return an empty list instead of the response
			LOG.error("Failed to get " + status + " Pet", exception);
			return new ArrayList<>();
		
		}).thenAcceptAsync(list -> {

			if(_isTerminated) {

				LOG.debug("Don't write anything, the task was terminated");
				
			} else {

				// The task is still running, I'm printing stuff to the output
				writePets(status, list);

			}
			
		}).whenComplete((i, t) -> {
			
			if(_isTerminated) {

				LOG.debug("Don't do anything else, the task was terminated");
				
			} else {

				// This subtask is complete
				callback.done();
				
				// If all 3 tasks are complete, we complete the asyncContext which is going to close the output's writer
				if(allDone()) {
					_portletAsyncContext.complete();
				}

			}

		});

	}
	
	private void writePets(String status, List<Pet> list) {

		try {
			
			LOG.debug("Writing petstore " + status + " response");
			
			Writer writer = _portletAsyncContext.getResourceResponse().getWriter();
			StringBuilder sb = new StringBuilder();
			sb.append("<h2>First 10 ").append(status).append(" Pets</h2>");
			sb.append("<ul>");
			list.stream().limit(10).forEach(item -> {
				sb.append("<li>").append(item.getName()).append("</li>");
			});
			sb.append("</ul>");
			writer.write(sb.toString());

			LOG.debug("Done writing petstore " + status + " response");

		} catch (IllegalStateException | IOException e) {
			LOG.error("Exception writing async response", e);
		}
		
	}
		
	private boolean allDone() {
		return _gotAvailablePets && _gotPendingPets && _gotSoldPets; 
	}
	
	public boolean is_gotAvailablePets() {
		return _gotAvailablePets;
	}

	public void set_gotAvailablePets(boolean _gotAvailablePets) {
		this._gotAvailablePets = _gotAvailablePets;
	}

	public boolean is_gotPendingPets() {
		return _gotPendingPets;
	}

	public void set_gotPendingPets(boolean _gotPendingPets) {
		this._gotPendingPets = _gotPendingPets;
	}

	public boolean is_gotSoldPets() {
		return _gotSoldPets;
	}

	public void set_gotSoldPets(boolean _gotSoldPets) {
		this._gotSoldPets = _gotSoldPets;
	}

	private interface MarkPets {
		public void done(); 
	}
	
	private class MarkAvailablePets implements MarkPets {
		
		public void done() {
			set_gotAvailablePets(true);
		}
		
	}

	private class MarkPendingPets implements MarkPets {
		
		public void done() {
			set_gotPendingPets(true);
		}
		
	}

	private class MarkSoldPets implements MarkPets {
		
		public void done() {
			set_gotSoldPets(true);
		}
		
	}
	
}
