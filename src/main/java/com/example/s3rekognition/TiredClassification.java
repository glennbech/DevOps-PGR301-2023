package com.example.s3rekognition;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class TiredClassification {
    private String filename;
    private int violationCount;
    private int personCount;
}
