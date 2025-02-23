package com.example.Kafka_producer.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaProducerConfig {
    
    public static final String FIRST_TOPIC = "alertas_topic";
    public static final String SECOND_TOPIC = "signosvitales_topic";
    
    @Bean
    List<NewTopic> topics() {
        List<String> topicNames = Arrays.asList(FIRST_TOPIC, SECOND_TOPIC);
        return topicNames.stream()
            .map(topicName -> TopicBuilder.name(topicName).build())
            .collect(Collectors.toList());
    }
}
