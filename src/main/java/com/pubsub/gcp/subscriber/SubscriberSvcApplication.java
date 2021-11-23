package com.pubsub.gcp.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.core.subscriber.PubSubSubscriberTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import com.google.gson.Gson;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.IOException;

@SpringBootApplication
public class SubscriberSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriberSvcApplication.class, args);
	}

	private static final String SUBSCRIPTION = "product-ml";
	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
		@Qualifier("pubsubInputChannel")MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, SUBSCRIPTION);
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}

//	@Bean
//	public PubSubMessageConverter pubSubMessageConverter() {
//		return new JacksonPubSubMessageConverter(new ObjectMapper());
//	}

	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public MessageHandler messageReceiver() {
		return message -> {
			//String value = new Gson().toJson((byte[])message.getPayload(), MessageData.class);
			//pubSubMessageConverter().fromPubSubMessage((PubsubMessage) message.getPayload(), MessageData.class)
			try {
				System.out.println("Message arrived! Payload: " + convert(message.getPayload()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			BasicAcknowledgeablePubsubMessage originalMessage =
					message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
			originalMessage.ack();
		};
	}

	private String convert(Object payload) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue((byte[])payload, MessageData.class).value;
	}


//	@Bean
//	ApplicationRunner subscribeRunner(PubSubSubscriberTemplate subscriberTemplate) {
//		return (args) -> {
//			subscriberTemplate.subscribeAndConvert(SUBSCRIPTION, msg -> {
//				System.out.println(msg.getPayload());
//				msg.ack();
//			}, MessageData.class);
//		};
//	}
}
