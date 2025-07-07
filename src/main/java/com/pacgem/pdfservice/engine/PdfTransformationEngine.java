package com.pacgem.pdfservice.engine;

//import com.pacgem.pdfservice.model.PurchaseOrderData;
//import com.pacgem.pdfservice.service.TemplateService;
//import com.pacgem.pdfservice.service.StickerGenerator;
//import com.pacgem.pdfservice.exception.PdfProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

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
}

