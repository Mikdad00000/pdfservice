package com.pacgem.pdfservice.model.dto;

import com.pacgem.pdfservice.model.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessingResponse {
    private String jobId;
    private ProcessingStatus status;
    private String transformedFileKey;

    public ProcessingResponse(String jobId, ProcessingStatus status) {
        this.jobId = jobId;
        this.status = status;
    }
}
