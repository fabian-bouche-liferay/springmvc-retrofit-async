package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.samples.fbo.spring.petstore.service.PetService;
import com.liferay.samples.fbo.spring.petstore.util.Constants;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.portlet.PortletAsyncContext;
import javax.portlet.ResourceRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.client.model.Pet;

@Controller
@RequestMapping("VIEW")
public class PetsResourceController {

	@Autowired
	private PetService _petService;
	
	private static Log LOG = LogFactoryUtil.getLog(PetsResourceController.class);
	
	@ResourceMapping("pets")
	public void getPets(ResourceRequest resourceRequest) {

		LOG.debug("Handling pets resource request");
		
		// Start an async context
		PortletAsyncContext portletAsyncContext = resourceRequest.startPortletAsync();

		// Request completable futures for 3 API calls
		CompletableFuture<List<Pet>> availablePetsfuture = this._petService.getPets("available");
		CompletableFuture<List<Pet>> pendingPetsfuture = this._petService.getPets("pending");
		CompletableFuture<List<Pet>> soldPetsfuture = this._petService.getPets("sold");

		// Create a runnable responsible for the management of the async process
		// It implements Runnable
		PetsResourceRequestRunnable task = new PetsResourceRequestRunnable(portletAsyncContext, availablePetsfuture, pendingPetsfuture, soldPetsfuture);

		// Setting timeout to the async context
		portletAsyncContext.setTimeout(Constants.ASYNC_TIMEOUT);
		
		// This listener handles events related to that async context (timeout, completion...)
		portletAsyncContext.addListener(new PetsResourcePortletAsyncListener(task));
		
		// The initial ServletRequest thread will be released and the response will be managed by another thread as the context completes
		portletAsyncContext.start(task);
		
	}

}
