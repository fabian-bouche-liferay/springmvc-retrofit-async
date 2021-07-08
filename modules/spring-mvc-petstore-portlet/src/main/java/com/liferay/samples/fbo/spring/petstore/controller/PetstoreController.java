package com.liferay.samples.fbo.spring.petstore.controller;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import javax.portlet.PortletPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("VIEW")
public class PetstoreController {

	private static Log LOG = LogFactoryUtil.getLog(PetstoreController.class);

	@RenderMapping
	public String prepareView(PortletPreferences portletPreferences) {

		return "petstore";
	}
	
}
