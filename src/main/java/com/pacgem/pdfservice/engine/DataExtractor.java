package com.pacgem.pdfservice.engine;

//import com.pacgem.pdfservice.model.PurchaseOrderData;
//import com.pacgem.pdfservice.exception.PdfProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DataExtractor {

//    public PurchaseOrderData extractData(PDDocument document) {
//        try {
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//            return parseDataFromText(text);
//        } catch (IOException e) {
//            throw new PdfProcessingException("Failed to extract data", e);
//        }
//    }
//
//    private PurchaseOrderData parseDataFromText(String text) {
//        // Use simple regex parsing to extract purchase order details
//        PurchaseOrderData data = new PurchaseOrderData();
//        data.setOrderNumber(extractOrderNumber(text));
//        data.setVendor(extractVendor(text));
//        data.setItems(extractItems(text));
//        data.setTotalAmount(extractTotalAmount(text));
//        data.setOrderDate(extractOrderDate(text));
//        return data;
//    }

    private String extractOrderNumber(String text) {
        // implement regex
        return "PO-12345";
    }
    private String extractVendor(String text) {
        // implement regex
        return "Acme Supplies";
    }
    private String extractItems(String text) {
        // implement regex
        return "Widget A, Widget B";
    }
    private double extractTotalAmount(String text) {
        // implement regex
        return 199.99;
    }
    private String extractOrderDate(String text) {
        // implement regex
        return "2025-07-06";
    }
}

