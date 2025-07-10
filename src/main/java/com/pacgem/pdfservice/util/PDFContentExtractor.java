//package com.pacgem.pdfservice.util;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import java.io.File;
//import java.util.*;
//
//public class PDFContentExtractor {
//
//    public static void main(String[] args) throws Exception {
//        File pdfFile = new File("Templete_EQ126438-TCL1UK-460-22173.pdf");
//        try (PDDocument doc = PDDocument.load(pdfFile)) {
//            PDPage page = doc.getPage(0);
//
//            // 1. Text
//            FontInfoCollector textCollector = new FontInfoCollector();
//            List<Map<String, Object>> textData = textCollector.extractText(doc);
//
//            // 2. Images
//            ImageExtractorWithCoordinates imgExtractor = new ImageExtractorWithCoordinates(page);
//            List<Map<String, Object>> imageData = imgExtractor.extract();
//
//            // 3. Combine
//            List<Map<String, Object>> allContent = new ArrayList<>();
//            allContent.addAll(textData);
//            allContent.addAll(imageData);
//
//            // 4. Output to JSON
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("pdf_content.json"), allContent);
//            System.out.println("✅ Metadata exported to pdf_content.json");
//        }
//    }
//}
//
