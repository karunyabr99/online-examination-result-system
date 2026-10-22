# ExamFlow — Technical Architecture & Database Specification

## 1. System Overview
**ExamFlow** is an institutional web-based examination and result processing platform engineered for academic microproject demonstration in BTech Computer Science & Engineering. The system strictly separates client presentation from server-side grading integrity.

---

## 2. Layered Architecture

```
+-------------------------------------------------------------------+
|                         CLIENT LAYER                              |
|   HTML5, CSS3 Custom Tokens, JavaScript ES6+, Responsive Layout   |
+----------------------------------+--------------------------------+
                                   | HTTP / HTTPS
+----------------------------------v--------------------------------+
|                      PRESENTATION CONTROLLERS                     |
|  - HomeController         - StudentExamController                 |
|  - AdminAuthController    - AdminDashboardController              |
|  - AdminQuestionController                                        |
+----------------------------------+--------------------------------+
                                   | DTOs & Validation
+----------------------------------v--------------------------------+
|                        SERVICE / BUSINESS LAYER                   |
|  - ExaminationService: Registration, Attempt Lifecycle, Grading   |
|  - ResultService: Metrics Calculation, Filtering, CSV Export      |
|  - QuestionService: Authoring, Question Sanitization              |
|  - AuthService: BCrypt Password Hashing, Session Management       |
+----------------------------------+--------------------------------+
                                   | Entities / Queries
+----------------------------------v--------------------------------+
|                     DATA ACCESS LAYER (Spring JDBC)               |
|  - StudentRepository      - ExamRepository                        |
|  - QuestionRepository     - ExamAttemptRepository                 |
|  - AnswerRepository       - ResultRepository                      |
|  - AdminUserRepository                                            |
+----------------------------------+--------------------------------+
                                   | SQLite JDBC Driver
+----------------------------------v--------------------------------+
|                     DATABASE LAYER (SQLite 3)                     |
|  - Foreign Key Constraints Enforced (PRAGMA foreign_keys = ON)    |
|  - WAL Journaling Mode, Parameterized Statements                  |
+-------------------------------------------------------------------+
```

---

## 3. Relational Database Schema

### Table: `students`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Surrogate internal key |
| `student_id` | TEXT | UNIQUE NOT NULL | Candidate roll number / registration ID |
| `full_name` | TEXT | NOT NULL | Candidate full legal name |
| `email` | TEXT | NULL | Notification email address |
| `course` | TEXT | NULL | Academic degree / class stream |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Registration timestamp |

### Table: `exams`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `title` | TEXT | NOT NULL | Examination title |
| `subject` | TEXT | NOT NULL | Subject / Module title |
| `description` | TEXT | NULL | Syllabus and scope description |
| `duration_minutes` | INTEGER | NOT NULL DEFAULT 10 | Duration in minutes |
| `total_marks` | INTEGER | NOT NULL DEFAULT 10 | Maximum attainable marks |
| `passing_percentage` | REAL | NOT NULL DEFAULT 40.0 | Benchmark passing percentage |
| `is_active` | INTEGER | NOT NULL DEFAULT 1 | 1 = Active, 0 = Inactive |

### Table: `questions`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `exam_id` | INTEGER | NOT NULL, FK -> `exams(id)` | Associated exam |
| `question_text` | TEXT | NOT NULL | Question prompt |
| `option_a` | TEXT | NOT NULL | Choice A |
| `option_b` | TEXT | NOT NULL | Choice B |
| `option_c` | TEXT | NOT NULL | Choice C |
| `option_d` | TEXT | NOT NULL | Choice D |
| `correct_option` | TEXT | NOT NULL ('A', 'B', 'C', 'D') | Correct answer key |
| `marks` | INTEGER | NOT NULL DEFAULT 1 | Marks awarded for correct key |
| `explanation` | TEXT | NULL | Educational rationale |

### Table: `exam_attempts`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `attempt_uuid` | TEXT | UNIQUE NOT NULL | Cryptographic UUIDv4 identifier |
| `student_id` | INTEGER | NOT NULL, FK -> `students(id)` | Candidate reference |
| `exam_id` | INTEGER | NOT NULL, FK -> `exams(id)` | Exam reference |
| `started_at` | TIMESTAMP | NOT NULL | Attempt start timestamp |
| `submitted_at` | TIMESTAMP | NULL | Submission timestamp |
| `status` | TEXT | NOT NULL | `IN_PROGRESS`, `COMPLETED`, `TIMED_OUT` |

### Table: `answers`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `attempt_id` | INTEGER | NOT NULL, FK -> `exam_attempts(id)` | Attempt reference |
| `question_id` | INTEGER | NOT NULL, FK -> `questions(id)` | Question reference |
| `selected_option` | TEXT | NULL ('A', 'B', 'C', 'D', NULL) | Candidate selected choice |
| `is_correct` | INTEGER | NOT NULL DEFAULT 0 | 1 = Correct, 0 = Incorrect |
| `marks_awarded` | INTEGER | NOT NULL DEFAULT 0 | Marks scored |

### Table: `results`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `attempt_id` | INTEGER | UNIQUE NOT NULL, FK -> `exam_attempts(id)` | 1:1 relation with attempt |
| `score` | INTEGER | NOT NULL | Total marks scored |
| `total_marks` | INTEGER | NOT NULL | Maximum test marks |
| `percentage` | REAL | NOT NULL | Formula: `(Score / TotalMarks) * 100` |
| `pass_fail` | TEXT | NOT NULL | `PASS` or `FAIL` |
| `evaluated_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Evaluation completion timestamp |

### Table: `admin_users`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | Primary key |
| `username` | TEXT | UNIQUE NOT NULL | Admin login handle |
| `password_hash` | TEXT | NOT NULL | BCrypt encrypted hash |
| `full_name` | TEXT | NOT NULL | Administrator name |
| `role` | TEXT | NOT NULL DEFAULT 'ADMIN' | Role authorization |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

---

## 4. Evaluation and Security Guarantees

1. **Answer Masking:** The `QuestionService.getSanitizedQuestionsForExam()` returns `SanitizedQuestionDto` instances with `correctOption` and `explanation` stripped out before dispatching HTML to candidate clients.
2. **Duplicate Submission Immunity:** The `ExaminationService.evaluateAndSaveAttempt()` checks the attempt status. If an attempt is already marked `COMPLETED`, it rejects re-evaluation and returns the existing result record.
3. **Database Transactions:** Evaluation updates attempt status, inserts candidate answers in batch, and writes the summary result row inside an atomic Spring `@Transactional` boundary.
4. **Foreign Key Integrity:** SQLite enforces relational integrity via `PRAGMA foreign_keys = ON;`.
