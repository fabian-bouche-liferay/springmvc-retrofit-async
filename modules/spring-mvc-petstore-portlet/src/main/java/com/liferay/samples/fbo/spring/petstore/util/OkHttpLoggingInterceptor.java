package com.liferay.samples.fbo.spring.petstore.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpLoggingInterceptor implements Interceptor {

	private static Log LOG = LogFactoryUtil.getLog(OkHttpLoggingInterceptor.class);
	
	@Override
	public Response intercept(Chain chain) throws IOException {
	    
		Request request = chain.request();

	    long start = System.nanoTime();
	    LOG.debug("OkHttp " + String.format("Sending request %s on %s%n%s",
	        request.url(), chain.connection(), request.headers()));

	    Response response;
		try {
			response = chain.proceed(request);
		    long end = System.nanoTime();
		    LOG.debug("OkHttp " + String.format("Received response for %s in %.1fms%n%s",
		        response.request().url(), (end - start) / 1e6d, response.headers()));
		} catch (IOException e) {
		    LOG.debug("OkHttp " + String.format("Response for %s timed out",
			        request.url()));
		    throw e;
		}

	    return response;
	}
	
}