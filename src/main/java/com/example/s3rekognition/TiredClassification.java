package com.example.s3rekognition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TiredClassification {
    private String filename;
    private int violationCount;
    private int personCount;
}
