package com.example.s3rekognition.controller;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.example.s3rekognition.TiredClassification;
import com.example.s3rekognition.PPEClassificationResponse;
import com.example.s3rekognition.PPEResponse;
import com.example.s3rekognition.TiredFacesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class RekognitionController implements ApplicationListener<ApplicationReadyEvent> {

    private final AmazonS3 s3Client;

    private final AmazonRekognition rekognitionClient;

    private static final Logger logger = Logger.getLogger(RekognitionController.class.getName());

    /**
     * This endpoint takes an S3 bucket name in as an argument, scans all the
     * Files in the bucket for Protective Gear Violations.
     * <p>
     *
     * @param bucketName
     * @return
     */
    @GetMapping(value = "/scan-ppe", consumes = "*/*", produces = "application/json")
    @ResponseBody
    public ResponseEntity<PPEResponse> scanForPPE(@RequestParam String bucketName) {
        logger.info("Bucket provided is " + bucketName);
        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);

        // This will hold all of our classifications
        List<PPEClassificationResponse> classificationResponses = new ArrayList<>();

        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        // Iterate over each object and scan for PPE
        for (S3ObjectSummary image : images) {
            logger.info("scanning " + image.getKey());

            // This is where the magic happens, use AWS rekognition to detect PPE
            DetectProtectiveEquipmentRequest request = new DetectProtectiveEquipmentRequest()
                    .withImage(new Image()
                            .withS3Object(new S3Object()
                                    .withBucket(bucketName)
                                    .withName(image.getKey())))
                    .withSummarizationAttributes(new ProtectiveEquipmentSummarizationAttributes()
                            .withMinConfidence(80f)
                            .withRequiredEquipmentTypes("FACE_COVER"));

            DetectProtectiveEquipmentResult result = rekognitionClient.detectProtectiveEquipment(request);

            // If any person on an image lacks PPE on the face, it's a violation of regulations
            boolean violation = isViolation(result);

            logger.info("scanning " + image.getKey() + ", violation result " + violation);
            // Categorize the current image as a violation or not.
            int personCount = result.getPersons().size();
            PPEClassificationResponse classification = new PPEClassificationResponse(image.getKey(), personCount, violation);
            classificationResponses.add(classification);
        }
        PPEResponse ppeResponse = new PPEResponse(bucketName, classificationResponses);
        return ResponseEntity.ok(ppeResponse);
    }

    /**
     * This endpoint takes an S3 bucket name in as an argument, scans all the
     * Files in the bucket for tired faces.
     *
     * @param bucketName
     * @return
     */
    @GetMapping(value = "/scan-tired", produces = "application/json")
    public ResponseEntity<TiredFacesResponse> scanForTiredFaces(@RequestParam String bucketName) {
        // List all objects in the S3 bucket
        ListObjectsV2Result imageList = s3Client.listObjectsV2(bucketName);

        // This is all the images in the bucket
        List<S3ObjectSummary> images = imageList.getObjectSummaries();

        // Iterate over each object and scan for tiredness by checking if they are confused or scared
        List<TiredClassification> imageResults = images.stream()
                //  Create scan requests
                .map(image -> new DetectFacesRequest()
                        .withImage(new Image()
                                .withS3Object(new S3Object()
                                        .withBucket(bucketName)
                                        .withName(image.getKey()))
                        )
                        .withAttributes(Attribute.ALL)
                )
                .peek(detectFacesRequest -> logger.info("Detecting faces in s3://" + bucketName + "/" + detectFacesRequest.getImage().getS3Object().getName()))
                .map(detectFacesRequest -> {
                    DetectFacesResult result = rekognitionClient.detectFaces(detectFacesRequest);
                    return TiredClassification.builder()
                            .filename(detectFacesRequest.getImage().getS3Object().getName())
                            .violationCount(result.getFaceDetails()
                                    .stream()
                                    .map(faceDetails -> faceDetails
                                            .getEmotions()
                                            .stream()
                                            .filter(emotion -> emotion.getConfidence() >= 80f) // Confidence threshold could be a configuration maybe?
                                            .peek(emotion -> logger.info("Detected " + emotion + " in " + detectFacesRequest.getImage().getS3Object().getName()))
                                            // Tired is not an emotion, so we match against confused or fear instead.
                                            // This really should use its own model trained to find tired faces.
                                            .anyMatch(emotion ->
                                                    emotion.getType().contentEquals(EmotionName.CONFUSED.name())
                                                            ||
                                                    emotion.getType().contentEquals(EmotionName.FEAR.name())
                                            )
                                    )
                                    .filter(Boolean::booleanValue)
                                    .mapToInt(v -> 1)
                                    .sum()
                            )
                            .personCount(result.getFaceDetails().size())
                            .build();
                }).collect(Collectors.toList());
        return ResponseEntity.ok(new TiredFacesResponse(bucketName, imageResults));
    }

    @PostMapping("/upload-image")
    public void uploadToBucket(@RequestBody File file, @Value("${bucket.name}") String bucketName) {
            logger.info("Uploading to s3 bucket: " + file.getName());
            s3Client.putObject(bucketName, file.getName(), file);
    }

    /**
     * Detects if the image has a protective gear violation for the FACE bodypart-
     * It does so by iterating over all persons in a picture, and then again over
     * each body part of the person. If the body part is a FACE and there is no
     * protective gear on it, a violation is recorded for the picture.
     *
     * @param result
     * @return
     */
    private static boolean isViolation(DetectProtectiveEquipmentResult result) {
        return result.getPersons().stream()
                .flatMap(p -> p.getBodyParts().stream())
                .anyMatch(bodyPart -> bodyPart.getName().equals("FACE")
                        && bodyPart.getEquipmentDetections().isEmpty());
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

    }
}
