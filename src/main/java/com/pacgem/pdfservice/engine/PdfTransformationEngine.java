package com.pacgem.pdfservice.engine;

//import com.pacgem.pdfservice.model.PurchaseOrderData;
//import com.pacgem.pdfservice.service.TemplateService;
//import com.pacgem.pdfservice.service.StickerGenerator;
//import com.pacgem.pdfservice.exception.PdfProcessingException;
import com.pacgem.pdfservice.exception.PdfProcessingException;
import com.pacgem.pdfservice.model.dto.PurchaseOrderData;
import com.pacgem.pdfservice.model.dto.StickerTemplate;
import com.pacgem.pdfservice.model.dto.TransitStickerData;
import com.pacgem.pdfservice.service.TemplateService;
import com.pacgem.pdfservice.util.DataExtractor;
import com.pacgem.pdfservice.util.StickerGenerator;
import com.pacgem.pdfservice.util.TransitStickerGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.Loader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfTransformationEngine {

//    private final TemplateService templateService;
//    private final DataExtractor dataExtractor;
//    private final StickerGenerator stickerGenerator;
//
//    public byte[] transform(byte[] inputPdf, String format) {
//        try (PDDocument document = PDDocument.load(inputPdf)) {
//            PurchaseOrderData data = dataExtractor.extractData(document);
//            var template = templateService.getTemplate(format);
//            return stickerGenerator.generateSticker(data, template);
//        } catch (IOException e) {
//            throw new PdfProcessingException("Failed to transform PDF", e);
//        }
//    }


    private final TemplateService templateService;
    private final DataExtractor dataExtractor;
    private final StickerGenerator stickerGenerator;
    private final TransitStickerGenerator transitStickerGenerator;

    public byte[] transform(MultipartFile inputPdf, String format) {
        try (PDDocument document = Loader.loadPDF(inputPdf.getBytes())) {
            // Step 1: Extract data (purchase order, invoice etc.)
            PurchaseOrderData data = dataExtractor.extractData(document);

            // Step 2: Get template by format
            StickerTemplate template = templateService.getTemplate(format);

            // Step 3: Generate transformed PDF using extracted data and template
             return stickerGenerator.generateSticker(data, template);

        } catch (IOException e) {
            log.error("Error transforming PDF: {}", e.getMessage());
            throw new PdfProcessingException("Failed to transform PDF", e);
        }
    }
    public byte[] transform(TransitStickerData data, String format) {
        if ("TRANSIT_STICKER".equalsIgnoreCase(format)) {
            return transitStickerGenerator.generate(data);
        }
        throw new UnsupportedOperationException("Format not supported for direct JSON input: " + format);
    }
}

