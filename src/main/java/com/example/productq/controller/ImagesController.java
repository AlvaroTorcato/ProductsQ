package com.example.productq.controller;

import com.example.productq.model.UploadFileResponse;
import com.example.productq.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Tag(name = "Images", description = "Endpoints for images of the products")
@RequestMapping("/images")
@RestController
public class ImagesController {
    private static final Font BARCODE_TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Logger logger = LoggerFactory.getLogger(ImagesController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Upload image to product")
    @PostMapping("/uploadFile/{sku}")
    public UploadFileResponse uploadFile(@PathVariable("sku") final String sku, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String fileName = fileStorageService.storeFile(file,sku,request);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/images/downloadFile/")
                .path(fileName)
                .toUriString();


        return new UploadFileResponse(fileName, fileDownloadUri);
    }
    @Operation(summary = "Download image from product")
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
    @Operation(summary = "Get the barcode of a given product")
    @GetMapping(path = "/barcode/{sku}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getBarbecueBarCode(@PathVariable("sku") final String sku) throws Exception {
        BufferedImage image = fileStorageService.getBarcode(sku);
        return ResponseEntity.ok().body(image);}
}
