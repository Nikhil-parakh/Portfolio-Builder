document.addEventListener('DOMContentLoaded', () => {
    // --- CONFIGURATION ---
    const API_URL = 'http://localhost:8080/api/portfolio/generate';
    
    // --- ELEMENTS ---
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const filePreview = document.getElementById('filePreview');
    const fileNameDisplay = document.getElementById('fileName');
    const removeBtn = document.getElementById('removeFileBtn');
    const submitBtn = document.getElementById('submitBtn');
    const statusMsg = document.getElementById('statusMessage');
    const uploadForm = document.getElementById('uploadForm');
    const spinner = document.querySelector('.spinner');
    const btnText = document.querySelector('.btn-text');

    let currentFile = null;

    // --- EVENT LISTENERS ---

    // Drag & Drop Interactions
    ['dragenter', 'dragover'].forEach(evt => {
        dropZone.addEventListener(evt, (e) => {
            e.preventDefault();
            e.stopPropagation();
            dropZone.classList.add('drag-over');
        });
    });

    ['dragleave', 'drop'].forEach(evt => {
        dropZone.addEventListener(evt, (e) => {
            e.preventDefault();
            e.stopPropagation();
            dropZone.classList.remove('drag-over');
        });
    });

    // Handle File Drop
    dropZone.addEventListener('drop', (e) => {
        const files = e.dataTransfer.files;
        if (files.length) handleFile(files[0]);
    });

    // Handle Click to Browse (delegated to hidden input)
    dropZone.addEventListener('click', (e) => {
        if (e.target.tagName !== 'BUTTON') { // Avoid double triggering if clicking the button directly
            fileInput.click();
        }
    });

    // Handle File Input Change
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length) handleFile(e.target.files[0]);
    });

    // Handle Remove File
    removeBtn.addEventListener('click', () => {
        currentFile = null;
        fileInput.value = '';
        togglePreview(false);
        showStatus('');
    });

    // Handle Form Submit
    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!currentFile) return;
        await uploadFile(currentFile);
    });

    // --- LOGIC FUNCTIONS ---

    function handleFile(file) {
        // Validate type
        const validTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
        if (!validTypes.includes(file.type)) {
            showStatus('Invalid file type. Please upload PDF or DOCX.', 'error');
            return;
        }

        // Validate size (e.g., max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showStatus('File is too large. Max size is 5MB.', 'error');
            return;
        }

        currentFile = file;
        fileNameDisplay.textContent = file.name;
        togglePreview(true);
        showStatus('');
    }

    function togglePreview(show) {
        if (show) {
            dropZone.classList.add('hidden');
            filePreview.classList.remove('hidden');
            submitBtn.disabled = false;
        } else {
            dropZone.classList.remove('hidden');
            filePreview.classList.add('hidden');
            submitBtn.disabled = true;
        }
    }

    function showStatus(msg, type = 'neutral') {
        statusMsg.textContent = msg;
        statusMsg.className = 'status-message ' + type;
    }

    function setLoading(isLoading) {
        if (isLoading) {
            submitBtn.disabled = true;
            btnText.textContent = 'Generating...';
            spinner.classList.remove('hidden');
        } else {
            submitBtn.disabled = false;
            btnText.textContent = 'Generate Portfolio';
            spinner.classList.add('hidden');
        }
    }

    // --- BACKEND INTEGRATION ---
    async function uploadFile(file) {
        setLoading(true);
        showStatus('Analyzing resume and generating website... This may take up to 30 seconds.');

        const formData = new FormData();
        // The key 'file' matches your Spring Boot @RequestParam("file")
        formData.append('file', file); 

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                // 1. Get the binary data (blob)
                const blob = await response.blob();
                
                // 2. Create a temporary URL for the blob
                const downloadUrl = window.URL.createObjectURL(blob);
                
                // 3. Create a hidden link element to trigger the download
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = downloadUrl;
                a.download = 'portfolio_website.zip'; // Default filename
                
                // 4. Append to body, click, and cleanup
                document.body.appendChild(a);
                a.click();
                
                window.URL.revokeObjectURL(downloadUrl);
                document.body.removeChild(a);

                showStatus('Success! Portfolio downloaded.', 'success');
            } else {
                // If backend sends text error
                const errorText = await response.text();
                showStatus('Error: ' + errorText, 'error');
            }
        } catch (error) {
            console.error('Upload error:', error);
            showStatus('Connection failed. Is the backend running?', 'error');
        } finally {
            setLoading(false);
        }
    }
});