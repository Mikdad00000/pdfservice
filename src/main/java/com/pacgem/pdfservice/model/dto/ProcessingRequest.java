package com.pacgem.pdfservice.model.dto;

import lombok.Data;

@Data
public class ProcessingRequest {
    private String format;

    public ProcessingRequest(String format) {
        this.format = format;
    }
}

