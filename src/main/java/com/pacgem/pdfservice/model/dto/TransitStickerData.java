package com.pacgem.pdfservice.model.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransitStickerData {
    // Header form fields
    private String ratioLabel;
    private String seasonCode;          // e.g., "SS25"
    private String poNumber;           // e.g., "902-4125"
    private String ukStyleRef;         // e.g., "TST SS BASIC T GRE"
    private String styleDescription;   // e.g., "1007057"
    private String ceStyleRef;         // e.g., "EQ126438"
    private String eqosCode;           // e.g., "KDS GARMENT IND LTD..."
    private String sellerName;         // e.g., "041194998"
    private String manufactureDate;    // e.g., "APRIL,2025"
    private String department;         // e.g., "MENSWEAR"
    private String brand;              // e.g., "F&F"
    private boolean rfidCompliant;     // true/false → "YES"/"NO"
    private boolean tagAtSource;       // true/false → "YES"/"NO"
    private String packagingSupplier;  // e.g., "DEKKO ACCESSORIES LTD."
    private int currentBox;            // e.g., 1
    private int totalBoxes;            // e.g., 10
    private String qrContent;          // e.g., "902-4125,998,041194998"
    private String tpnCode;
    private String shortDescription;
    private String supplierName;
    private String packagingSupplierName;
    private String boxLabel;


    // Getters and setters...
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeRatioEntry {
        private String size;
        private int caseQty;
        private int garmentQty;
        private int cartonQty;
        private double tclAllowance;
        private double tslAllowance;
        private int tclQty;
        private int tslQty;
    }
}
