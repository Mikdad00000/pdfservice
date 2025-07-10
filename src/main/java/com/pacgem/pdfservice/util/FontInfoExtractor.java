package com.pacgem.pdfservice.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class FontInfoExtractor extends PDFTextStripper {

    public FontInfoExtractor() throws IOException {
        super.setSortByPosition(true);  // optional: keeps reading order
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        for (TextPosition text : textPositions) {
            System.out.printf("Text: %-20s Font: %-20s Size: %-6.2f X: %-6.2f Y: %-6.2f%n",
                    text.getUnicode(),
                    text.getFont().getName(),
                    text.getFontSizeInPt(),
                    text.getXDirAdj(),
                    text.getYDirAdj()
            );
        }
    }

//    File file = new File("D:\\Mikdad\\Pacgem\\pdf-processing-service\\src\\main\\resources\\templates\\Templete_EQ126438-TCL1UK-460-22173.pdf");
//        try (PDDocument document = Loader.loadPDF(file)) {
//        FontInfoExtractor stripper = new FontInfoExtractor();
//        stripper.setStartPage(1);
//        stripper.setEndPage(1);
//        stripper.getText(document);  // triggers writeString()
//    }
}
