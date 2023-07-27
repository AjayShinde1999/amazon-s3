package com.amazons3image.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/api")
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity<String> handleUploadForm(@RequestParam("description") String description,
                                                   @RequestParam("file") MultipartFile multipart) {
        String fileName = multipart.getOriginalFilename();

        System.out.println("Description: " + description);
        System.out.println("filename: " + fileName);

        String message = "";

        try {
            S3Util.uploadFile(fileName, multipart.getInputStream());
            message = "Your file has been uploaded successfully!";
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception ex) {
            message = "Error uploading file: " + ex.getMessage();
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> handleImageDownload(@RequestParam("fileName") String fileName) {
        try {
            // Download the image from S3 using the S3Util class
            InputStream imageStream = S3Util.downloadFile(fileName);

            // Set the content type and headers for the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Set the appropriate content type based on the image type

            // Create an InputStreamResource to wrap the image stream
            InputStreamResource resource = new InputStreamResource(imageStream);

            // Return the ResponseEntity with the image data in the response body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception ex) {
            // Handle any errors that occur during the download process
            String errorMessage = "Error downloading image: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes())));
        }
    }

}

