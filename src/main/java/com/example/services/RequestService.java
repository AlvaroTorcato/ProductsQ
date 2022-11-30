package com.example.services;

import com.example.model.ProductAPOD;
import com.example.model.ProductDetailsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class RequestService {
    String baseURL="http://localhost:8082/";
    private HttpURLConnection openConn(String Url) throws IOException {

        URL url = new URL(Url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");

        return connection;
    }

    public ProductDetailsDTO retrieveProductFromApi(String sku) throws IOException {
        String baseUrl = baseURL+"products/"+sku;
        ProductDetailsDTO product=null;
        try {
            InputStream responseStream = openConn(baseUrl).getInputStream();

            ObjectMapper mapper = new ObjectMapper();

            ProductAPOD apod = mapper.readValue(responseStream, ProductAPOD.class);
            product= new ProductDetailsDTO(apod.sku, apod.name, apod.description);
        } catch (IOException e) {
            System.out.println(e);
        }
        return product;
    }
}
