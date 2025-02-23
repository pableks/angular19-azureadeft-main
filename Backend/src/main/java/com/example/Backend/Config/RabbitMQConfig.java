package com.example.Backend.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String QUEUE_ALERTAS = "alertas.queue";
    public static final String QUEUE_RESUMEN = "resumen.queue";
    public static final String EXCHANGE_NAME = "hospital.exchange";
    public static final String ROUTING_KEY_ALERTAS = "alertas.routing";
    public static final String ROUTING_KEY_RESUMEN = "resumen.routing";

    @Bean
    public Queue alertasQueue() {
        return QueueBuilder.durable(QUEUE_ALERTAS)
                .withArgument("x-message-ttl", 60000) // Messages expire after 1 minute
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "dlq.alertas")
                .build();
    }

    @Bean
    public Queue resumenQueue() {
        return QueueBuilder.durable(QUEUE_RESUMEN)
                .withArgument("x-message-ttl", 300000) // Messages expire after 5 minutes
                .build();
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding alertasBinding(Queue alertasQueue, DirectExchange exchange) {
        return BindingBuilder.bind(alertasQueue)
                .to(exchange)
                .with(ROUTING_KEY_ALERTAS);
    }

    @Bean
    public Binding resumenBinding(Queue resumenQueue, DirectExchange exchange) {
        return BindingBuilder.bind(resumenQueue)
                .to(exchange)
                .with(ROUTING_KEY_RESUMEN);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dlq.alertas").build();
    }
}
