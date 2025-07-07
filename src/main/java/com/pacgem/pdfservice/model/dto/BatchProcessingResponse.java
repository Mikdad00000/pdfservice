package com.pacgem.pdfservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchProcessingResponse {
    private String batchId;
    private int totalJobs;
}

