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

    // Handle Click to Browse
    // Note: The click listener on dropZone is removed to prevent conflict with the specific 'Browse' button
    // The Browse button in HTML now calls onclick="document.getElementById('fileInput').click()" directly.

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

        // Validate size (max 5MB)
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
            btnText.textContent = 'Generating Magic...';
            spinner.classList.remove('hidden');
        } else {
            submitBtn.disabled = false;
            btnText.textContent = 'Generate Website';
            spinner.classList.add('hidden');
        }
    }

    // --- BACKEND INTEGRATION ---
    async function uploadFile(file) {
        setLoading(true);
        showStatus('Analyzing resume and designing layout...', 'neutral'); // UI feedback update

        const formData = new FormData();
        formData.append('file', file); 

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const blob = await response.blob();
                const downloadUrl = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = downloadUrl;
                a.download = 'portfolio_website.zip'; 
                
                document.body.appendChild(a);
                a.click();
                
                window.URL.revokeObjectURL(downloadUrl);
                document.body.removeChild(a);

                showStatus('Success! Portfolio generated and downloaded.', 'success');
            } else {
                const errorText = await response.text();
                showStatus('Error: ' + errorText, 'error');
            }
        } catch (error) {
            console.error('Upload error:', error);
            showStatus('Connection failed. Please check backend server.', 'error');
        } finally {
            setLoading(false);
        }
    }
});