package com.example.productq.services;


import com.example.productq.controller.FileStorageException;
import com.example.productq.controller.MyFileNotFoundException;
import com.example.productq.model.ProductDetailsDTO;
import com.example.productq.model.UserDetailsDTO;
import com.example.productq.repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Font BARCODE_TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private final Path fileStorageLocation = Path.of(Paths.get("").toAbsolutePath().normalize() + "/images");
    @Autowired
    ProductRepository repository;

    @Autowired
    private ProductService service;

    @Autowired
    public FileStorageService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, String sku, HttpServletRequest request) {
        String jwt = parseJwt(request);
        UserDetailsDTO user = makeRequestToAutentication(jwt);
        if (user.getRoles() != "[MODERADOR]"){
            throw  new ResponseStatusException(HttpStatus.FORBIDDEN, "Can´t be accessed by this user");
        }
        // Normalize file name
        UUID uuid = UUID.randomUUID();
        String fileName = uuid.toString()+".png";

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            service.addImage(fileName,sku);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
    public BufferedImage getBarcode(String sku) throws Exception {
        ProductDetailsDTO product = repository.findProductBySku(sku);
        if (product == null){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found ");
        }
        Barcode barcode = BarcodeFactory.createCode128(sku);
        barcode.setFont(BARCODE_TEXT_FONT);
        barcode.setLabel(sku);
        BufferedImage image = BarcodeImageHandler.getImage(barcode);
        return image;
    }
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    public UserDetailsDTO makeRequestToAutentication(String jwt){
        String urlRequest = "http://localhost:8084/auth/search" + jwt;
        UserDetailsDTO user = null;
        try {
            InputStream responseStream = openConn(urlRequest).getInputStream();

            ObjectMapper mapper = new ObjectMapper();

            user = mapper.readValue(responseStream, UserDetailsDTO.class);
        } catch (IOException e) {
            System.out.println(e);
        }

        return user;
    }
    private HttpURLConnection openConn(String baseUrl) throws IOException {

        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");

        return connection;
    }
}
