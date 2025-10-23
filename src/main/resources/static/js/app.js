/**
 * MediSure Application JavaScript
 * Production-ready health insurance management system
 */

(function() {
    'use strict';

    // ==========================================
    // Utility Functions
    // ==========================================

    /**
     * Debounce function for performance optimization
     */
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    /**
     * Format currency
     */
    function formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }

    /**
     * Format date
     */
    function formatDate(date, options = {}) {
        return new Intl.DateTimeFormat('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            ...options
        }).format(new Date(date));
    }

    /**
     * Get CSRF token from meta tag
     */
    function getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    }

    /**
     * Get CSRF header name from meta tag
     */
    function getCsrfHeader() {
        return document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    }

    // ==========================================
    // Toast Notifications
    // ==========================================

    /**
     * Show toast notification
     * @param {string} message - Message to display
     * @param {string} type - Type of toast (success, error, info, warning)
     * @param {number} duration - Duration in milliseconds
     */
    window.showToast = function(message, type = 'info', duration = 5000) {
        window.dispatchEvent(new CustomEvent('toast', {
            detail: { message, type, duration }
        }));
    };

    // ==========================================
    // Confirmation Dialog
    // ==========================================

    /**
     * Show confirmation modal
     * @param {string} title - Dialog title
     * @param {string} message - Dialog message
     * @param {Function} confirmAction - Action to execute on confirmation
     */
    window.confirm = function(title, message, confirmAction) {
        window.dispatchEvent(new CustomEvent('open-modal', {
            detail: { title, message, action: confirmAction }
        }));
    };

    // ==========================================
    // Form Validation
    // ==========================================

    /**
     * Real-time form validation
     */
    function initFormValidation() {
        document.querySelectorAll('form[data-validate]').forEach(form => {
            form.addEventListener('submit', function(e) {
                const invalidFields = form.querySelectorAll(':invalid');
                
                if (invalidFields.length > 0) {
                    e.preventDefault();
                    invalidFields[0].focus();
                    showToast('Please fill in all required fields correctly', 'error');
                }
            });

            // Real-time validation on blur
            form.querySelectorAll('input, textarea, select').forEach(field => {
                field.addEventListener('blur', function() {
                    if (!field.validity.valid) {
                        field.classList.add('border-red-500');
                        field.classList.remove('border-slate-300');
                    } else {
                        field.classList.remove('border-red-500');
                        field.classList.add('border-slate-300');
                    }
                });
            });
        });
    }

    // ==========================================
    // Auto-logout Warning
    // ==========================================

    /**
     * Warn user before session expires
     */
    function initSessionTimeout() {
        const SESSION_TIMEOUT = 25 * 60 * 1000; // 25 minutes
        const WARNING_TIME = 5 * 60 * 1000; // 5 minutes before expiry
        
        let timeoutWarning;
        let timeoutLogout;

        function resetTimers() {
            clearTimeout(timeoutWarning);
            clearTimeout(timeoutLogout);
            
            timeoutWarning = setTimeout(() => {
                showToast('Your session will expire in 5 minutes. Please save your work.', 'warning', 10000);
            }, SESSION_TIMEOUT - WARNING_TIME);
            
            timeoutLogout = setTimeout(() => {
                showToast('Session expired. Redirecting to login...', 'error');
                setTimeout(() => {
                    window.location.href = '/login?expired=true';
                }, 2000);
            }, SESSION_TIMEOUT);
        }

        // Reset timers on user activity
        ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
            document.addEventListener(event, debounce(resetTimers, 1000));
        });

        resetTimers();
    }

    // ==========================================
    // File Upload Preview
    // ==========================================

    /**
     * Initialize file upload previews
     */
    function initFileUploadPreviews() {
        document.querySelectorAll('input[type="file"][data-preview]').forEach(input => {
            input.addEventListener('change', function(e) {
                const file = e.target.files[0];
                const previewId = input.dataset.preview;
                const preview = document.getElementById(previewId);
                
                if (file && preview) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        if (file.type.startsWith('image/')) {
                            preview.innerHTML = `<img src="${e.target.result}" class="max-w-full h-auto rounded-lg" alt="Preview">`;
                        } else {
                            preview.innerHTML = `<p class="text-sm text-slate-600">File: ${file.name} (${(file.size / 1024).toFixed(2)} KB)</p>`;
                        }
                    };
                    reader.readAsDataURL(file);
                }
            });
        });
    }

    // ==========================================
    // Table Enhancements
    // ==========================================

    /**
     * Initialize table row selection
     */
    function initTableSelection() {
        document.querySelectorAll('table[data-selectable]').forEach(table => {
            const selectAllCheckbox = table.querySelector('input[type="checkbox"][data-select-all]');
            const rowCheckboxes = table.querySelectorAll('tbody input[type="checkbox"]');
            
            if (selectAllCheckbox) {
                selectAllCheckbox.addEventListener('change', function() {
                    rowCheckboxes.forEach(checkbox => {
                        checkbox.checked = selectAllCheckbox.checked;
                    });
                });
            }
            
            rowCheckboxes.forEach(checkbox => {
                checkbox.addEventListener('change', function() {
                    const allChecked = Array.from(rowCheckboxes).every(cb => cb.checked);
                    const someChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
                    
                    if (selectAllCheckbox) {
                        selectAllCheckbox.checked = allChecked;
                        selectAllCheckbox.indeterminate = !allChecked && someChecked;
                    }
                });
            });
        });
    }

    // ==========================================
    // Copy to Clipboard
    // ==========================================

    /**
     * Copy text to clipboard
     */
    window.copyToClipboard = function(text, successMessage = 'Copied to clipboard!') {
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard.writeText(text).then(() => {
                showToast(successMessage, 'success');
            }).catch(() => {
                showToast('Failed to copy to clipboard', 'error');
            });
        } else {
            // Fallback for older browsers
            const textArea = document.createElement('textarea');
            textArea.value = text;
            textArea.style.position = 'fixed';
            textArea.style.left = '-999999px';
            document.body.appendChild(textArea);
            textArea.select();
            try {
                document.execCommand('copy');
                showToast(successMessage, 'success');
            } catch (err) {
                showToast('Failed to copy to clipboard', 'error');
            }
            document.body.removeChild(textArea);
        }
    };

    // ==========================================
    // Print Functionality
    // ==========================================

    /**
     * Print specific element
     */
    window.printElement = function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            const printWindow = window.open('', '', 'height=600,width=800');
            printWindow.document.write('<html><head><title>Print</title>');
            printWindow.document.write('<link rel="stylesheet" href="/css/custom.css">');
            printWindow.document.write('</head><body>');
            printWindow.document.write(element.innerHTML);
            printWindow.document.write('</body></html>');
            printWindow.document.close();
            printWindow.focus();
            printWindow.print();
            printWindow.close();
        }
    };

    // ==========================================
    // Keyboard Shortcuts
    // ==========================================

    /**
     * Initialize keyboard shortcuts
     */
    function initKeyboardShortcuts() {
        document.addEventListener('keydown', function(e) {
            // Ctrl/Cmd + K for search
            if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
                e.preventDefault();
                const searchInput = document.querySelector('input[type="search"], input[name="search"]');
                if (searchInput) {
                    searchInput.focus();
                }
            }
            
            // ESC to close modals
            if (e.key === 'Escape') {
                window.dispatchEvent(new CustomEvent('close-modal'));
                window.dispatchEvent(new CustomEvent('close-generic-modal'));
            }
        });
    }

    // ==========================================
    // Auto-save Draft
    // ==========================================

    /**
     * Auto-save form drafts to localStorage
     */
    function initAutoSave() {
        document.querySelectorAll('form[data-autosave]').forEach(form => {
            const formId = form.dataset.autosave;
            
            // Load saved draft
            const savedData = localStorage.getItem(`draft_${formId}`);
            if (savedData) {
                try {
                    const data = JSON.parse(savedData);
                    Object.keys(data).forEach(key => {
                        const field = form.querySelector(`[name="${key}"]`);
                        if (field && field.type !== 'password') {
                            field.value = data[key];
                        }
                    });
                    showToast('Draft restored', 'info', 3000);
                } catch (e) {
                    console.error('Failed to restore draft:', e);
                }
            }
            
            // Auto-save on input
            const saveForm = debounce(() => {
                const formData = new FormData(form);
                const data = {};
                for (let [key, value] of formData.entries()) {
                    const field = form.querySelector(`[name="${key}"]`);
                    if (field && field.type !== 'password') {
                        data[key] = value;
                    }
                }
                localStorage.setItem(`draft_${formId}`, JSON.stringify(data));
            }, 2000);
            
            form.addEventListener('input', saveForm);
            
            // Clear draft on successful submit
            form.addEventListener('submit', function() {
                localStorage.removeItem(`draft_${formId}`);
            });
        });
    }

    // ==========================================
    // Initialize on DOM Ready
    // ==========================================

    document.addEventListener('DOMContentLoaded', function() {
        console.log('MediSure initialized');
        
        // Initialize all features
        initFormValidation();
        initSessionTimeout();
        initFileUploadPreviews();
        initTableSelection();
        initKeyboardShortcuts();
        initAutoSave();
        
        // Add smooth scrolling
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    e.preventDefault();
                    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            });
        });
        
        // Initialize tooltips
        const tooltips = document.querySelectorAll('[data-tooltip]');
        tooltips.forEach(element => {
            element.addEventListener('mouseenter', function() {
                const tooltip = document.createElement('div');
                tooltip.className = 'absolute z-50 px-2 py-1 text-xs text-white bg-slate-900 rounded shadow-lg';
                tooltip.textContent = this.dataset.tooltip;
                tooltip.style.top = this.offsetTop - 30 + 'px';
                tooltip.style.left = this.offsetLeft + 'px';
                tooltip.id = 'tooltip';
                this.parentNode.appendChild(tooltip);
            });
            
            element.addEventListener('mouseleave', function() {
                const tooltip = document.getElementById('tooltip');
                if (tooltip) {
                    tooltip.remove();
                }
            });
        });
    });

    // ==========================================
    // Export Utility Functions
    // ==========================================

    window.MediSure = {
        debounce,
        formatCurrency,
        formatDate,
        getCsrfToken,
        getCsrfHeader,
        showToast,
        copyToClipboard,
        printElement
    };

})();

