package com.example.productq.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StopWatch;

public class RabbitReciver {

    //@RabbitListener(queues = "#{autoDeleteQueue1.name}")
    @RabbitListener(queues = {"products"})
    public void receive1(String in) throws InterruptedException {
        receive(in, 1);
    }


    public void receive(String in, int receiver) throws InterruptedException {
        StopWatch watch = new StopWatch();
        watch.start();
        System.out.println("instance " + receiver + " [x] Received '" + in + "'");
        watch.stop();
        System.out.println("instance " + receiver + " [x] Done in "
                + watch.getTotalTimeSeconds() + "s");
    }



}
