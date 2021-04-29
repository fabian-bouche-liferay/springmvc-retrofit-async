package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import javax.portlet.PortletPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("VIEW")
public class PetstoreController {

	private static final Logger LOG = LoggerFactory.getLogger(PetstoreController.class);

	@RenderMapping
	public String prepareView(PortletPreferences portletPreferences) {

		return "petstore";
	}
	
}
