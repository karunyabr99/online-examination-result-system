/**
 * ExamFlow - Global Client-side Utilities
 */

document.addEventListener('DOMContentLoaded', () => {
    // Print button handler
    const printBtn = document.getElementById('printResultBtn');
    if (printBtn) {
        printBtn.addEventListener('click', () => {
            window.print();
        });
    }

    // Auto-dismiss flash alerts after 5 seconds
    const flashAlerts = document.querySelectorAll('.alert-dismissible');
    flashAlerts.forEach((alert) => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});
