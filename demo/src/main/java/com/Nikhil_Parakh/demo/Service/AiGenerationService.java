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
                        You are an Elite Frontend Developer & UI/UX Designer with a portfolio of Awwwards-winning websites.

                        Your Goal: Build a visually stunning, high-converting portfolio website that stands out.

                        Your Expertise:
                        - Advanced CSS: Smooth gradients, glassmorphism, deep shadows, and modern grid layouts (Bento grids).
                        - Typography: You utilize visual hierarchy with bold headings and readable body text.
                        - Interaction: You add subtle, polished hover states and micro-interactions.
                        - Responsive: Flawless mobile-first execution.

                        You strictly avoid:
                        - Boring, flat, all-white designs.
                        - Broken layouts or unstyled links.
                        - "Demo" quality code.

                        Your Output Logic:
                        1. Analyze the input content deeply.
                        2. If a specific section (like Projects or Experience) is missing, adapt the layout gracefully.
                        3. MANDATORY: You MUST extract and render all Social Links (LinkedIn, GitHub, Email, Portfolio) if they exist in the input data.
                        4. Return ONLY the code in the requested format.
                        """));

        String userPromptCode = """
                Create a complete, production-ready portfolio website based on the specification provided below.

                The result must be visually striking, colorful yet professional, and fully responsive.

                --------------------------------------------------
                DESIGN STYLE (MANDATORY - MODERN TECH THEME)
                --------------------------------------------------
                Do not produce a plain white site. Use the following design language:

                1. Typography:
                   - MUST import the 'Poppins' or 'Outfit' font from Google Fonts in the HTML head.
                   - Use high-contrast font weights (Bold headings, regular text).

                2. Color Palette:
                   - Background: Very light grey/blue tint (#f8fafc) or soft cream, NOT plain white.
                   - Primary Accent: Use a vibrant Gradient (e.g., Blue to Purple, or Orange to Red) for buttons, active states, and key highlights.
                   - Cards: Pure white (#ffffff) with distinct box-shadows (e.g., 0 4px 6px -1px rgba(0, 0, 0, 0.1)) and rounded corners (border-radius: 12px or 16px).
                   - Text: Dark Slate (#0f172a) for headings, Cool Grey (#475569) for body.

                3. UI Components:
                   - Navbar: Sticky, glassmorphism effect (backdrop-filter: blur).
                   - Buttons: Modern, pill-shaped or slightly rounded, with hover lift effects.
                   - Social Links: MUST be displayed prominently (e.g., in the Hero section or Footer) using text or FontAwesome icons if available (or clear text buttons).

                --------------------------------------------------
                IMPORTANT TECHNICAL REQUIREMENTS
                --------------------------------------------------
                1. LINKING:
                   - HTML <head> must include: <link rel="stylesheet" href="style.css">
                   - HTML <head> must include Google Fonts links.
                   - End of <body> must include: <script src="script.js"></script>

                2. CONTENT PARSING (CRITICAL):
                   - Read the SPECIFICATION text carefully.
                   - If the text contains URLs for LinkedIn, GitHub, Twitter, or Email, you MUST create clickable `<a>` tags for them. Do not ignore them.
                   - If project descriptions are long, truncate them visually or use a grid layout.

                3. CSS STRUCTURE:
                   - Use CSS Variables (:root) for colors to ensure consistency.
                   - Use Flexbox and CSS Grid for layouts.

                --------------------------------------------------
                OUTPUT FORMAT (STRICT)
                --------------------------------------------------
                Return ONLY the code sections below. No markdown, no intro text.

                --html--
                HTML CODE HERE
                --html--

                --css--
                CSS CODE HERE
                --css--

                --js--
                JS CODE HERE
                --js--

                --------------------------------------------------
                SPECIFICATION (SOURCE CONTENT)
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