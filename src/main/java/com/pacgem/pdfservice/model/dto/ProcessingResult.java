package com.pacgem.pdfservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessingResult {
    private String jobId;
    private String transformedFileKey;
}

