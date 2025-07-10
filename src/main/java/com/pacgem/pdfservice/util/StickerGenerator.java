package com.pacgem.pdfservice.util;

import com.pacgem.pdfservice.exception.PdfProcessingException;
import com.pacgem.pdfservice.model.dto.PurchaseOrderData;
import com.pacgem.pdfservice.model.dto.StickerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
@Slf4j
public class StickerGenerator {

    public byte[] generateSticker(PurchaseOrderData data, StickerTemplate template) {
        try (PDDocument document = new PDDocument()) {
            PDRectangle pageSize = new PDRectangle(template.getWidth(), template.getHeight());
            PDPage page = new PDPage(pageSize);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
//                contentStream.setFont(PDType1Font.HELVETICA_BOLD, template.getFontSize());
                contentStream.setLeading(16f);
                contentStream.newLineAtOffset(50, pageSize.getHeight() - 50);

                contentStream.showText("Order Number: " + data.getOrderNumber()); contentStream.newLine();
                contentStream.showText("Vendor: " + data.getVendor()); contentStream.newLine();
                contentStream.showText("Order Date: " + data.getOrderDate()); contentStream.newLine();

                contentStream.showText("Items:"); contentStream.newLine();
//                for (PurchaseOrderData.Item item : /*data.getItems()*/) {
//                    contentStream.showText(" - " + item.getName() + " x" + item.getQuantity() + " @ $" + item.getPrice());
//                    contentStream.newLine();
//                }

                contentStream.showText("Total: $" + data.getTotalAmount()); contentStream.newLine();
                contentStream.endText();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new PdfProcessingException("Failed to generate sticker", e);
        }
    }
}
