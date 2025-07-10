package com.pacgem.pdfservice.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickerTemplate {
    private String name;         // e.g., "LABEL_A5"
    private String layoutPath;   // Path to template file (e.g., in S3 or resources)
    private int width;           // in points (1/72 inch)
    private int height;
    private boolean landscape;   // true if horizontal
    private String font;         // font name
    private int fontSize;
}
