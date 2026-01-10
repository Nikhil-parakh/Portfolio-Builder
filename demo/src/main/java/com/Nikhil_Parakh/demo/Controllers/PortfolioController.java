package com.Nikhil_Parakh.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.Nikhil_Parakh.demo.DTO.ResponseDTO;
import com.Nikhil_Parakh.demo.Service.AiGenerationService;
import com.Nikhil_Parakh.demo.Service.DocumentParserService;
import com.Nikhil_Parakh.demo.Service.ZipService;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired
    private final DocumentParserService parserService;

    @Autowired
    private final AiGenerationService aiService;

    @Autowired
    private final ZipService zipService;

    public PortfolioController(DocumentParserService parserService, AiGenerationService aiService, ZipService zipService){
        this.parserService = parserService;
        this.aiService = aiService;
        this.zipService = zipService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generatePortfolio(@RequestParam("file") MultipartFile file) {
        try {
            String resumeText = parserService.parseDocument(file);

            ResponseDTO portfolio = aiService.generatePortfolio(resumeText);

            byte[] zipData = zipService.createZipFile(portfolio);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"portfolio_website.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
