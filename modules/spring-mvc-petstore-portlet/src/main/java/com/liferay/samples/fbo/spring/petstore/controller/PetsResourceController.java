package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.samples.fbo.spring.petstore.service.PetService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.portlet.PortletAsyncContext;
import javax.portlet.ResourceRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.client.model.Pet;

@Controller
@RequestMapping("VIEW")
public class PetsResourceController {

	@Autowired
	private PetService _petService;
	
	private static final Logger LOG = LoggerFactory.getLogger(PetsResourceController.class);
	
	@ResourceMapping("pets")
	public void getPets(ResourceRequest resourceRequest) {
		
		PortletAsyncContext portletAsyncContext = resourceRequest.startPortletAsync();

		portletAsyncContext.setTimeout(10000);
		portletAsyncContext.addListener(new PetsResourcePortletAsyncListener());
		
		CompletableFuture<List<Pet>> future = this._petService.getPets();
		
		portletAsyncContext.start(new PetsResourceRequestRunnable(portletAsyncContext, future));
		
	}

}
