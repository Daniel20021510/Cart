package org.macales.cart.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    private static final String QUEUE_NAME = "cartCheckoutQueue";
    private static final String EXCHANGE_NAME = "cartCheckoutExchange";

    @Bean
    public Queue cartCheckoutQueue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    Exchange cartCheckoutExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue cartCheckoutQueue, Exchange cartCheckoutExchange) {
        return BindingBuilder.bind(cartCheckoutQueue).to(cartCheckoutExchange).with("cart.checkout").noargs();
    }
}
