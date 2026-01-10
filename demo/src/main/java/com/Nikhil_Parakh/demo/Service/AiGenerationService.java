package com.Nikhil_Parakh.demo.Service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.Nikhil_Parakh.demo.DTO.ResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiGenerationService {

    private static final Logger log = LoggerFactory.getLogger(AiGenerationService.class);
    private final ChatLanguageModel chatLanguageModel;

    public AiGenerationService(ChatLanguageModel chatLanguageModel) {
        this.chatLanguageModel = chatLanguageModel;
    }

    /**
     * Orchestrates the 2-step generation process:
     * 1. Analyze Resume -> Create Specification
     * 2. Use Specification -> Generate HTML/CSS/JS
     */
    @SuppressWarnings("removal")
    public ResponseDTO generatePortfolio(String resumeText) {

        // --- STEP 1: Specification Extraction ---
        log.info("Step 1: Starting resume analysis and specification extraction.");

        List<ChatMessage> specMessages = new ArrayList<>();
        specMessages.add(SystemMessage
                .from("You are a resume analyzer. Convert resume text into a structured website specification."));

        String userPromptSpec = """
                From the resume below, extract:
                - Name
                - About
                - Skills
                - Experience
                - Projects
                - Education
                - Contact Links (LinkedIn, GitHub, Email - Verify accuracy)
                - Achievements

                Resume:
                %s
                """;
        specMessages.add(UserMessage.from(String.format(userPromptSpec, resumeText)));

        Response<AiMessage> specResponse = chatLanguageModel.generate(specMessages);
        String websiteSpec = specResponse.content().text();

        log.debug("Generated Specification: {}", websiteSpec);
        log.info("Step 1 Complete: Specification extracted successfully.");

        // --- STEP 2: Frontend Code Generation ---
        log.info("Step 2: Generating frontend code (HTML/CSS/JS).");

        List<ChatMessage> codeMessages = new ArrayList<>();
        codeMessages.add(SystemMessage
                .from("""
                        You are a senior frontend engineer with 8+ years of professional experience building production-ready portfolio and marketing websites.

                        You specialize in:
                        - Semantic HTML5 with accessibility best practices (ARIA, proper landmarks)
                        - Modern CSS (Flexbox, Grid, responsive layouts, clean spacing, scalable class naming)
                        - Maintainable, minimal vanilla JavaScript (no frameworks unless requested)
                        - UI/UX principles used in real-world products (clear hierarchy, spacing, contrast, alignment)
                        - Performance-conscious design (fast load, minimal JS, optimized CSS)
                        - Cross-browser compatibility and mobile-first responsiveness

                        You think like a real frontend professional:
                        - You prioritize clean structure over flashy effects
                        - You write readable, reusable, and well-organized code
                        - You avoid unnecessary libraries or overengineering
                        - You ensure class names are consistent and meaningful
                        - You design layouts that look good to recruiters and clients

                        Your output MUST:
                        - Be production-quality, not demo-quality
                        - Follow the exact output format requested
                        - Generate HTML, CSS, and JavaScript that work together without errors
                        - Respect all technical constraints provided
                        - Contain no explanations, comments, or markdown — only code in the specified format

                        Treat this task as if you are delivering a real client project.
                        """));

        String userPromptCode = """
                Create a complete, production-ready portfolio website using the specification provided below.

                The result must look professional, modern, and suitable for real-world use by developers and professionals.

                --------------------------------------------------
                DESIGN STYLE (MANDATORY)
                --------------------------------------------------
                Use a Modern Classic Light–Neutral Theme with a clean, elegant, and confident appearance.

                - Background: Soft off-white / warm neutral tones (#f8fafc, #f1f5f9) to create an airy, modern feel.
                - Text: Charcoal or deep slate (#1f2933, #334155) for high readability and professional contrast.
                - Accents: Muted royal blue or soft indigo (#2563eb, #4f46e5) for buttons, links, and highlights.
                - Cards & Sections: White or very light grey cards with subtle shadows and rounded corners to add modern depth.
                - Typography: Modern sans-serif fonts that are clean, readable, and professional.
                - Layout Style: Spacious sections, clear visual hierarchy, balanced whitespace, and smooth scrolling behavior.
                - Animations: Minimal and tasteful only (soft hover effects and gentle transitions).
                - Overall Feel: Minimal, polished, elegant, and timeless — appropriate for portfolios reviewed by recruiters and clients.

                --------------------------------------------------
                IMPORTANT TECHNICAL REQUIREMENTS (STRICT)
                --------------------------------------------------
                1. The HTML file MUST include the following line inside the <head> tag to link the CSS file:
                   <link rel="stylesheet" href="style.css">

                2. The HTML file MUST include the following line at the very end of the <body> tag to link the JavaScript file:
                   <script src="script.js"></script>

                3. All CSS class names used in the HTML MUST match exactly with the CSS code provided.

                4. Use only semantic HTML elements and clean, maintainable structure.

                --------------------------------------------------
                OUTPUT FORMAT (DO NOT DEVIATE)
                --------------------------------------------------
                Output the result STRICTLY in the following format.
                Do not add explanations, comments, markdown, or extra text outside this structure.

                --html--
                HTML CODE
                --html--

                --css--
                CSS CODE
                --css--

                --js--
                JAVASCRIPT CODE
                --js--

                --------------------------------------------------
                SPECIFICATION (SOURCE OF CONTENT)
                --------------------------------------------------
                %s
                """;

        codeMessages.add(UserMessage.from(String.format(userPromptCode, websiteSpec)));

        Response<AiMessage> codeResponse = chatLanguageModel.generate(codeMessages);

        log.info("Step 2 Complete: Frontend code generated.");
        return parseResponse(codeResponse.content().text());
    }

    private ResponseDTO parseResponse(String rawResponse) {
        String html = extractSection(rawResponse, "--html--");
        String css = extractSection(rawResponse, "--css--");
        String js = extractSection(rawResponse, "--js--");
        return new ResponseDTO(html, css, js);
    }

    private String extractSection(String text, String marker) {
        try {
            String[] parts = text.split(marker);
            if (parts.length >= 2) {
                return parts[1].trim();
            }
        } catch (Exception e) {
            log.error("Failed to extract section using marker: {}", marker, e);
        }
        return "";
    }
}