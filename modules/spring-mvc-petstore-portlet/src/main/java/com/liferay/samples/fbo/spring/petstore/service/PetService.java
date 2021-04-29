package com.liferay.samples.fbo.spring.petstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

	private static final String BASE_URL = "https://petstore.swagger.io/v2/";
	
	private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
	
	private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
	
	public CompletableFuture<List<Pet>> getPets() {

		Retrofit retrofit = builder.build();
		PetApi petApi = retrofit.create(PetApi.class);
		
		List<String> statusList = new ArrayList<String>();
		statusList.add("available");
		
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
				future.completeExceptionally(t);
			}

			@Override
			public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {

				if(response.isSuccessful()) {
					future.complete(response.body());
				} else {
					future.completeExceptionally(new PetServiceException("Response got code " + response.code()));
				}
				
			}
			
		});

		return future;

	}
	
}
