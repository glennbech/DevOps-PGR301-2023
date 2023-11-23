package com.example.s3rekognition;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.logging.Logger;

@SpringBootApplication
public class S3RekognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3RekognitionApplication.class, args);
    }

    @Bean
    Regions getRegion(@Value("${aws.region:eu-west-1}") String aws_region) {
        // use strip so whitespace doesn't crash the program
        return Regions.fromName(aws_region.strip());
    }

    @Bean
    AmazonRekognition getRekognitionClient(Regions aws_region) {
        return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(aws_region)
                .build();
    }

    @Bean
    AmazonS3 getS3Client(Regions aws_region) {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(aws_region)
                .build();
    }
}
