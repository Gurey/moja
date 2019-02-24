package se.gurey.hemera4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
		ObjectMapper jsonMapper = new ObjectMapper();
		Connection nc = Nats.connect();
		Dispatcher dispatcher = nc.createDispatcher(msg -> {
				System.out.println(msg.getSubject());
				try {
					System.out.println(jsonMapper.readValue(msg.getData(), JsonNode.class));
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
		dispatcher.subscribe(">");
		Hemera hemera = new Hemera(nc, objectMapper);
		hemera.add("topic", "cmd", Add.class, t -> {
			System.out.println(t.getA() + t.getB());
		});
		
		AtomicInteger inc = new AtomicInteger();
		// hemera.act("topic", "cmd", new Add(inc.getAndIncrement(), inc.getAndIncrement()));
	}

}
