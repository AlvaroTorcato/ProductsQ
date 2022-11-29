package com.example.productq.services;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitReciver {

    @RabbitHandler
    public void receive(String in) {
        System.out.println(" [x] Received :'" + in + "'");
    }

}
