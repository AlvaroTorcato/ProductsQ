package com.example;

import com.example.controller.ClientProduct;
import com.example.model.Product;
import com.example.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
public class ProductQApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductQApplication.class, args);
    }

    @Autowired
    ClientProduct clientProduct;

    @Bean
    public CommandLineRunner loadData(ProductRepository repository) {
        return (args) -> {
            // save a couple of customers
            if(repository.count() == 0){
                List<Product> lista = clientProduct.send();
                if (lista != null){
                    repository.saveAll(lista);
                }
            }
            //repository.save(new Customer("Jack", "Bauer"));
        };

    }

}
