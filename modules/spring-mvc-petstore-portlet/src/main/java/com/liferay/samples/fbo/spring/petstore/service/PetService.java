package com.liferay.samples.fbo.spring.petstore.service;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.samples.fbo.spring.petstore.util.Constants;
import com.liferay.samples.fbo.spring.petstore.util.OkHttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import io.swagger.client.api.PetApi;
import io.swagger.client.model.Pet;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class PetService {

	private static Log LOG = LogFactoryUtil.getLog(PetService.class);

	private static final String BASE_URL = "https://petstore.swagger.io/v2/";
	
	private static OkHttpClient client = new OkHttpClient.Builder()
			  .addInterceptor(new OkHttpLoggingInterceptor())
			  .callTimeout(Constants.OKHTTP_TIMEOUT, TimeUnit.MILLISECONDS)
			  .build();
	
	private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client);
	
	public CompletableFuture<List<Pet>> getPets(String status) {

		Retrofit retrofit = builder.build();
		PetApi petApi = retrofit.create(PetApi.class);
		
		List<String> statusList = new ArrayList<String>();
		statusList.add(status);
		
		Call<List<Pet>> asyncCall = petApi.findPetsByStatus(statusList);

		final CompletableFuture<List<Pet>> future = new CompletableFuture<List<Pet>>() {
			@Override public boolean cancel(boolean mayInterruptIfRunning) {
				if (mayInterruptIfRunning) {
					asyncCall.cancel();
				}
				return super.cancel(mayInterruptIfRunning);
			}
		};
		
		asyncCall.enqueue(new Callback<List<Pet>>() {

			@Override
			public void onFailure(Call<List<Pet>> call, Throwable t) {
				
				LOG.debug("Request failed");
				
				future.completeExceptionally(new PetServiceException("Request failed"));
			}

			@Override
			public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {

				if(response.isSuccessful()) {

					LOG.debug("Request succeeded");

					future.complete(response.body());
				} else {

					LOG.debug("Request failed with code " + response.code());

					future.completeExceptionally(new PetServiceException("Response got code " + response.code()));
				}
				
			}
			
		});

		return future;

	}

	
}
