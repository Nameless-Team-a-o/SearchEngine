package com.nameless.storage_server.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    // Define constants for queue, exchange, and routing key
    public static final String QUEUE_NAME = "SubmissionsQueue";
    public static final String EXCHANGE_NAME = "SubmissionsExchange";
    public static final String ROUTING_KEY = "SubmissionsRoutingKey";

    // Define constants for UserQueue, UserExchange, and UserRoutingKey
    public static final String USER_QUEUE_NAME = "UserQueue";
    public static final String USER_EXCHANGE_NAME = "UserExchange";
    public static final String USER_ROUTING_KEY = "UserRoutingKey";

    // Jackson JSON message converter bean
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Define the Submissions Queue
    @Bean
    public Queue submissionQueue() {
        return new Queue(QUEUE_NAME, true); // Durable queue
    }

    // Define the User Queue
    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE_NAME, true); // Durable queue
    }

    // RabbitTemplate bean with message converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    // Define the Submission Exchange
    @Bean
    public DirectExchange submissionExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // Define the User Exchange
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE_NAME);
    }

    // Bind the Submissions Queue to the Submissions Exchange with Routing Key
    @Bean
    public Binding submissionBinding(Queue submissionQueue, DirectExchange submissionExchange) {
        return BindingBuilder.bind(submissionQueue).to(submissionExchange).with(ROUTING_KEY);
    }

    // Bind the User Queue to the User Exchange with Routing Key
    @Bean
    public Binding userBinding(Queue userQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(USER_ROUTING_KEY);
    }
}
