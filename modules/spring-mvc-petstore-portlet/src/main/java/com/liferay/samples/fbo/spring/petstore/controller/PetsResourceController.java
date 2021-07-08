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
		
		PortletAsyncContext portletAsyncContext = resourceRequest.startPortletAsync();

		CompletableFuture<List<Pet>> availablePetsfuture = this._petService.getPets("available");
		CompletableFuture<List<Pet>> pendingPetsfuture = this._petService.getPets("pending");
		CompletableFuture<List<Pet>> soldPetsfuture = this._petService.getPets("sold");

		PetsResourceRequestRunnable task = new PetsResourceRequestRunnable(portletAsyncContext, availablePetsfuture, pendingPetsfuture, soldPetsfuture);

		portletAsyncContext.setTimeout(Constants.ASYNC_TIMEOUT);
		portletAsyncContext.addListener(new PetsResourcePortletAsyncListener(task));
		
		portletAsyncContext.start(task);
		
	}

}
