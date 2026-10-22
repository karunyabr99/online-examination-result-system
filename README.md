# ExamFlow — Online Examination and Result Processing System

[![Java Version](https://img.shields.io/badge/Java-21%20LTS-007396?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-SQLite%203-003B57?style=flat-square&logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Build Tool](https://img.shields.io/badge/Build-Maven%203.9-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-18%20Passed-success?style=flat-square)](file:///Applications/online-examination-result-system)

> **Academic Microproject for BTech Computer Science & Engineering (CSE)**  
> Target GitHub Repository: [https://github.com/karunyabr99/online-examination-result-system](https://github.com/karunyabr99/online-examination-result-system)

---

## 1. Project Overview

**ExamFlow** is an institutional, full-stack online examination and automated result processing portal built from the ground up for academic microproject demonstration. Engineered using **Java 21**, **Spring Boot 3.3.4**, **SQLite 3**, and **Thymeleaf**, the system provides a tamper-resistant environment for conducting computer-based multiple-choice examinations (MCQs) with instant server-side grading and rich analytical reporting.

Unlike basic toy apps, **ExamFlow** incorporates enterprise architectural patterns:
1. **Zero Client Leakage:** Correct answers and rationale are strictly kept on the server; candidates only receive sanitized question payloads.
2. **Atomic Server Evaluation:** Scores, percentages, and PASS/FAIL marks are computed on the backend within transactional SQLite operations.
3. **Double Submission Prevention:** Each exam session is guarded by a cryptographically secure UUID (`attempt_uuid`) ensuring an attempt can only be evaluated once.
4. **Relational Database Integrity:** SQLite foreign-key enforcement (`PRAGMA foreign_keys = ON;`), WAL journaling, and parameterized prepared statements prevent SQL injection.

---

## 2. Key Features

### 🎓 Candidate & Examination Portal
- **Student Registration & Verification:** Form-based student registration with roll number validation and active curriculum exam selection.
- **Pre-Exam Instructions:** Displays total questions, marks, passing criteria, and time allowance before the candidate initiates their attempt.
- **Interactive MCQ Examination Console:**
  - One-question-at-a-time focus view with smooth pagination.
  - Radio card selection with instant visual feedback.
  - Interactive **Question Palette** showing answered (green), unanswered (neutral), and active (blue ring) states.
  - Real-time client countdown timer with a pulse alert when under 2 minutes.
  - Automatic submission when time expires.
  - Confirmation modal detailing answered vs. unanswered tallies before finalizing.
- **Official Scorecard & Certificate:**
  - Dynamic PASS (green) / FAIL (red) banner.
  - Percentage computation using `(Marks Obtained / Maximum Marks) * 100`.
  - Detailed breakdown: Correct, Incorrect, Unattempted, and Total Time Taken.
  - Dedicated **Print View (`@media print`)** for generating clean official transcripts or PDF reports directly from the browser.

### 🛡️ Administration & Result Processing
- **Protected Faculty / Admin Area:** Protected by `AdminAuthInterceptor` session verification and BCrypt password encryption.
- **Real-Time Analytical Dashboard:**
  - Dynamic statistics from SQLite: Distinct candidates, total attempts, pass rate, average scores, and failures.
  - Candidate search by student name or roll number.
  - Multi-criteria filtering by examination title and PASS/FAIL status.
  - **One-Click CSV Export:** Generates formatted `examflow_results_*.csv` file for external academic record-keeping.
- **Detailed Attempt Audit Trail:** Faculty can review any candidate attempt question-by-question, comparing the candidate's chosen option with the correct key and technical explanation.
- **Question Bank Management:** Add, edit, and delete questions with validation (ensuring all 4 choices and a valid correct option A/B/C/D are populated).

---

## 3. Technology Stack

| Component | Technology | Version / Specification |
|---|---|---|
| **Programming Language** | Java (OpenJDK / Oracle JDK) | **Java 21 LTS** |
| **Backend Framework** | Spring Boot | **3.3.4** |
| **Web Tier** | Spring MVC & Thymeleaf | HTML5, CSS3, ES6 JavaScript |
| **Database Access** | Spring JDBC (`JdbcTemplate`) | SQLite JDBC `3.45.3.0` |
| **Database Engine** | SQLite 3 | File-based `examflow.db` |
| **Security & Hashing** | Spring Security Crypto | `BCryptPasswordEncoder` |
| **Build & Dependency Tool** | Apache Maven & Maven Wrapper (`./mvnw`) | Maven `3.9.6` |
| **Testing Frameworks** | JUnit 5, Mockito 5, AssertJ, Spring Test | 18 Automated Unit & Integration Tests |
| **Operating System** | macOS (Apple Silicon / Intel) | Terminal & VS Code |

---

## 4. Database Architecture & Schema

The relational schema is configured in `src/main/resources/schema.sql` and automatically initialized on startup with `PRAGMA foreign_keys = ON;`.

```
     +-----------------+          +-----------------+
     |    students     |          |      exams      |
     +-----------------+          +-----------------+
     | id (PK)         |          | id (PK)         |
     | student_id (UQ) |          | title           |
     | full_name       |          | duration_minutes|
     | email, course   |          | passing_pct     |
     +--------+--------+          +----+-------+----+
              |                        |       |
              | 1                      | 1     | 1
              |                        |       |
              |        N               |       | N
     +--------v--------+---------------+       |
     |  exam_attempts  <---------------+       |
     +-----------------+                       |
     | id (PK)         |                       |
     | attempt_uuid(UQ)|                       |
     | started_at      |                       |
     | status          |                       |
     +--------+--------+                       |
              |                                |
              | 1 (1:1)                        |
              v                                |
     +-----------------+                       |
     |     results     |                       |
     +-----------------+                       |
     | score, total    |                       |
     | percentage      |                       |
     | pass_fail       |                       |
     +-----------------+                       |
              |                                |
              | 1                              v
              |               N       +-----------------+
              +----------------------->    questions    |
              |                       +-----------------+
              |                       | id (PK)         |
              |                       | option_a,b,c,d  |
              |                       | correct_option  |
              |                       | marks           |
              |                       +--------+--------+
              |                                |
              |        N                       | 1
              +----------------v---------------+
                               |
                        +------v----------+
                        |     answers     |
                        +-----------------+
                        | selected_option |
                        | is_correct      |
                        | marks_awarded   |
                        +-----------------+
```

---

## 5. System Screenshots (Placeholders)

| Screen | Description | Preview |
|---|---|---|
| **Portal Home** | Dynamic stats cards, exam listings, hero section | `[Screenshot: Portal Home & Statistics]` |
| **Candidate Entry** | Student roll number, name, course entry form | `[Screenshot: Candidate Registration]` |
| **Exam Console** | Live timer, question palette, MCQ selection | `[Screenshot: Timed MCQ Interface]` |
| **Scorecard** | PASS/FAIL banner, percentage, print button | `[Screenshot: Student Result Card]` |
| **Admin Dashboard** | Filters, metric tiles, attempts table, CSV export | `[Screenshot: Admin Results Dashboard]` |
| **Question Audit** | Detailed question-by-question candidate breakdown | `[Screenshot: Faculty Question Audit]` |

---

## 6. Local Setup and Execution on macOS

### Prerequisites
1. **Java 21 LTS** installed:
   ```bash
   # Verify your Java installation
   java -version
   # Expected output: openjdk version "21.x.x" or java version "21.x.x"
   ```
2. **macOS Terminal** or **Visual Studio Code** terminal.

### Quick Start (Running via Maven Wrapper)
Clone or navigate to the project root directory:
```bash
cd /Applications/online-examination-result-system
```

Set `JAVA_HOME` (if not already set globally):
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
```

Build the project and run all automated tests:
```bash
./mvnw clean test
```

Start the application:
```bash
./mvnw spring-boot:run
```

Once started, open your web browser:
- **Student Examination Portal:** [http://localhost:8080](http://localhost:8080)
- **Student Exam Registration:** [http://localhost:8080/exam/register](http://localhost:8080/exam/register)
- **Faculty / Admin Login:** [http://localhost:8080/admin/login](http://localhost:8080/admin/login)

---

## 7. Demonstration Walkthrough

### Candidate Demonstration Workflow:
1. Navigate to `http://localhost:8080` and click **Start Examination**.
2. Select **Java & Database Foundations Assessment**.
3. Enter Candidate Details:
   - **Full Name:** `Yashwanth Naik`
   - **Student ID:** `1MS21CS099`
   - **Department / Course:** `BTech CSE - Semester 6`
4. Review the examination duration (10 minutes) and rules, then click **Begin Examination Now**.
5. Practice answering questions:
   - Notice the **Question Palette** numbers dynamically turn **green** as answers are selected.
   - Use **Previous** and **Next** or jump directly using the palette numbers.
   - Observe the live countdown timer at the top right.
6. Click **Finish Examination &check;**. Confirm your submission in the modal dialog.
7. Inspect the **Official Result Transcript Card** displaying the calculated percentage, marks obtained, and the prominent **PASS** or **FAIL** status badge.
8. Click **Print Transcript / PDF** to see the print-formatted view.

### Administrator Demonstration Workflow:
1. Open [http://localhost:8080/admin/login](http://localhost:8080/admin/login).
2. Enter the local demonstration credentials:
   - **Username:** `admin`
   - **Password:** `admin123`
3. Inspect the live **Results Dashboard**:
   - Verify that your newly completed test attempt is present in the table.
   - Search by student ID `1MS21CS099` or name.
   - Filter by status (`PASS` or `FAIL`).
4. Click **View Details** on any attempt to inspect the candidate's exact responses against the answer key.
5. Click **Export CSV Report** to download the comma-separated records file.
6. Navigate to **Question Bank** to add, edit, or delete questions.

---

## 8. Verification & Test Suite Summary

The application includes a comprehensive test suite executed with JUnit 5, Mockito, and Spring Boot Test:

```bash
./mvnw test
```

### Verified Test Cases:
| Test Class | Tests Run | Status | Description |
|---|---|---|---|
| `ExaminationServiceTest` | 5 | ✅ PASS | All correct answers (100%), all incorrect (0%), exact 40% pass boundary, 30% fail boundary, duplicate submission immunity |
| `ResultServiceTest` | 2 | ✅ PASS | CSV formatting with quote escaping, dashboard metric calculations |
| `StudentExamControllerTest` | 4 | ✅ PASS | Entry validation (blank fields rejected), instructions view forwarding, answer key leakage prevention |
| `AdminDashboardControllerTest` | 3 | ✅ PASS | Unauthenticated redirection, authenticated dashboard access, CSV attachment headers |
| `RepositoryIntegrationTest` | 4 | ✅ PASS | Real SQLite persistence: seed data availability, student CRUD, question lifecycle, end-to-end attempt/answer/result storage |
| **Total Test Suite** | **18** | **✅ 100% PASS** | **0 Failures, 0 Errors, 0 Skipped** |

---

## 9. Limitations & Production Enhancements

This microproject is engineered for academic demonstrations. For a mission-critical production deployment:
- **Server-Side Heartbeat Enforcement:** Implement WebSocket or server-sent interval heartbeats to enforce server-side expiration down to the millisecond.
- **Candidate Webcam Proctoring:** WebRTC stream capturing for automated malpractice monitoring.
- **Randomized Question Shuffling:** Dynamic question selection and option permutation per candidate.
- **Enterprise IAM Integration:** Single Sign-On (SSO) via OAuth2 / SAML (Google Workspace or Microsoft Entra ID).
- **Distributed RDBMS:** Migration from SQLite to PostgreSQL or CockroachDB for multi-node horizontal write concurrency.

---

## 10. Git & GitHub Preparation

The local repository is initialized and configured for the remote repository:
```bash
git remote -v
# origin  https://github.com/karunyabr99/online-examination-result-system.git (fetch)
# origin  https://github.com/karunyabr99/online-examination-result-system.git (push)
```

To stage and push to GitHub (with appropriate credentials):
```bash
git add .
git commit -m "feat: complete Online Examination and Result Processing System with Java 21, Spring Boot 3, and SQLite"
git branch -M main
git push -u origin main
```

---
*Developed with ❤️ for BTech CSE Academic Microproject Demonstration &bull; ExamFlow 2026*
