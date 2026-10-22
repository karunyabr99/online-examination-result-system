-- Seed Data for ExamFlow System

-- 1. Insert Initial Exams
INSERT OR IGNORE INTO exams (id, title, subject, description, duration_minutes, total_marks, passing_percentage, is_active)
VALUES (1, 'Java & Database Foundations Assessment', 'BTech CSE - Core CS', 'Comprehensive assessment covering Java language internals, memory management, OOP, SQL queries, normalization, and ACID properties.', 10, 10, 40.0, 1);

INSERT OR IGNORE INTO exams (id, title, subject, description, duration_minutes, total_marks, passing_percentage, is_active)
VALUES (2, 'Object-Oriented Programming with Java', 'Java Programming', 'Detailed examination on OOP pillars, interfaces, exception handling, and Java 21 modern language features.', 15, 5, 50.0, 1);

-- 2. Insert Questions for Exam 1 (10 MCQs)
INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (1, 1, 
'Which component in the Java Virtual Machine is primarily responsible for converting frequently executed bytecode into native machine instructions at runtime?',
'Java Development Kit (JDK)',
'Just-In-Time (JIT) Compiler',
'Bootstrap ClassLoader',
'Garbage Collector',
'B', 1, 'The JIT Compiler analyzes bytecode execution patterns and compiles hot spots into direct machine code for performance acceleration.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (2, 1, 
'What is the fundamental difference between String, StringBuilder, and StringBuffer in Java?',
'String is mutable; StringBuilder is immutable and thread-safe',
'String is immutable; StringBuilder is mutable and not thread-safe; StringBuffer is mutable and thread-safe',
'StringBuffer is faster than StringBuilder in single-threaded environments',
'StringBuilder is synchronized while StringBuffer is unsynchronized',
'B', 1, 'String objects are immutable. StringBuilder is mutable and non-synchronized (faster). StringBuffer is mutable and thread-safe via synchronized methods.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (3, 1, 
'Which of the following is NOT one of the standard ACID properties governing relational database transactions?',
'Atomicity',
'Consistency',
'Accessibility',
'Durability',
'C', 1, 'ACID stands for Atomicity, Consistency, Isolation, and Durability. Accessibility is not an ACID property.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (4, 1, 
'In the Java Collections Framework, which data structure maintains elements in insertion order, permits null elements, and implements both List and Deque interfaces?',
'HashSet',
'TreeSet',
'LinkedList',
'PriorityQueue',
'C', 1, 'LinkedList implements both List and Deque, preserves insertion order, allows null values, and is backed by a doubly-linked list.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (5, 1, 
'Which SQL clause is used to filter groups produced by an aggregate query using the GROUP BY clause?',
'WHERE',
'HAVING',
'ORDER BY',
'LIMIT',
'B', 1, 'WHERE filters individual table rows prior to grouping, whereas HAVING filters groups after aggregation.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (6, 1, 
'What occurs if a Java thread invokes object.wait() without holding the synchronization monitor for that object?',
'Deadlock occurs immediately',
'IllegalMonitorStateException is thrown at runtime',
'The thread terminates silently without exception',
'InterruptedException is thrown by the JVM',
'B', 1, 'wait(), notify(), and notifyAll() require the calling thread to hold the monitor lock, otherwise IllegalMonitorStateException is thrown.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (7, 1, 
'In relational database design, a relation is in Third Normal Form (3NF) if and only if it is in 2NF and:',
'All multi-valued attributes are separated into child tables',
'No non-prime attribute is transitively dependent on any candidate key',
'Every determinant in the relation is a superkey',
'All composite keys are eliminated',
'B', 1, '3NF eliminates transitive dependencies of non-prime attributes on superkeys.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (8, 1, 
'Which modern Java language construct (standardized in Java 16+) provides a concise syntax for defining transparent, immutable data-carrier classes?',
'Sealed Classes',
'Records',
'Virtual Threads',
'Pattern Matching',
'B', 1, 'Records are immutable data carriers that automatically synthesize constructors, getters, equals, hashCode, and toString.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (9, 1, 
'In SQL, what is the exact behavior of an INNER JOIN between Table A and Table B?',
'Returns all rows from Table A with NULLs for unmatched rows in Table B',
'Returns only the rows that have matching values in both tables based on the join condition',
'Returns all rows from both tables unconditionally',
'Returns rows that exist in Table A but NOT in Table B',
'B', 1, 'INNER JOIN selects all records from both tables where the specified join predicate evaluates to true.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (10, 1, 
'Which SQLite statement must be executed on a connection to ensure foreign key constraint integrity is actively enforced?',
'PRAGMA foreign_keys = ON;',
'ALTER TABLE SYSTEM ENABLE CONSTRAINTS;',
'SET FOREIGN_KEY_CHECKS = 1;',
'PRAGMA integrity_check = STRICT;',
'A', 1, 'SQLite disables foreign key enforcement by default for backward compatibility; it must be enabled with PRAGMA foreign_keys = ON.');

-- 3. Insert Questions for Exam 2 (5 MCQs)
INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (11, 2,
'Which principle of Object-Oriented Programming is demonstrated when a subclass provides a specific implementation of a method declared in its superclass?',
'Encapsulation',
'Method Overriding (Runtime Polymorphism)',
'Method Overloading (Compile-time Polymorphism)',
'Data Abstraction',
'B', 1, 'Method overriding is dynamic polymorphism where a subclass provides its own implementation of a superclass method.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (12, 2,
'What is the requirement for a resource to be managed automatically in a Java try-with-resources block?',
'The class must extend Thread',
'The class must implement java.lang.AutoCloseable or java.io.Closeable',
'The class must be declared final',
'The class must implement java.io.Serializable',
'B', 1, 'try-with-resources automatically closes any resource that implements java.lang.AutoCloseable.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (13, 2,
'Can an interface in Java 8+ have concrete method implementations?',
'No, interfaces can only contain abstract methods',
'Yes, by declaring methods with the "default" or "static" keyword',
'Yes, but only if the class implementing it is abstract',
'No, only abstract classes allow method bodies',
'B', 1, 'Java 8 introduced default and static methods in interfaces with concrete implementations.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (14, 2,
'Which of the following exceptions is an unchecked (Runtime) exception in Java?',
'java.io.IOException',
'java.sql.SQLException',
'java.lang.NullPointerException',
'java.lang.ClassNotFoundException',
'C', 1, 'NullPointerException subclasses RuntimeException and is therefore an unchecked exception.');

INSERT OR IGNORE INTO questions (id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation)
VALUES (15, 2,
'What is the purpose of the super keyword in Java?',
'To refer to the current class instance',
'To refer to the immediate parent class object or call parent constructors and methods',
'To declare a class with superuser privileges',
'To prevent a method from being overridden',
'B', 1, 'The super keyword refers to the immediate parent class members and constructors.');
