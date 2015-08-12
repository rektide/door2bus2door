package com.voodoowarez.door2bus2door.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.voodoowarez.door2bus2door.CapitalizedNamingStrategy;
import com.voodoowarez.door2bus2door.model.BusPosition;
import com.voodoowarez.door2bus2door.model.BusPositions;
import com.voodoowarez.door2bus2door.model.WmataBusPositionMixin;
import com.voodoowarez.door2bus2door.model.WritableBusPosition;


public class PollingReceiver extends Receiver<BusPosition> {

	static private ScheduledExecutorService scheduledExecutorService;
	static private ObjectMapper objectMapper;
	static {
		PollingReceiver.scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
		PollingReceiver.objectMapper = new ObjectMapper();
		PollingReceiver.objectMapper.setPropertyNamingStrategy(new CapitalizedNamingStrategy());
		PollingReceiver.objectMapper.addMixIn(BusPosition.class, WmataBusPositionMixin.class);		
		PollingReceiver.objectMapper.addMixIn(WritableBusPosition.class, WmataBusPositionMixin.class);		
		PollingReceiver.objectMapper.registerModule(new MrBeanModule());
		PollingReceiver.objectMapper.registerModule(new AfterburnerModule());
	}
	
	// Configuration state
	private String baseUrl = "https://api.wmata.com/Bus.svc/json/jBusPositions";
	private String routeId;
	private String extraParams;

	// Computed state
	private CloseableHttpAsyncClient httpClient;
	private HttpGet httpRequest;
	private ScheduledFuture scheduledPoll;

	public PollingReceiver(final String routeId, final StorageLevel storageLevel, final CloseableHttpAsyncClient httpClient){
		super(storageLevel != null ? storageLevel : StorageLevel.MEMORY_ONLY());
		this.routeId = routeId;
		this.httpClient = httpClient != null ? httpClient : HttpAsyncClients.createDefault();
	}

	public void onStart(){
		this.httpClient.start();
		this.httpRequest = new HttpGet(this.baseUrl + "?RouteID=" + routeId + (this.extraParams != null ? this.extraParams : ""));
		this.scheduledPoll = PollingReceiver.scheduledExecutorService.scheduleAtFixedRate(() -> {
			doQuery().thenAcceptAsync(new Consumer<List<? extends BusPosition>>() {
				@Override
				public void accept(List<? extends BusPosition> busPositions) {
					for(BusPosition position : busPositions){
						store(position);
					}
				}
			});
		}, 0, 2, TimeUnit.MINUTES);
	}
 
 	public void onStop(){
 		this.scheduledPoll.cancel(false);
 		try {
			this.httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 		
	}

 	public CompletableFuture<List<? extends BusPosition>> doQuery(){
 		final CompletableFuture<List<? extends BusPosition>> completion = new CompletableFuture<List<? extends BusPosition>>();
 		this.httpClient.execute(this.httpRequest, new FutureCallback<HttpResponse>(){
			public void completed(HttpResponse response) {
				try {
					final InputStream content = response.getEntity().getContent();
					final BusPositions busPositions = PollingReceiver.objectMapper.readValue(content, BusPositions.class);
					completion.complete(busPositions.getPositions());
				}catch(Exception ex){
					completion.completeExceptionally(ex);
				}
			}
			public void cancelled() {
				completion.completeExceptionally(new CancellationException());
			}
			public void failed(Exception ex) {
				completion.completeExceptionally(ex);
			}
 			
 		});
 		return completion;
 	}
}


