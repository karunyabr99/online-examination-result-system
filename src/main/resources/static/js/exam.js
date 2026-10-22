/**
 * ExamFlow - MCQ Examination Interface Interactive Engine
 * Handles timer countdown, question pagination, answer tracking, palette updates, and auto-submission.
 */

document.addEventListener('DOMContentLoaded', () => {
    const questionCards = document.querySelectorAll('.question-card');
    const totalQuestions = questionCards.length;
    if (totalQuestions === 0) return;

    let currentIndex = 0;
    const examForm = document.getElementById('examForm');
    const prevBtn = document.getElementById('prevQuestionBtn');
    const nextBtn = document.getElementById('nextQuestionBtn');
    const clearBtn = document.getElementById('clearAnswerBtn');
    const submitModalBtn = document.getElementById('openSubmitModalBtn');
    const confirmSubmitBtn = document.getElementById('confirmSubmitBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    const submitModal = document.getElementById('submitModal');
    const answeredCountEl = document.getElementById('answeredCount');
    const unansweredCountEl = document.getElementById('unansweredCount');
    const modalAnsweredCount = document.getElementById('modalAnsweredCount');
    const modalUnansweredCount = document.getElementById('modalUnansweredCount');
    const timerDisplay = document.getElementById('timerDisplay');

    // 1. Navigation Functions
    function showQuestion(index) {
        if (index < 0 || index >= totalQuestions) return;

        questionCards.forEach((card, idx) => {
            if (idx === index) {
                card.classList.add('active');
            } else {
                card.classList.remove('active');
            }
        });

        // Update Palette 'current' indicator
        const paletteButtons = document.querySelectorAll('.palette-btn');
        paletteButtons.forEach((btn, idx) => {
            if (idx === index) {
                btn.classList.add('current');
            } else {
                btn.classList.remove('current');
            }
        });

        // Update Prev / Next buttons state
        if (prevBtn) {
            prevBtn.disabled = (index === 0);
        }
        if (nextBtn) {
            if (index === totalQuestions - 1) {
                nextBtn.style.display = 'none';
            } else {
                nextBtn.style.display = 'inline-flex';
            }
        }

        currentIndex = index;
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            if (currentIndex > 0) showQuestion(currentIndex - 1);
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            if (currentIndex < totalQuestions - 1) showQuestion(currentIndex + 1);
        });
    }

    // Palette direct question jumping
    const paletteButtons = document.querySelectorAll('.palette-btn');
    paletteButtons.forEach((btn) => {
        btn.addEventListener('click', () => {
            const targetIdx = parseInt(btn.getAttribute('data-index'), 10);
            showQuestion(targetIdx);
        });
    });

    // 2. Answer Selection Tracking
    function updateCountsAndPalette() {
        let answered = 0;

        questionCards.forEach((card, idx) => {
            const checkedRadio = card.querySelector('input[type="radio"]:checked');
            const paletteBtn = document.querySelector(`.palette-btn[data-index="${idx}"]`);

            // Update option card visual selection styling
            const optionCards = card.querySelectorAll('.option-card');
            optionCards.forEach((optCard) => {
                const radio = optCard.querySelector('input[type="radio"]');
                if (radio && radio.checked) {
                    optCard.classList.add('selected');
                } else {
                    optCard.classList.remove('selected');
                }
            });

            if (checkedRadio) {
                answered++;
                if (paletteBtn) paletteBtn.classList.add('answered');
            } else {
                if (paletteBtn) paletteBtn.classList.remove('answered');
            }
        });

        const unanswered = totalQuestions - answered;

        if (answeredCountEl) answeredCountEl.textContent = answered;
        if (unansweredCountEl) unansweredCountEl.textContent = unanswered;
        if (modalAnsweredCount) modalAnsweredCount.textContent = answered;
        if (modalUnansweredCount) modalUnansweredCount.textContent = unanswered;
    }

    // Listen to option changes
    const optionCards = document.querySelectorAll('.option-card');
    optionCards.forEach((card) => {
        card.addEventListener('click', (e) => {
            const radio = card.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = true;
                updateCountsAndPalette();
            }
        });
    });

    // Clear answer button
    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            const currentCard = questionCards[currentIndex];
            if (currentCard) {
                const radios = currentCard.querySelectorAll('input[type="radio"]');
                radios.forEach((r) => { r.checked = false; });
                updateCountsAndPalette();
            }
        });
    }

    // 3. Confirmation Modal
    if (submitModalBtn && submitModal) {
        submitModalBtn.addEventListener('click', () => {
            updateCountsAndPalette();
            submitModal.classList.add('active');
        });
    }

    if (cancelModalBtn && submitModal) {
        cancelModalBtn.addEventListener('click', () => {
            submitModal.classList.remove('active');
        });
    }

    // Double-submit prevention
    let isSubmitting = false;
    if (confirmSubmitBtn && examForm) {
        confirmSubmitBtn.addEventListener('click', () => {
            if (isSubmitting) return;
            isSubmitting = true;
            confirmSubmitBtn.disabled = true;
            confirmSubmitBtn.innerHTML = '<span>Submitting & Evaluating...</span>';
            examForm.submit();
        });
    }

    // 4. Countdown Timer Engine
    const timerContainer = document.querySelector('.exam-timer-box');
    const initialDuration = parseInt(timerContainer ? timerContainer.getAttribute('data-duration-seconds') : '600', 10);
    let remainingSeconds = initialDuration > 0 ? initialDuration : 600;

    function formatTime(seconds) {
        const m = Math.floor(seconds / 60);
        const s = seconds % 60;
        return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    }

    if (timerDisplay) {
        timerDisplay.textContent = formatTime(remainingSeconds);

        const timerInterval = setInterval(() => {
            remainingSeconds--;

            if (remainingSeconds <= 0) {
                clearInterval(timerInterval);
                timerDisplay.textContent = '00:00';
                if (!isSubmitting) {
                    isSubmitting = true;
                    alert('Time has expired! Submitting your examination answers automatically for evaluation.');
                    examForm.submit();
                }
                return;
            }

            timerDisplay.textContent = formatTime(remainingSeconds);

            // Warning style if under 2 minutes
            if (remainingSeconds <= 120 && timerContainer) {
                timerContainer.classList.add('timer-warning');
            }
        }, 1000);
    }

    // Initialize first question and counts
    showQuestion(0);
    updateCountsAndPalette();
});
