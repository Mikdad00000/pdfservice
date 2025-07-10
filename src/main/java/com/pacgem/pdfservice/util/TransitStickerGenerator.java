package com.pacgem.pdfservice.util;

import com.pacgem.pdfservice.model.dto.TransitStickerData;
import com.pacgem.pdfservice.model.dto.TransitStickerData.SizeRatioEntry;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TransitStickerGenerator {
    @Autowired
    private ResourceLoader resourceLoader;

    private float  pageHeight;
//    public byte[] generate(TransitStickerData data) {
//        try (PDDocument document = new PDDocument()) {
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
//                float margin = 50;
//                float yStart = page.getMediaBox().getHeight() - margin;
//                float xStart = margin;
//                float lineHeight = 15;
//                float tableStartY = yStart - 180;
//
//                PDType1Font helbeticaBoldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//                cs.setFont(helbeticaBoldFont, 14);
//                cs.beginText();
//                cs.newLineAtOffset(xStart, yStart);
//                cs.showText("Transit Sticker");
//                cs.endText();
//
//                PDType1Font helbeticaFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
//                cs.setFont(helbeticaFont, 10);
//
//                // Header Info
//                String[] leftHeaders = {
//                        "PO No: " + data.getPoNumber(),
//                        "Brand: " + data.getBrand(),
//                        "Department: " + data.getDepartment(),
//                        "Season Code: " + data.getSeasonCode(),
//                        "Product Ref: " + data.getProductRef(),
//                        "Seller Name: " + data.getSellerName()
//                };
//
//                String[] rightHeaders = {
//                        "UK Style Ref: " + data.getUkStyleRef(),
//                        "CE Style Ref: " + data.getCeStyleRef(),
//                        "Packaging: " + data.getPackaging(),
//                        "Product Type: " + data.getProductType(),
//                        "PO Type: " + data.getPoType(),
//                        "Tag At Source?: " + (data.isTagAtSource() ? "Yes" : "No")
//                };
//
//                float y = yStart - 30;
//                for (int i = 0; i < leftHeaders.length; i++) {
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart, y - i * lineHeight);
//                    cs.showText(leftHeaders[i]);
//                    cs.endText();
//
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart + 300, y - i * lineHeight);
//                    cs.showText(rightHeaders[i]);
//                    cs.endText();
//                }
//
//                // Table Header
//                float tableX = xStart;
//                float tableY = tableStartY;
//                float colWidth = 70;
//                float rowHeight = 20;
//                String[] headers = {
//                        "Size/Ratio", "Case Qty", "Garment Qty", "Carton Qty",
//                        "TCL Allowance(%)", "TSL Allowance(%)", "TCL Qty", "TSL Qty"
//                };
//
//                // Draw header
//                cs.setFont(helbeticaBoldFont, 9);
//                for (int i = 0; i < headers.length; i++) {
//                    cs.beginText();
//                    cs.newLineAtOffset(tableX + i * colWidth + 2, tableY);
//                    cs.showText(headers[i]);
//                    cs.endText();
//                }
//
//                // Draw rows
//                cs.setFont(helbeticaFont, 9);
//                List<SizeRatioEntry> entries = data.getSizeRatios();
//                for (int i = 0; i < entries.size(); i++) {
//                    SizeRatioEntry row = entries.get(i);
//                    float rowY = tableY - (i + 1) * rowHeight;
//
//                    String[] rowData = {
//                            row.getSize(),
//                            String.valueOf(row.getCaseQty()),
//                            String.valueOf(row.getGarmentQty()),
//                            String.valueOf(row.getCartonQty()),
//                            String.valueOf(row.getTclAllowance()),
//                            String.valueOf(row.getTslAllowance()),
//                            String.valueOf(row.getTclQty()),
//                            String.valueOf(row.getTslQty())
//                    };
//
//                    for (int j = 0; j < rowData.length; j++) {
//                        cs.beginText();
//                        cs.newLineAtOffset(tableX + j * colWidth + 2, rowY);
//                        cs.showText(rowData[j]);
//                        cs.endText();
//                    }
//                }
//            }
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            document.save(out);
//            return out.toByteArray();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate Transit Sticker PDF", e);
//        }
//    }
//    public byte[] generate(TransitStickerData data) {
//        try (PDDocument document = new PDDocument()) {
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
//                float margin = 50;
//                float yStart = page.getMediaBox().getHeight() - margin;
//                float xStart = margin;
//                float lineHeight = 15;
//                float pageWidth = page.getMediaBox().getWidth() - 2 * margin;
//
//                // Title
//                PDType1Font helveticaBoldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//                cs.setFont(helveticaBoldFont, 16);
//                cs.beginText();
//                cs.newLineAtOffset(xStart, yStart);
//                cs.showText("TRANSIT STICKER");
//                cs.endText();
//
//                // Draw horizontal line under title
//                cs.moveTo(xStart, yStart - 5);
//                cs.lineTo(xStart + pageWidth, yStart - 5);
//                cs.stroke();
//
//                PDType1Font helveticaFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
//                cs.setFont(helveticaFont, 10);
//
//                float y = yStart - 30;
//
//                // Left column headers
//                String[] leftHeaders = {
//                        "PO No: " + data.getPoNumber(),
//                        "UK Style Ref: " + data.getUkStyleRef(),
//                        "Short Description: " + data.getStyleDescription(),
//                        "CE Style Ref (VPN/OPTION ID): " + data.getCeStyleRef(),
//                        "Supplier Name: " + data.getSellerName(),
//                        "MANUFACTURED DATE: " + data.getManufactureDate()
//                };
//
//                // Right column headers
//                String[] rightHeaders = {
//                        "RATIO",
////                        "EQ Code: " + (data.getEqCode() != null ? data.getEqCode() : ""),
//                        "TST SS BASIC T GRE",
//                        "Style Ref: " + data.getProductRef(),
//                        "Brand: " + data.getBrand(),
//                        "Season: " + data.getSeasonCode()
//                };
//
//                // Additional info
//                String[] additionalInfo = {
//                        "Dept: " + data.getDepartment(),
//                        "RFID Compliant: NO",
//                        "Tag At Source: " + (data.isTagAtSource() ? "YES" : "NO"),
////                        "Packaging Supplier Name: " + (data.getPackagingSupplier() != null ? data.getPackagingSupplier() : ""),
//                        "Product Type: " + data.getProductType(),
//                        "PO Type: " + data.getPoType()
//                };
//
//                // Draw left column
//                for (int i = 0; i < leftHeaders.length; i++) {
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart, y - i * lineHeight);
//                    cs.showText(leftHeaders[i]);
//                    cs.endText();
//                }
//
//                // Draw right column
//                for (int i = 0; i < rightHeaders.length; i++) {
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart + 300, y - i * lineHeight);
//                    cs.showText(rightHeaders[i]);
//                    cs.endText();
//                }
//
//                // Draw additional info below
//                float additionalY = y - (leftHeaders.length * lineHeight) - 20;
//                for (int i = 0; i < additionalInfo.length; i++) {
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart, additionalY - i * lineHeight);
//                    cs.showText(additionalInfo[i]);
//                    cs.endText();
//                }
//
//                // Draw horizontal line before table
//                float tableStartY = additionalY - (additionalInfo.length * lineHeight) - 20;
//                cs.moveTo(xStart, tableStartY + 10);
//                cs.lineTo(xStart + pageWidth, tableStartY + 10);
//                cs.stroke();
//
//                // Table setup
//                float tableX = xStart;
//                float tableY = tableStartY;
//                float colWidth = (pageWidth - 20) / 8; // 8 columns
//                float rowHeight = 20;
//
//                String[] headers = {
//                        "Size/Ratio", "Case Qty", "Garment Qty", "Carton Qty",
//                        "TCL Allowance(%)", "TSL Allowance(%)", "TCL Qty", "TSL Qty"
//                };
//
//                // Draw table border
//                cs.setLineWidth(1);
//                float tableWidth = colWidth * headers.length;
//                float tableHeight = rowHeight * (data.getSizeRatios().size() + 1);
//
//                // Outer border
//                cs.addRect(tableX, tableY - tableHeight, tableWidth, tableHeight);
//                cs.stroke();
//
//                // Draw header row
//                cs.setFont(helveticaBoldFont, 9);
//                for (int i = 0; i < headers.length; i++) {
//                    // Vertical lines
//                    cs.moveTo(tableX + i * colWidth, tableY);
//                    cs.lineTo(tableX + i * colWidth, tableY - tableHeight);
//                    cs.stroke();
//
//                    // Header text
//                    cs.beginText();
//                    cs.newLineAtOffset(tableX + i * colWidth + 2, tableY - 15);
//                    cs.showText(headers[i]);
//                    cs.endText();
//                }
//
//                // Draw last vertical line
//                cs.moveTo(tableX + tableWidth, tableY);
//                cs.lineTo(tableX + tableWidth, tableY - tableHeight);
//                cs.stroke();
//
//                // Draw horizontal line after header
//                cs.moveTo(tableX, tableY - rowHeight);
//                cs.lineTo(tableX + tableWidth, tableY - rowHeight);
//                cs.stroke();
//
//                // Draw data rows
//                cs.setFont(helveticaFont, 9);
//                List<SizeRatioEntry> entries = data.getSizeRatios();
//                for (int i = 0; i < entries.size(); i++) {
//                    SizeRatioEntry row = entries.get(i);
//                    float rowY = tableY - (i + 1) * rowHeight;
//
//                    String[] rowData = {
//                            row.getSize(),
//                            String.valueOf(row.getCaseQty()),
//                            String.valueOf(row.getGarmentQty()),
//                            String.valueOf(row.getCartonQty()),
//                            String.valueOf(row.getTclAllowance()),
//                            String.valueOf(row.getTslAllowance()),
//                            String.valueOf(row.getTclQty()),
//                            String.valueOf(row.getTslQty())
//                    };
//
//                    for (int j = 0; j < rowData.length; j++) {
//                        cs.beginText();
//                        cs.newLineAtOffset(tableX + j * colWidth + 2, rowY - 15);
//                        cs.showText(rowData[j]);
//                        cs.endText();
//                    }
//
//                    // Draw horizontal line after each row
//                    if (i < entries.size() - 1) {
//                        cs.moveTo(tableX, rowY - rowHeight);
//                        cs.lineTo(tableX + tableWidth, rowY - rowHeight);
//                        cs.stroke();
//                    }
//                }
//
//                // Add "No of pieces in ratio:" and "Box............ of .............." at the bottom
//                float bottomY = tableY - tableHeight - 30;
//                cs.setFont(helveticaFont, 10);
//                cs.beginText();
//                cs.newLineAtOffset(xStart, bottomY);
//                cs.showText("No of pieces in ratio:");
//                cs.endText();
//
//                cs.beginText();
//                cs.newLineAtOffset(xStart, bottomY - 20);
//                cs.showText("Box............ of ..............");
//                cs.endText();
//
//                // Add dates if available
//                if (data.getStartDate() != null && data.getEndDate() != null) {
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart + 300, bottomY);
//                    cs.showText("Start Date: " + data.getStartDate());
//                    cs.endText();
//
//                    cs.beginText();
//                    cs.newLineAtOffset(xStart + 300, bottomY - 20);
//                    cs.showText("End Date: " + data.getEndDate());
//                    cs.endText();
//                }
//            }
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            document.save(out);
//            return out.toByteArray();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate Transit Sticker PDF", e);
//        }
//    }


//public byte[] generate(TransitStickerData data) {
//    try (PDDocument document = new PDDocument()) {
//        // Create a smaller page size for sticker format
//        PDRectangle pageSize = new PDRectangle(400, 300);
//        PDPage page = new PDPage(pageSize);
//        document.addPage(page);
//
//        try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
//            float margin = 20;
//            float pageWidth = pageSize.getWidth();
//            float pageHeight = pageSize.getHeight();
//            float yStart = pageHeight - margin;
//            float xStart = margin;
//            float lineHeight = 12;
//
//            PDType1Font helveticaBoldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//            PDType1Font helveticaFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
//
//            // Draw border around entire sticker
//            cs.setLineWidth(2);
//            cs.addRect(margin/2, margin/2, pageWidth - margin, pageHeight - margin);
//            cs.stroke();
//
//            // Header: RATIO and SS25
//            cs.setFont(helveticaBoldFont, 16);
//            cs.beginText();
//            cs.newLineAtOffset(xStart, yStart - 20);
//            cs.showText("RATIO");
//            cs.endText();
//
//            cs.beginText();
//            cs.newLineAtOffset(pageWidth - 80, yStart - 20);
//            cs.showText(data.getSeasonCode());
//            cs.endText();
//
//            float currentY = yStart - 45;
//
//            // Left column data
//            cs.setFont(helveticaBoldFont, 9);
//            String[] leftLabels = {
//                    "TPND:",
//                    "PO No:",
//                    "UK Style Ref:",
//                    "CE Style Ref (VPN/OPTION ID):",
//                    "Short Description:",
//                    "EQOS Code:",
//                    "Supplier Name:"
//            };
//
//            String[] leftValues = {
//                    data.getTpnd() != null ? data.getTpnd() : "",
//                    data.getPoNumber(),
//                    data.getUkStyleRef(),
//                    data.getCeStyleRef(),
//                    data.getStyleDescription(),
//                    data.getEqosCode() != null ? data.getEqosCode() : "",
//                    data.getSellerName()
//            };
//
//            // Right column data
//            String[] rightLabels = {
//                    "Dept:",
//                    "Brand:",
//                    "RFID Compliant:",
//                    "Tag At Source:",
//                    "",
//                    "",
//                    "Packaging Supplier Name:"
//            };
//
//            String[] rightValues = {
//                    data.getDepartment(),
//                    data.getBrand(),
//                    "NO",
//                    data.isTagAtSource() ? "YES" : "NO",
//                    "",
//                    "",
//                    data.getPackagingSupplier() != null ? data.getPackagingSupplier() : ""
//            };
//
//            // Draw left column
//            for (int i = 0; i < leftLabels.length; i++) {
//                cs.beginText();
//                cs.newLineAtOffset(xStart, currentY - (i * lineHeight));
//                cs.showText(leftLabels[i]);
//                cs.endText();
//
//                cs.setFont(helveticaFont, 9);
//                cs.beginText();
//                cs.newLineAtOffset(xStart + 80, currentY - (i * lineHeight));
//                cs.showText(leftValues[i]);
//                cs.endText();
//                cs.setFont(helveticaBoldFont, 9);
//            }
//
//            // Draw right column
//            float rightColumnX = pageWidth - 150;
//            for (int i = 0; i < rightLabels.length; i++) {
//                if (!rightLabels[i].isEmpty()) {
//                    cs.beginText();
//                    cs.newLineAtOffset(rightColumnX, currentY - (i * lineHeight));
//                    cs.showText(rightLabels[i]);
//                    cs.endText();
//
//                    cs.setFont(helveticaFont, 9);
//                    cs.beginText();
//                    cs.newLineAtOffset(rightColumnX + 50, currentY - (i * lineHeight));
//                    cs.showText(rightValues[i]);
//                    cs.endText();
//                    cs.setFont(helveticaBoldFont, 9);
//                }
//            }
//
//            // Bottom section
//            float bottomY = currentY - (leftLabels.length * lineHeight) - 20;
//
//            cs.setFont(helveticaBoldFont, 9);
//            cs.beginText();
//            cs.newLineAtOffset(xStart, bottomY);
//            cs.showText("No of pieces in ratio:");
//            cs.endText();
//
//            cs.beginText();
//            cs.newLineAtOffset(xStart, bottomY - 15);
//            cs.showText("Box............ of ..............");
//            cs.endText();
//
//            // Barcode area (placeholder)
//            cs.setFont(helveticaFont, 8);
//            cs.beginText();
//            cs.newLineAtOffset(xStart, bottomY - 35);
//            cs.showText("0 5 0 5 9 6 9 7 6 1 2 4 6 6");
//            cs.endText();
//
//            // Manufacturing date
//            cs.setFont(helveticaBoldFont, 9);
//            cs.beginText();
//            cs.newLineAtOffset(xStart, bottomY - 50);
//            cs.showText("MANUFACTURED DATE: " + data.getManufactureDate());
//            cs.endText();
//
//            // Right side bottom - certification logos area
//            float logoX = rightColumnX + 20;
//            cs.setFont(helveticaFont, 6);
//            cs.beginText();
//            cs.newLineAtOffset(logoX, bottomY - 25);
//            cs.showText("FSC");
//            cs.endText();
//
//            cs.beginText();
//            cs.newLineAtOffset(logoX + 20, bottomY - 25);
//            cs.showText("PEFC");
//            cs.endText();
//
//            // Draw small boxes for logos
//            cs.setLineWidth(0.5f);
//            cs.addRect(logoX - 2, bottomY - 35, 15, 15);
//            cs.stroke();
//            cs.addRect(logoX + 18, bottomY - 35, 15, 15);
//            cs.stroke();
//        }
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        document.save(out);
//        return out.toByteArray();
//
//    } catch (Exception e) {
//        throw new RuntimeException("Failed to generate Transit Sticker PDF", e);
//    }
//}
// last


    public byte[] generate(TransitStickerData data) {
        try {
            // Load template from resources
            Resource resource = resourceLoader.getResource("classpath:templates/Templete_EQ126438-TCL1UK-460-22173.pdf");
            InputStream templateStream = resource.getInputStream();

            // Load the PDF document
//            PDDocument document = PDDocument.load(templateStream);
            PDDocument document = Loader.loadPDF(templateStream.readAllBytes());
            // Method 1: If template has form fields
            if (hasFormFields(document)) {
                return fillFormFields(document, data);
            }

            // Method 2: Text replacement approach
            return replaceTextContent(document, data);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Transit Sticker PDF from template", e);
        }
    }

    private boolean hasFormFields(PDDocument document) {
        try {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            return acroForm != null && acroForm.getFields().size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] fillFormFields(PDDocument document, TransitStickerData data) throws IOException {
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

        // Fill all available fields
        Map<String, String> fieldValues = createFieldValueMap(data);

        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            fillFieldIfExists(acroForm, entry.getKey(), entry.getValue());
        }

        // Flatten the form to make it non-editable
        acroForm.flatten();

        return saveDocumentToByteArray(document);
    }

    private Map<String, String> createFieldValueMap(TransitStickerData data) {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put("poNumber", data.getPoNumber());
        fieldValues.put("seasonCode", data.getSeasonCode());
        fieldValues.put("styleDescription", data.getStyleDescription());
        fieldValues.put("sellerName", data.getSellerName());
        fieldValues.put("ukStyleRef", data.getUkStyleRef());
        fieldValues.put("ceStyleRef", data.getCeStyleRef());
        fieldValues.put("department", data.getDepartment());
        fieldValues.put("brand", data.getBrand());
        fieldValues.put("manufactureDate", data.getManufactureDate());
        fieldValues.put("tagAtSource", data.isTagAtSource() ? "YES" : "NO");
//        fieldValues.put("packaging", data.getPackaging());
//        fieldValues.put("productRef", data.getProductRef());
        return fieldValues;
    }

//    private byte[] replaceTextContent(PDDocument document, TransitStickerData data) throws IOException {
//        // Load the first page (assuming you're working with the first page)
//        PDPage page = document.getPage(0);
//
//        // Create a new content stream that will replace existing content
//        PDPageContentStream cs = new PDPageContentStream(
//                document,
//                page,
//                PDPageContentStream.AppendMode.OVERWRITE,
//                true,
//                true
//        );
//
//
//        // Define fonts (you might want to make these parameters or class variables)
//        PDFont franklinGothicFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
//        PDFont franklinGothicBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//        replaceText(cs, franklinGothicBold, 24, 300, 590, data.getSeasonCode());           // Bold 24pt
//        replaceText(cs, franklinGothicFont, 12, 100, 530, data.getCeStyleRef());        // Regular 12pt
//        replaceText(cs, franklinGothicFont, 12, 100, 510, data.getPoNumber());
//        replaceText(cs, franklinGothicFont, 12, 100, 490, data.getUkStyleRef());
//        replaceText(cs, franklinGothicFont, 12, 100, 470, data.getShortDescription());
//        replaceText(cs, franklinGothicFont, 12, 100, 450, data.getEqosCode());
//        replaceText(cs, franklinGothicFont, 12, 100, 430, data.getSupplierName());
//        replaceText(cs, franklinGothicFont, 12, 100, 410, data.getManufacturedDate());
//        replaceText(cs, franklinGothicFont, 12, 100, 390, data.getDepartment());
//        replaceText(cs, franklinGothicBold, 14, 100, 370, data.getBrand());                // Slightly larger and bold
//        replaceText(cs, franklinGothicFont, 12, 100, 350, data.getRfidCompliant());
////        replaceText(cs, franklinGothicFont, 12, 100, 330, data.getTagAtSource());
//        replaceText(cs, franklinGothicFont, 12, 100, 310, data.getPackagingSupplierName());
//        replaceText(cs, franklinGothicFont, 12, 100, 120, data.getBoxLabel());
//
////
////        // Replace Season Code (24pt, bold)
////        replaceText(cs, franklinGothicBold, 24, 300, 230, data.getSeasonCode());
////
////        // Replace PO Number (15pt)
////        replaceText(cs, franklinGothicFont, 15, 50, 250, data.getPoNumber());
////
////        // Replace Dept (9pt)
////        replaceText(cs, franklinGothicFont, 9, 250, 140, "Dept: " + data.getDepartment());
//
//        // Replace all text fields with data from TransitStickerData
//
////        // Replace PO Number
////        replaceText(cs, franklinGothicFont, 10, 50, 250, data.getPoNumber());
////
////        // Replace Season Code
////        replaceText(cs, franklinGothicBold, 16, 300, 230, data.getSeasonCode());
//
//        // Replace EQOS Code (if you have it in data)
////        if (data.getCeStyleRef() != null) {
////            replaceText(cs, franklinGothicFont, 10, 50, 200, data.getCeStyleRef());
////        }
//
//        // Replace Style Description
////        replaceText(cs, franklinGothicFont, 10, 50, 180, data.getStyleDescription());
//
//        // Replace Supplier Name
////        replaceText(cs, franklinGothicFont, 10, 50, 160, data.getSellerName());
//
//        // Replace other fields based on your template positions
////        replaceText(cs, franklinGothicFont, 9, 50, 140, "TPND: " + (data.getProductRef() != null ? data.getProductRef() : ""));
////        replaceText(cs, franklinGothicFont, 9, 50, 125, "PO No: " + data.getPoNumber());
////        replaceText(cs, franklinGothicFont, 9, 50, 110, "UK Style Ref: " + data.getUkStyleRef());
////        replaceText(cs, franklinGothicFont, 9, 50, 95, "Short Description: " + data.getStyleDescription());
////        replaceText(cs, franklinGothicFont, 9, 50, 80, "CE Style Ref (VPN/OPTION ID): " + data.getCeStyleRef());
////        replaceText(cs, franklinGothicFont, 9, 50, 65, "Supplier Name: " + data.getSellerName());
////        replaceText(cs, franklinGothicFont, 9, 50, 50, "MANUFACTURED DATE: " + data.getManufactureDate());
//
//        // Right column
////        replaceText(cs, franklinGothicFont, 9, 250, 140, "Dept: " + data.getDepartment());
////        replaceText(cs, franklinGothicFont, 9, 250, 125, "Brand: " + data.getBrand());
////        replaceText(cs, franklinGothicFont, 9, 250, 110, "RFID Compliant: NO");
////        replaceText(cs, franklinGothicFont, 9, 250, 95, "Tag At Source: " + (data.isTagAtSource() ? "YES" : "NO"));
//
//        cs.close();
//
//        return saveDocumentToByteArray(document);
//    }

//    private void replaceText(PDPageContentStream cs, PDFont font, float fontSize, float x, float y, String text) throws IOException {
//        // First, draw a white rectangle to "erase" existing text
////        cs.setNonStrokingColor(Color.WHITE);
////        cs.addRect(x, y - 2, 200, fontSize + 4); // Adjust width as needed
////        cs.fill();
//        float rectWidth = 200; // Adjust to fit your layout
//        float rectHeight = fontSize + 4;
//        cs.setNonStrokingColor(Color.WHITE);
//        cs.addRect(x, y - 2, rectWidth, rectHeight);
//        cs.fill();
//
//
//        // Then draw the new text in black
//        cs.setNonStrokingColor(Color.BLUE);
//        cs.setFont(font, fontSize);
//        cs.beginText();
//        cs.newLineAtOffset(x, y);
//        cs.showText(text != null ? text : "");
//        cs.endText();
//    }

    /**
     * Replaces text in a PDF at specified coordinates with white background covering.
     * @param cs       PDPageContentStream
     * @param font     PDFont (Franklin Gothic Condensed or fallback)
     * @param fontSize Font size in points
     * @param x        X-coordinate (from left)
     * @param y        Y-coordinate (from bottom)
     * @param text     Text to insert
     */
    private void replaceText(PDPageContentStream cs, PDFont font, float fontSize, float x, float y, String text) throws IOException {
        // 1. Draw white rectangle to "erase" existing text
        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(x, y - 2, this.pageHeight-200, fontSize + 4);  // Width 200 covers most fields
        cs.fill();

//        cs.setNonStrokingColor(Color.RED);
//        cs.addRect(x, this.pageHeight - y, 50, 2); // Horizontal line marker
//        cs.fill();

        // 2. Draw new text in black
        cs.setNonStrokingColor(Color.BLACK);
        cs.setFont(font, fontSize);
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");  // Handle null values
        cs.endText();
    }
    /**
     * Replaces all dynamic fields in the transit sticker PDF.
     * @param document PDDocument (loaded template)
     * @param data     TransitStickerData (contains field values)
     * @return byte[]  Modified PDF as byte array
     */
//    private byte[] replaceTextContent(PDDocument document, TransitStickerData data) throws IOException {
//        PDPage page = document.getPage(0);
//        PDPageContentStream cs = new PDPageContentStream(
//                document, page,
//                PDPageContentStream.AppendMode.OVERWRITE,
//                true, true
//        );
//
//        // Load fonts (fallback to Helvetica if Franklin Gothic not available)
////        PDFont franklinRegular = PDType1Font.HELVETICA;       // Fallback for Franklin Gothic Condensed
////        PDFont franklinBold = PDType1Font.HELVETICA_BOLD;     // Fallback for Franklin Gothic Bold
//
//          PDFont franklinBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
//          PDFont franklinRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
//
//        // --- Replace Fields (Coordinates Adjusted for Your Template) ---
//        // Right Column (Dynamic Fields)
//        replaceText(cs, franklinBold, 24, 300, 590, data.getSeasonCode());  // Season Code (Bold, 24pt)
//
//        // Left Column (Static Fields)
//        replaceText(cs, franklinRegular, 12, 100, 530, data.getCeStyleRef());
//        replaceText(cs, franklinRegular, 12, 100, 510, data.getPoNumber());
//        replaceText(cs, franklinRegular, 12, 100, 490, data.getUkStyleRef());
//        replaceText(cs, franklinRegular, 12, 100, 470, data.getStyleDescription());
//        replaceText(cs, franklinRegular, 12, 100, 450, data.getEqosCode());
//        replaceText(cs, franklinRegular, 12, 100, 430, data.getSellerName());
//        replaceText(cs, franklinRegular, 12, 100, 410, data.getManufactureDate());
//        replaceText(cs, franklinRegular, 12, 100, 390, data.getDepartment());
//        replaceText(cs, franklinBold, 14, 100, 370, data.getBrand());        // Brand (Bold, 14pt)
//        replaceText(cs, franklinRegular, 12, 100, 350, data.isRfidCompliant() ? "YES" : "NO");
//        replaceText(cs, franklinRegular, 12, 100, 330, data.isTagAtSource() ? "YES" : "NO");
//        replaceText(cs, franklinRegular, 12, 100, 310, data.getPackagingSupplier());
//
//        // Box Label (Dynamic)
////        replaceText(cs, franklinRegular, 12, 100, 120,
////                String.format("Box %d of %d", data.getCurrentBox(), data.getTotalBoxes()));
//
//        // QR Code (Example: Generate separately and overlay as image)
//        // QRCodeGenerator.generateAndPlaceQR(cs, data.getQrContent(), 380, 100, 17);
//
//        cs.close();
//        return saveDocumentToByteArray(document);
//    }
private byte[] replaceTextContent(PDDocument document, TransitStickerData data) throws IOException {
//    PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
        PDPage page = document.getPage(0);
    this.pageHeight = page.getMediaBox().getHeight();
        PDPageContentStream cs = new PDPageContentStream(
                document, page,
                PDPageContentStream.AppendMode.OVERWRITE,
                true, true
        );

    // Use a fallback font (or load FranklinGothicCondSSK if embedded)
    String path = "D:" + File.separator + "Mikdad" + File.separator + "Pacgem" +
            File.separator + "Roboto_Condensed" + File.separator + "static" +
            File.separator + "RobotoCondensed-ExtraBold.ttf";

    PDFont font = PDType0Font.load(document, new File(path));


//    PDFont font = new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);

    // Field replacements using real positions and font sizes
    replaceText(cs, font, 26f, 10f,  pageHeight - 25.6f, data.getRatioLabel());
    replaceText(cs, font, 28f, 274f,  pageHeight - 26.27f, data.getSeasonCode());
    replaceText(cs, font, 18f, 6f,  pageHeight - 55.53f, "TPND:");
    replaceText(cs, font, 16f, 109f,  pageHeight - 53.58f, data.getTpnCode());

    replaceText(cs, font, 10f, 212f,  pageHeight - 52.74f, "Dept:");
    replaceText(cs, font, 10f, 290f,  pageHeight - 52.74f, data.getDepartment());

    replaceText(cs, font, 18f, 6f,  pageHeight - 73.53f, "PO No:");
    replaceText(cs, font, 16f, 109f,  pageHeight - 71.58f, data.getPoNumber());

    replaceText(cs, font, 10f, 212f,  pageHeight - 66.74f, "Brand:");
    replaceText(cs, font, 11f, 290f,  pageHeight - 67.71f, data.getBrand());
    replaceText(cs, font, 10f, 212f, pageHeight - 80.74f, "RFID Compliant:");
    replaceText(cs, font, 11f, 290f, pageHeight - 81.71f, "YES");


    replaceText(cs, font, 10f, 6f,  pageHeight - 86.74f, "UK Style Ref:");
    replaceText(cs, font, 10f, 111f,  pageHeight - 86.74f, data.getUkStyleRef());

    replaceText(cs, font, 7f, 6f,  pageHeight - 98.82f, "CE Style Ref (VPN/OPTION ID):");
    replaceText(cs, font, 10f, 111f,  pageHeight - 100.74f, data.getCeStyleRef());

    replaceText(cs, font, 11f, 212f,  pageHeight - 94.71f, "Tag At Source:");
//    replaceText(cs, font, 11f, 290f, 94.71f, data.getTagAtSource());

    replaceText(cs, font, 10f, 6f,  pageHeight - 112.74f, "Short Description:");
    replaceText(cs, font, 10f, 111f,  pageHeight - 114.74f, data.getShortDescription());

    replaceText(cs, font, 10f, 6f,  pageHeight - 127.74f, "EQOS Code:");
    replaceText(cs, font, 10f, 111f,  pageHeight - 127.74f, data.getEqosCode());

    replaceText(cs, font, 10f, 6f,  pageHeight - 140.74f, "Supplier Name:");
    replaceText(cs, font, 10f, 111f,  pageHeight - 140.74f, data.getSupplierName());


    replaceText(cs, font, 7f, 192f,  pageHeight - 157f, "Packaging Supplier Name:");
    replaceText(cs, font, 8f, 270f,  pageHeight - 157f, data.getPackagingSupplierName());
    replaceText(cs, font, 10f, 191f, pageHeight - 170.74f, "No of pieces in ratio:");


    replaceText(cs, font, 10f, 192f,  pageHeight - 184.74f, data.getBoxLabel());

    replaceText(cs, font, 10f, 13f,  pageHeight - 214.74f, "MANUFACTURED DATE:");
    replaceText(cs, font, 10f, 111f,  pageHeight - 214.74f, data.getManufactureDate());

    cs.close();
    return saveDocumentToByteArray(document);
}


    /**
     * Saves the modified PDDocument to a byte array.
     */
    private byte[] saveDocumentToByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out.toByteArray();
    }

    private void fillFieldIfExists(PDAcroForm acroForm, String fieldName, String value) {
        try {
            PDField field = acroForm.getField(fieldName);
            if (field != null && value != null) {
                field.setValue(value);
            }
        } catch (Exception e) {
            log.warn("Could not fill field {}: {}", fieldName, e.getMessage());
        }
    }
//    public byte[] generate(TransitStickerData data) {
////        File templateFile = new File("src/main/resources/templates/Templete_EQ126438-TCL1UK-460-22173.pdf");
//        File templateFile = new File("D:/Mikdad/Pacgem/pdf-processing-service/src/main/resources/templates/Templete_EQ126438-TCL1UK-460-22173.pdf");
//
//        try (PDDocument document = Loader.loadPDF(templateFile)) {
//            PDAcroForm form = document.getDocumentCatalog().getAcroForm();
//
//            if (form == null) {
//                throw new RuntimeException("Template is missing an AcroForm.");
//            }
//
//            // Map data fields (ensure template field names match!)
//            setField(form, "poNumber", data.getPoNumber());
//            setField(form, "brand", data.getBrand());
//            setField(form, "department", data.getDepartment());
//            setField(form, "seasonCode", data.getSeasonCode());
//            setField(form, "productRef", data.getProductRef());
//            setField(form, "sellerName", data.getSellerName());
//            setField(form, "ukStyleRef", data.getUkStyleRef());
//            setField(form, "ceStyleRef", data.getCeStyleRef());
//            setField(form, "packaging", data.getPackaging());
//            setField(form, "productType", data.getProductType());
//            setField(form, "poType", data.getPoType());
//            setField(form, "styleDescription", data.getStyleDescription());
//            setField(form, "manufactureDate", data.getManufactureDate());
//            setField(form, "startDate", data.getStartDate());
//            setField(form, "endDate", data.getEndDate());
//            setField(form, "tagAtSource", data.isTagAtSource() ? "Yes" : "No");
//
//            // Optional: Flatten the form to prevent editing
//            form.flatten();
//
//            // Output as byte[]
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            document.save(out);
//            return out.toByteArray();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate Transit Sticker from template", e);
//        }
//    }

    private void setField(PDAcroForm form, String name, String value) {
        try {
            PDField field = form.getField(name);
            if (field != null) {
                field.setValue(value != null ? value : "");
            } else {
                System.out.println("⚠️ Field not found in template: " + name);
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to set field: " + name + " - " + e.getMessage());
        }
    }
}

