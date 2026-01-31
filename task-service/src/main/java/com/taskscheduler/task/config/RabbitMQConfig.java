package com.taskscheduler.task.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String TASK_EXCHANGE = "task.exchange";
    public static final String TASK_QUEUE = "task.queue";
    public static final String TASK_ROUTING_KEY = "task.routing.key";
    
    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(TASK_EXCHANGE);
    }
    
    @Bean
    public Queue taskQueue() {
        return new Queue(TASK_QUEUE, true);
    }
    
    @Bean
    public Binding taskBinding() {
        return BindingBuilder
                .bind(taskQueue())
                .to(taskExchange())
                .with(TASK_ROUTING_KEY);
    }
}
