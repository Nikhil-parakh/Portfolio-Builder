# 🚀 AI Portfolio Generator

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0%2B-green)
![AI](https://img.shields.io/badge/GenAI-Integrated-blueviolet)

> **Turn your resume into a professional portfolio website in seconds.**

## 📖 Overview

The **AI Portfolio Generator** is a full-stack application that leverages Generative AI to automate the creation of personal portfolio websites. 

Users simply drag and drop their resume (PDF or DOCX). The application parses the document, analyzes the content using an LLM (Large Language Model), and generates a complete, responsive HTML/CSS website tailored to the user's skills and experience. The final result is instantly downloaded as a `.zip` file.

## ✨ Features

-   **🎨 Futuristic UI:** A sleek, dark-mode interface with glassmorphism effects and neon animations.
-   **📂 Drag & Drop Upload:** Seamless file handling for PDF and DOCX formats.
-   **🤖 AI Analysis:** intelligently extracts skills, projects, and summaries from resumes.
-   **⚡ Instant Generation:** Generates valid HTML/CSS code and packages it into a ZIP file.
-   **🔒 Secure Processing:** Server-side validation for file types and sizes.

## 🛠️ Tech Stack

### Frontend
-   **HTML5 & CSS3:** Custom "Space/Glass" design system.
-   **JavaScript (Vanilla):** Async fetch API for handling file uploads and binary downloads.
-   **Animations:** CSS Keyframes for ambient backgrounds and loading states.

### Backend
-   **Java 17+:** Core language.
-   **Spring Boot:** REST API creation and file handling.
-   **LangChain4j / OpenAI API:** "gemini-flash-latest" for content generation.
-   **Apache PDFBox / POI:** For parsing PDF and Word documents.

---

## 🚀 Getting Started

Follow these instructions to set up the project locally.

### Prerequisites
-   **Java Development Kit (JDK) 17** or higher.
-   **Maven** (for backend dependency management).
-   **An IDE** (IntelliJ IDEA, Eclipse, or VS Code).
-   **API Key** (Gemini) for the AI service.

### 1. Backend Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/ai-portfolio-generator.git](https://github.com/your-username/ai-portfolio-generator.git)
    cd ai-portfolio-generator
    ```

2.  **Configure Environment Variables:**
    Navigate to `src/main/resources/application.properties` and add your AI API keys and configuration:
    ```properties
    server.port=8080
    spring.servlet.multipart.max-file-size=10MB
    spring.servlet.multipart.max-request-size=10MB
    
    # AI Configuration (Example)
    ai.api.key=YOUR_API_KEY_HERE
    ai.model=gemini_model
    ```

3.  **Build and Run:**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The backend should now be running at `http://localhost:8080`.

### 2. Frontend Setup

1.  Navigate to the `frontend` folder (where your `index.html`, `style.css`, and `script.js` are located).
2.  Open `index.html` in your browser.
    * *Recommended:* Use the **Live Server** extension in VS Code to avoid CORS issues if you run into them, though the current script allows direct file opening.

---

## 🔌 API Documentation

### Generate Portfolio
**Endpoint:** `POST /api/portfolio/generate`

**Description:** Uploads a resume file and returns a ZIP file containing the generated website.

**Request Body:** `multipart/form-data`
| Key | Type | Description |
| :--- | :--- | :--- |
| `file` | `File` | The resume file (.pdf, .docx). Max 10MB. |

**Response:**
-   `200 OK`: Returns `application/zip` (The portfolio website).
-   `400 Bad Request`: Invalid file type or missing file.
-   `500 Internal Server Error`: AI processing failed.

---

## 🔮 Future Roadmap

-   [ ] Add multiple template themes (Minimalist, Creative, Corporate).
-   [ ] Allow users to edit the generated content before downloading.
-   [ ] Deploy backend to AWS/Render and frontend to Vercel.
-   [ ] Add LinkedIn profile URL scraping support.

## 🤝 Contributing

Contributions are welcome!
1.  Fork the Project.
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the Branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 👤 Author

**Nikhil Parakh**
-   **Role:** Java Backend Developer (Java/Spring Boot + GenAI)
-   **GitHub:** [GitHubLink](https://github.com/Nikhil-parakh)
-   **LinkedIn:** [LinkedInLink](https://www.linkedin.com/in/parakhnikhil/)

## 📄 License

This project is open-source and available for educational and professional use.
