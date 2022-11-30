package com.example.controller;

import com.example.model.ProductDTO;
import com.example.model.ProductDetailsDTO;
import com.example.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Products", description = "Endpoints for getting information about the products")
@RequestMapping("/products")
@RestController
public class ProductController {

    @Autowired
    ProductService service;

    @Operation(summary = "Gets all products")
    @GetMapping
    List<ProductDTO> findAllToCatalog(@RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return service.findAllProductsFromCatalog(pageNo,pageSize);
    }

    @Operation(summary = "Gets the product with the given sku")
    @GetMapping(value = "/{sku}")
    public ProductDetailsDTO findProductBySku(@PathVariable("sku") String sku) throws IOException {
        return service.findProductBySku(sku);
    }

    @Operation(summary = "Gets products that contain the given string")
    @GetMapping(value = "/search/{nameOrSku}")
    public List<ProductDTO> findByNameOrSku(@PathVariable("nameOrSku") String nameOrSku,@RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return service.findByNameOrSku(nameOrSku,pageNo,pageSize);
    }


}
