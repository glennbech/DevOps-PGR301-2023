package com.example.s3rekognition;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class TiredFacesResponse implements Serializable {
    private String bucketName;
    private List<TiredClassification> violations;
}
