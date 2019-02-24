package se.gurey.hemera4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;

public class Hemera {

	private Connection nc;
	private ObjectMapper objectMapper;

	public Hemera(Connection nc, ObjectMapper objectMapper) {
		this.nc = nc;
		this.objectMapper = objectMapper;
		
	}

	public <T> void add(String topic, String cmd, Class<T> clazz, Consumer<? super T> consumer) {
		Dispatcher dispatcher = this.nc.createDispatcher(msg -> {
			byte[] data = msg.getData();
			try {
				consumer.accept(objectMapper.readValue(data, clazz));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		dispatcher.subscribe(topic + "." + cmd, "queue");
	}

	public <T> void act(String topic, String cmd, T value) {
		try {
			this.nc.publish(topic+"."+cmd, objectMapper.writeValueAsBytes(value));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
