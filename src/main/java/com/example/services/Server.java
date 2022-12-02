package com.example.services;

import com.example.model.ProductDTO;
import com.example.model.ProductDetailsDTO;
import com.example.repositories.ProductRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Server {
	@Autowired
	ProductRepository repository;

	@RabbitListener(queues = "${queue.name}")
	public boolean findProduct(String sku) {
		System.out.println("Received request for " + sku);
		boolean n = true;
		ProductDetailsDTO productDTO = repository.findProductBySku(sku);
		if (productDTO == null){
			n = false;
		}

		System.out.println("Returned " + n);

		return n;
	}


}
