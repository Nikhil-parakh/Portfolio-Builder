package com.Nikhil_Parakh.demo.Service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentParserService {
    public String parseDocument(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        System.out.println(">>> DEBUG: parsing of file starterd" + filename);

        if (filename == null)
            throw new IllegalArgumentException("Filename cannot be null!");

        String lowerCaseName = filename.toLowerCase();

        String rawText = "";
        if (lowerCaseName.endsWith(".pdf")) {
            rawText = extractTextFromPDF(file.getInputStream());
        } else if (lowerCaseName.endsWith(".docx")) {
            rawText = extractTextFromDoc(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported File Format... Only PDF or Docx are Allowed.");
        }

        System.out.println("Raw Text extracted successfully");
        return rawText;
    }

    public String extractTextFromPDF(InputStream istream) throws Exception {
        try (PDDocument doc = Loader.loadPDF(new RandomAccessReadBuffer(istream))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(doc).trim();
        }
    }

    private String extractTextFromDoc(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }
}
