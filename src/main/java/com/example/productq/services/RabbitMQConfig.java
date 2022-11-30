package com.example.productq.services;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@Profile({"tut3", "pub-sub", "publish-subscribe"})
@Configuration
@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)
public class RabbitMQConfig {
    @Bean
    public FanoutExchange fanout() {
        return new FanoutExchange("tut.fanout");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public CachingConnectionFactory connectionFactory(){
        return new CachingConnectionFactory("localhost");
    }
    //@Profile("receiver")
    private static class ReceiverConfig {

        @Bean
        public Queue autoDeleteQueue1() {
            return new AnonymousQueue();
        }


        @Bean
        public Binding binding1(FanoutExchange fanout,
                                Queue autoDeleteQueue1) {
            return BindingBuilder.bind(autoDeleteQueue1).to(fanout);
        }

        @Bean
        public RabbitReciver receiver() {
            return new RabbitReciver();
        }
    }

    //@Profile("sender")
    @Bean
    public RabbitSender sender() {
        return new RabbitSender();
    }
}
