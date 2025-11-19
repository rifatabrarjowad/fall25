-- ======================================================
-- AI Club Management Database - COSC 3337 Term Project
-- ======================================================
DROP TABLE IF EXISTS Budget_Request;
DROP TABLE IF EXISTS Project_Member;
DROP TABLE IF EXISTS Project;
DROP TABLE IF EXISTS Attendance;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Member_Role;
DROP TABLE IF EXISTS Role;
DROP TABLE IF EXISTS Member;

-- Member table
CREATE TABLE Member (
    member_id      INTEGER PRIMARY KEY,
    first_name     TEXT NOT NULL,
    last_name      TEXT NOT NULL,
    email          TEXT NOT NULL UNIQUE,
    major          TEXT,
    class_year     INTEGER,
    join_date      DATE NOT NULL,
    is_student     INTEGER NOT NULL CHECK (is_student IN (0, 1))
);

-- Role table
CREATE TABLE Role (
    role_id    INTEGER PRIMARY KEY,
    role_name  TEXT NOT NULL UNIQUE
);

-- Member_Role associative table
CREATE TABLE Member_Role (
    member_id   INTEGER NOT NULL,
    role_id     INTEGER NOT NULL,
    start_date  DATE NOT NULL,
    end_date    DATE,
    PRIMARY KEY (member_id, role_id, start_date),
    FOREIGN KEY (member_id) REFERENCES Member(member_id),
    FOREIGN KEY (role_id)   REFERENCES Role(role_id)
);

-- Event table
CREATE TABLE Event (
    event_id         INTEGER PRIMARY KEY,
    title            TEXT NOT NULL,
    event_date       DATE NOT NULL,
    start_time       TIME,
    end_time         TIME,
    location         TEXT,
    event_type       TEXT NOT NULL CHECK (event_type IN ('Workshop','Study Session','Competition','Social')),
    budget_approved  REAL NOT NULL DEFAULT 0
);

-- Attendance table
CREATE TABLE Attendance (
    member_id      INTEGER NOT NULL,
    event_id       INTEGER NOT NULL,
    status         TEXT NOT NULL CHECK (status IN ('ATTENDED','ABSENT','RSVP')),
    check_in_time  DATETIME,
    PRIMARY KEY (member_id, event_id),
    FOREIGN KEY (member_id) REFERENCES Member(member_id),
    FOREIGN KEY (event_id)  REFERENCES Event(event_id)
);

-- Project table
CREATE TABLE Project (
    project_id   INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    description  TEXT,
    start_date   DATE NOT NULL,
    status       TEXT NOT NULL CHECK (status IN ('PLANNED','ACTIVE','COMPLETED'))
);

-- Project_Member associative table
CREATE TABLE Project_Member (
    project_id      INTEGER NOT NULL,
    member_id       INTEGER NOT NULL,
    role_in_project TEXT NOT NULL,
    joined_on       DATE NOT NULL,
    PRIMARY KEY (project_id, member_id),
    FOREIGN KEY (project_id) REFERENCES Project(project_id),
    FOREIGN KEY (member_id)  REFERENCES Member(member_id)
);

-- Budget_Request table
CREATE TABLE Budget_Request (
    request_id       INTEGER PRIMARY KEY,
    event_id         INTEGER,
    project_id       INTEGER,
    amount_requested REAL NOT NULL CHECK (amount_requested > 0),
    amount_approved  REAL,
    status           TEXT NOT NULL CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    submitted_on     DATE NOT NULL,
    decided_on       DATE,
    FOREIGN KEY (event_id)   REFERENCES Event(event_id),
    FOREIGN KEY (project_id) REFERENCES Project(project_id),
    -- Ensure at least one of event_id or project_id is not NULL
    CHECK (event_id IS NOT NULL OR project_id IS NOT NULL)
);

-- Index to speed up attendance lookups by event
CREATE INDEX idx_attendance_event
    ON Attendance (event_id);

-- Another useful index by date and type of event
CREATE INDEX idx_event_date_type
    ON Event (event_date, event_type);

CREATE TRIGGER trg_update_event_budget
AFTER UPDATE OF status, amount_approved
ON Budget_Request
FOR EACH ROW
WHEN NEW.status = 'APPROVED' AND NEW.event_id IS NOT NULL AND NEW.amount_approved IS NOT NULL
BEGIN
    UPDATE Event
    SET budget_approved = NEW.amount_approved
    WHERE event_id = NEW.event_id;
END;

-- Sample Data
-- Insert Members
INSERT INTO Member (member_id, first_name, last_name, email, major, class_year, join_date, is_student) VALUES
(1, 'Rifat', 'Jowad', 'rifat@example.edu', 'Computer Science', 2026, '2024-01-15', 1),
(2, 'Sara', 'Lopez', 'sara.lopez@example.edu', 'Data Science', 2025, '2024-01-20', 1),
(3, 'Ali', 'Khan',  'ali.khan@example.edu',  'Mathematics',     2027, '2024-02-01', 1),
(4, 'Mia', 'Chen',  'mia.chen@example.edu',  'Computer Science', 2026, '2024-02-10', 1),
(5, 'Dr',  'Atkinson', 'gentry.atkinson@example.edu', 'Faculty', 0, '2024-01-01', 0);

-- Insert Roles
INSERT INTO Role (role_id, role_name) VALUES
(1, 'President'),
(2, 'Vice President'),
(3, 'Treasurer'),
(4, 'Event Coordinator'),
(5, 'Faculty Advisor');

-- Insert Member_Role
INSERT INTO Member_Role (member_id, role_id, start_date, end_date) VALUES
(1, 1, '2024-01-15', NULL),
(2, 2, '2024-01-20', NULL),
(3, 3, '2024-02-01', NULL),
(4, 4, '2024-02-10', NULL),
(5, 5, '2024-01-01', NULL);

-- Insert Events
INSERT INTO Event (event_id, title, event_date, start_time, end_time, location, event_type, budget_approved) VALUES
(1, 'Intro to AI & Club Kickoff', '2024-02-20', '18:00', '19:30', 'Room 101', 'Social',  100),
(2, 'Python for Machine Learning Workshop', '2024-03-05', '18:00', '20:00', 'Lab 202', 'Workshop', 150),
(3, 'Study Session: Linear Algebra for ML', '2024-03-12', '17:00', '19:00', 'Library 3rd Floor', 'Study Session', 0),
(4, 'Prompt Engineering Competition', '2024-04-01', '18:00', '20:30', 'Room 105', 'Competition', 200);

-- Insert Attendance
INSERT INTO Attendance (member_id, event_id, status, check_in_time) VALUES
(1, 1, 'ATTENDED', '2024-02-20 18:05'),
(2, 1, 'ATTENDED', '2024-02-20 18:10'),
(3, 1, 'ABSENT',   NULL),
(4, 1, 'ATTENDED', '2024-02-20 18:15'),

(1, 2, 'ATTENDED', '2024-03-05 18:01'),
(2, 2, 'ATTENDED', '2024-03-05 18:03'),
(3, 2, 'ATTENDED', '2024-03-05 18:05'),

(1, 3, 'ATTENDED', '2024-03-12 17:00'),
(4, 3, 'RSVP',     NULL),

(2, 4, 'ATTENDED', '2024-04-01 18:10'),
(3, 4, 'ATTENDED', '2024-04-01 18:05'),
(4, 4, 'ATTENDED', '2024-04-01 18:00');

-- Insert Projects
INSERT INTO Project (project_id, name, description, start_date, status) VALUES
(1, 'Banglish Sentiment Classifier', 'Classify mixed Bangla-English ecommerce reviews.', '2024-03-01', 'ACTIVE'),
(2, 'AI Club Website', 'Static site for events and resources.', '2024-03-15', 'ACTIVE'),
(3, 'SAT Math Tutor Bot', 'Chatbot that helps students practice SAT math.', '2024-04-01', 'PLANNED');

-- Insert Project_Member
INSERT INTO Project_Member (project_id, member_id, role_in_project, joined_on) VALUES
(1, 1, 'Lead',        '2024-03-01'),
(1, 2, 'Contributor', '2024-03-02'),
(1, 3, 'Contributor', '2024-03-03'),
(2, 2, 'Lead',        '2024-03-15'),
(2, 4, 'Contributor', '2024-03-16'),
(3, 1, 'Lead',        '2024-04-01');

-- Insert Budget_Request
INSERT INTO Budget_Request (request_id, event_id, project_id, amount_requested, amount_approved, status, submitted_on, decided_on) VALUES
(1, 1, NULL, 120, 100, 'APPROVED', '2024-02-10', '2024-02-15'),
(2, 2, NULL, 200, 150, 'APPROVED', '2024-02-25', '2024-03-01'),
(3, 4, NULL, 250, NULL, 'PENDING', '2024-03-20', NULL),
(4, NULL, 1, 300, 250, 'APPROVED', '2024-03-10', '2024-03-18');


-- Q1: JOIN QUERY
-- Description: List each event with the number of members who ATTENDED it, ordered by attendance count in descending order.
SELECT
    e.event_id,
    e.title,
    e.event_date,
    COUNT(CASE WHEN a.status = 'ATTENDED' THEN 1 END) AS attendees_count
FROM Event e
LEFT JOIN Attendance a ON e.event_id = a.event_id
GROUP BY e.event_id, e.title, e.event_date
ORDER BY attendees_count DESC;


-- Q2: SUBQUERY
-- Description: Find members who have never ATTENDED any event.
SELECT
    m.member_id,
    m.first_name,
    m.last_name,
    m.email
FROM Member m
WHERE m.member_id NOT IN (
    SELECT DISTINCT member_id
    FROM Attendance
    WHERE status = 'ATTENDED'
);


-- Q3: WINDOW FUNCTION
-- Description: For each member who has attended at least one event, show the
-- total events they attended and their rank by attendance count (1 = most active).
WITH attendance_counts AS (
    SELECT
        m.member_id,
        m.first_name,
        m.last_name,
        COUNT(CASE WHEN a.status = 'ATTENDED' THEN 1 END) AS attended_count
    FROM Member m
    JOIN Attendance a ON m.member_id = a.member_id
    GROUP BY m.member_id, m.first_name, m.last_name
)
SELECT
    member_id,
    first_name,
    last_name,
    attended_count,
    RANK() OVER (ORDER BY attended_count DESC) AS activity_rank
FROM attendance_counts
ORDER BY activity_rank, last_name;


-- Q4: WINDOW FUNCTION (EVENTS)
-- Description: For each event, show its attendance count and the average attendance per event type using a window function.
WITH event_attendance AS (
    SELECT
        e.event_id,
        e.title,
        e.event_type,
        COUNT(CASE WHEN a.status = 'ATTENDED' THEN 1 END) AS attendees_count
    FROM Event e
    LEFT JOIN Attendance a ON e.event_id = a.event_id
    GROUP BY e.event_id, e.title, e.event_type
)
SELECT
    event_id,
    title,
    event_type,
    attendees_count,
    AVG(attendees_count) OVER (PARTITION BY event_type) AS avg_attendance_for_type
FROM event_attendance
ORDER BY event_type, event_id;


-- Q5: COMPLEX JOIN + FILTER
-- Description: List all active projects with the number of members in each project, and show only projects with at least 2 members.
SELECT
    p.project_id,
    p.name,
    p.status,
    COUNT(pm.member_id) AS member_count
FROM Project p
LEFT JOIN Project_Member pm ON p.project_id = pm.project_id
WHERE p.status = 'ACTIVE'
GROUP BY p.project_id, p.name, p.status
HAVING COUNT(pm.member_id) >= 2
ORDER BY member_count DESC;


-- Q6: TRANSACTION (UPDATE + INSERT)
-- Description: Example transaction: approve a pending budget request for the 'Prompt Engineering Competition' event and update its event budget. (In a real DB, you would adjust IDs and maybe include a ROLLBACK example.)
BEGIN TRANSACTION;

-- Step 1: Approve a specific pending budget request (ID = 3) and set approved amount.
UPDATE Budget_Request
SET status = 'APPROVED',
    amount_approved = 220,
    decided_on = DATE('2024-03-25')
WHERE request_id = 3
  AND status = 'PENDING';

-- Step 2: Log the approval in a simple logging table (if you create one),
-- or just select the updated request to confirm.
-- For now, we simply select the updated row.
SELECT * FROM Budget_Request WHERE request_id = 3;

COMMIT;