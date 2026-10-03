# Student Database Management System

A Java Swing desktop application for managing students, courses, fees, marks, and library records. All data is stored in and retrieved from a MySQL database. The dashboard, student list, fees, marks, and library pages all reflect live data from the database.

---

## Features

- **Dashboard** — live counts of students, courses, subjects, and monthly fee collection
- **Students** — view, search, edit, and delete students; click a row to view full profile
- **Add Student** — insert a new student with course loaded dynamically from the `courses` table
- **Student Profile** — personal info, admission details, fees summary, marks, recent payment, edit and delete
- **Courses** — add, edit, delete courses with duration, fee, and status
- **Fees** — track fee payments per student, view history, add new payments
- **Marks** — enter internal (30) and external (70) marks per subject per semester; auto grade calculation
- **Library** — manage books, issue to students, return, and delete

---

## Requirements

- **Java JDK 17 or newer** — [Download](https://www.oracle.com/java/technologies/downloads/)
- **MySQL Server 8.0 or newer** — [Download](https://dev.mysql.com/downloads/mysql/)
- **MySQL Connector/J** (JDBC driver JAR) — [Download](https://dev.mysql.com/downloads/connector/j/)
- **VS Code** with the **Extension Pack for Java**, or any Java IDE (IntelliJ, Eclipse, NetBeans)

---

## Project Structure

```
Student_DataBase_Management_System/
├── src/
│   ├── Dashboard.java              (main entry point)
│   ├── DatabaseConnection.java     (JDBC connection setup)
│   ├── AddStudentPage.java
│   ├── StudentsPage.java
│   ├── StudentProfile.java
│   ├── CoursesPage.java
│   ├── FeesPage.java
│   ├── MarksPage.java
│   ├── LibraryPage.java
│   └── TestDB.java                 (optional connection test)
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── .vscode/
│   └── settings.json
├── .gitignore
├── README.md
└── seed.sql
```

---

## Setup Instructions

### Step 1 — Install MySQL and start the server

Make sure the MySQL service is running. On Windows, open **Services** and confirm `MySQL80` (or your version) is "Running". On macOS/Linux:

```bash
sudo service mysql start
```

Note your MySQL `root` password — you'll need it in Step 3.

### Step 2 — Create the database and populate sample data

Open MySQL Workbench (or any MySQL client) and run the full SQL script below. This creates the `Student_Database`, all tables, and inserts 100 sample students plus courses, payments, marks, and library books.

**Option A — MySQL Workbench**
1. Open MySQL Workbench → connect as `root`
2. **File → Open SQL Script** → select `seed.sql` (see full content below)
3. Click the ⚡ lightning bolt to execute

**Option B — Command line**
```bash
mysql -u root -p < seed.sql
```

### Step 3 — Set your MySQL password in the code

Open `src/DatabaseConnection.java` and change this line to your actual MySQL password:

```java
private static final String PASSWORD = "your_password_here";
```

Example if your password is `Mk@123`:
```java
private static final String PASSWORD = "Mk@123";
```

### Step 4 — Add the MySQL Connector JAR

Download **MySQL Connector/J** from [here](https://dev.mysql.com/downloads/connector/j/) and place the JAR inside the `lib/` folder:

```
lib/mysql-connector-j-9.7.0.jar
```

If you use a different version, update `.vscode/settings.json`:

```json
{
  "java.project.sourcePaths": ["src"],
  "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

### Step 5 — Run the application

**In VS Code:**
1. Open the project folder
2. Open `src/Dashboard.java`
3. Click **Run** (▶) above the `main` method, or press `Ctrl+F5`

**From terminal:**
```bash
javac -cp "lib/*" -d bin src/*.java
java -cp "bin:lib/*" Dashboard
```

On Windows, use `;` instead of `:` in the classpath:
```bash
java -cp "bin;lib/*" Dashboard
```

If everything is configured correctly, the Dashboard window opens with all counts pulled from MySQL.

---

## SQL Script (`seed.sql`)

Run this once. It creates the database, tables, and populates sample data (100 students, 5 courses, 50 payments, 250 marks, 10 library books). Safe to re-run — uses `CREATE IF NOT EXISTS` and `INSERT IGNORE`.

```sql
-- ============================================================
-- Student Database Management System - Seed Data
-- ============================================================

CREATE DATABASE IF NOT EXISTS Student_Database;
USE Student_Database;

-- ---------- TABLES ----------
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    dob DATE,
    course VARCHAR(100),
    gender VARCHAR(20),
    address VARCHAR(255),
    admission_date DATE,
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS courses (
    course_id VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(100),
    duration VARCHAR(50),
    total_fee DECIMAL(10,2),
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS fees_payments (
    payment_id VARCHAR(50) PRIMARY KEY,
    student_id INT,
    amount DECIMAL(10,2),
    payment_mode VARCHAR(50),
    receipt_no VARCHAR(50),
    payment_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS library_books (
    book_id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(150),
    author VARCHAR(100),
    status VARCHAR(20),
    issued_to VARCHAR(150),
    issue_date DATE,
    due_date DATE
);

CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    semester VARCHAR(20),
    subject VARCHAR(100),
    internal_marks INT,
    external_marks INT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- ---------- COURSES ----------
INSERT IGNORE INTO courses (course_id, course_name, duration, total_fee, status) VALUES
('C001', 'BCA',    '3 Years', 60000, 'Active'),
('C002', 'B.Tech', '4 Years', 80000, 'Active'),
('C003', 'MBA',    '2 Years', 70000, 'Active'),
('C004', 'BBA',    '3 Years', 55000, 'Active'),
('C005', 'MCA',    '2 Years', 65000, 'Active');

-- ---------- STUDENTS (100 rows) ----------
INSERT IGNORE INTO students
(id, full_name, email, phone, dob, course, gender, address, admission_date, status) VALUES
(1,  'Aarav Sharma',      'aarav.sharma1@example.com',      '9876500001', '2002-04-12', 'BCA',    'Male',   '101 MG Road, Delhi',       '2022-07-01', 'Active'),
(2,  'Vivaan Verma',      'vivaan.verma2@example.com',      '9876500002', '2001-08-23', 'B.Tech', 'Male',   '202 Ring Road, Mumbai',    '2021-07-15', 'Active'),
(3,  'Aditya Gupta',      'aditya.gupta3@example.com',      '9876500003', '2003-01-05', 'MBA',    'Male',   '303 Park Street, Kolkata', '2023-07-01', 'Active'),
(4,  'Vihaan Patel',      'vihaan.patel4@example.com',      '9876500004', '2002-11-19', 'BBA',    'Male',   '404 Anna Salai, Chennai',  '2022-07-10', 'Active'),
(5,  'Arjun Singh',       'arjun.singh5@example.com',       '9876500005', '2000-06-30', 'MCA',    'Male',   '505 FC Road, Pune',        '2020-07-01', 'Active'),
(6,  'Sai Kumar',         'sai.kumar6@example.com',         '9876500006', '2002-02-14', 'BCA',    'Male',   '606 Banjara Hills, Hyd',   '2022-07-01', 'Active'),
(7,  'Reyansh Reddy',     'reyansh.reddy7@example.com',     '9876500007', '2001-09-09', 'B.Tech', 'Male',   '707 Residency Rd, Blr',    '2021-07-15', 'Active'),
(8,  'Ayaan Nair',        'ayaan.nair8@example.com',        '9876500008', '2003-03-25', 'MBA',    'Male',   '808 Marine Dr, Kochi',     '2023-07-01', 'Active'),
(9,  'Krishna Iyer',      'krishna.iyer9@example.com',      '9876500009', '2002-07-07', 'BBA',    'Male',   '909 Mount Rd, Chennai',    '2022-07-10', 'Active'),
(10, 'Ishaan Mehta',      'ishaan.mehta10@example.com',     '9876500010', '2000-12-01', 'MCA',    'Male',   '110 Link Rd, Mumbai',      '2020-07-01', 'Active'),
(11, 'Ananya Joshi',      'ananya.joshi11@example.com',     '9876500011', '2002-05-17', 'BCA',    'Female', '111 Nehru Place, Delhi',   '2022-07-01', 'Active'),
(12, 'Diya Rao',          'diya.rao12@example.com',         '9876500012', '2001-10-28', 'B.Tech', 'Female', '112 Jayanagar, Blr',       '2021-07-15', 'Active'),
(13, 'Aadhya Das',        'aadhya.das13@example.com',       '9876500013', '2003-02-09', 'MBA',    'Female', '113 Salt Lake, Kolkata',   '2023-07-01', 'Active'),
(14, 'Saanvi Bose',       'saanvi.bose14@example.com',      '9876500014', '2002-08-21', 'BBA',    'Female', '114 Park Circus, Kolkata', '2022-07-10', 'Active'),
(15, 'Pari Chopra',       'pari.chopra15@example.com',      '9876500015', '2000-11-11', 'MCA',    'Female', '115 Rajouri Grdn, Delhi',  '2020-07-01', 'Active'),
(16, 'Anika Kapoor',      'anika.kapoor16@example.com',     '9876500016', '2002-01-30', 'BCA',    'Female', '116 Bandra West, Mumbai',  '2022-07-01', 'Active'),
(17, 'Navya Malhotra',    'navya.malhotra17@example.com',   '9876500017', '2001-07-04', 'B.Tech', 'Female', '117 Koramangala, Blr',     '2021-07-15', 'Active'),
(18, 'Myra Bansal',       'myra.bansal18@example.com',      '9876500018', '2003-04-16', 'MBA',    'Female', '118 Sector 17, Chandigarh','2023-07-01', 'Active'),
(19, 'Sara Agarwal',      'sara.agarwal19@example.com',     '9876500019', '2002-09-27', 'BBA',    'Female', '119 Civil Lines, Jaipur',  '2022-07-10', 'Active'),
(20, 'Riya Mishra',       'riya.mishra20@example.com',      '9876500020', '2000-05-06', 'MCA',    'Female', '120 Gomti Nagar, Lucknow', '2020-07-01', 'Active'),
(21, 'Rahul Sharma',      'rahul.sharma21@example.com',     '9876500021', '2002-03-15', 'BCA',    'Male',   '121 Karol Bagh, Delhi',    '2022-07-01', 'Active'),
(22, 'Priya Verma',       'priya.verma22@example.com',      '9876500022', '2001-06-22', 'B.Tech', 'Female', '122 Thane West, Mumbai',   '2021-07-15', 'Active'),
(23, 'Amit Gupta',        'amit.gupta23@example.com',       '9876500023', '2003-05-10', 'MBA',    'Male',   '123 Howrah, Kolkata',      '2023-07-01', 'Active'),
(24, 'Neha Patel',        'neha.patel24@example.com',       '9876500024', '2002-12-03', 'BBA',    'Female', '124 Adyar, Chennai',       '2022-07-10', 'Active'),
(25, 'Rohit Singh',       'rohit.singh25@example.com',      '9876500025', '2000-08-18', 'MCA',    'Male',   '125 Kothrud, Pune',        '2020-07-01', 'Active'),
(26, 'Sneha Kumar',       'sneha.kumar26@example.com',      '9876500026', '2002-06-29', 'BCA',    'Female', '126 Gachibowli, Hyd',      '2022-07-01', 'Active'),
(27, 'Karan Reddy',       'karan.reddy27@example.com',      '9876500027', '2001-04-01', 'B.Tech', 'Male',   '127 Whitefield, Blr',      '2021-07-15', 'Active'),
(28, 'Pooja Nair',        'pooja.nair28@example.com',       '9876500028', '2003-06-12', 'MBA',    'Female', '128 Kakkanad, Kochi',      '2023-07-01', 'Active'),
(29, 'Vikram Iyer',       'vikram.iyer29@example.com',      '9876500029', '2002-10-24', 'BBA',    'Male',   '129 T Nagar, Chennai',     '2022-07-10', 'Active'),
(30, 'Nisha Mehta',       'nisha.mehta30@example.com',      '9876500030', '2000-01-15', 'MCA',    'Female', '130 Andheri East, Mumbai', '2020-07-01', 'Active'),
(31, 'Manish Joshi',      'manish.joshi31@example.com',     '9876500031', '2002-02-27', 'BCA',    'Male',   '131 Dwarka, Delhi',        '2022-07-01', 'Active'),
(32, 'Kavita Rao',        'kavita.rao32@example.com',       '9876500032', '2001-03-19', 'B.Tech', 'Female', '132 HSR Layout, Blr',      '2021-07-15', 'Active'),
(33, 'Suresh Das',        'suresh.das33@example.com',       '9876500033', '2003-07-08', 'MBA',    'Male',   '133 New Town, Kolkata',    '2023-07-01', 'Active'),
(34, 'Deepa Bose',        'deepa.bose34@example.com',       '9876500034', '2002-11-30', 'BBA',    'Female', '134 Ballygunge, Kolkata',  '2022-07-10', 'Active'),
(35, 'Ravi Chopra',       'ravi.chopra35@example.com',      '9876500035', '2000-09-21', 'MCA',    'Male',   '135 Saket, Delhi',         '2020-07-01', 'Active'),
(36, 'Anita Kapoor',      'anita.kapoor36@example.com',     '9876500036', '2002-12-14', 'BCA',    'Female', '136 Juhu, Mumbai',         '2022-07-01', 'Active'),
(37, 'Rajesh Malhotra',   'rajesh.malhotra37@example.com',  '9876500037', '2001-01-26', 'B.Tech', 'Male',   '137 Indiranagar, Blr',     '2021-07-15', 'Active'),
(38, 'Sunita Bansal',     'sunita.bansal38@example.com',    '9876500038', '2003-08-05', 'MBA',    'Female', '138 Sector 22, Chandigarh','2023-07-01', 'Active'),
(39, 'Ajay Agarwal',      'ajay.agarwal39@example.com',     '9876500039', '2002-04-17', 'BBA',    'Male',   '139 Vaishali Nagar, Jaipur','2022-07-10','Active'),
(40, 'Rekha Mishra',      'rekha.mishra40@example.com',     '9876500040', '2000-07-19', 'MCA',    'Female', '140 Hazratganj, Lucknow',  '2020-07-01', 'Active'),
(41, 'Nikhil Sharma',     'nikhil.sharma41@example.com',    '9876500041', '2002-10-11', 'BCA',    'Male',   '141 Lajpat Nagar, Delhi',  '2022-07-01', 'Active'),
(42, 'Meera Verma',       'meera.verma42@example.com',      '9876500042', '2001-02-24', 'B.Tech', 'Female', '142 Dadar, Mumbai',        '2021-07-15', 'Active'),
(43, 'Siddharth Gupta',   'siddharth.gupta43@example.com',  '9876500043', '2003-09-15', 'MBA',    'Male',   '143 Ballygunge, Kolkata',  '2023-07-01', 'Active'),
(44, 'Tanvi Patel',       'tanvi.patel44@example.com',      '9876500044', '2002-03-08', 'BBA',    'Female', '144 Velachery, Chennai',   '2022-07-10', 'Active'),
(45, 'Yash Singh',        'yash.singh45@example.com',       '9876500045', '2000-05-22', 'MCA',    'Male',   '145 Hinjewadi, Pune',      '2020-07-01', 'Active'),
(46, 'Isha Kumar',        'isha.kumar46@example.com',       '9876500046', '2002-12-27', 'BCA',    'Female', '146 Kukatpally, Hyd',      '2022-07-01', 'Active'),
(47, 'Dhruv Reddy',       'dhruv.reddy47@example.com',      '9876500047', '2001-11-01', 'B.Tech', 'Male',   '147 Electronic City, Blr', '2021-07-15', 'Active'),
(48, 'Aisha Nair',        'aisha.nair48@example.com',       '9876500048', '2003-12-19', 'MBA',    'Female', '148 Edappally, Kochi',     '2023-07-01', 'Active'),
(49, 'Aarush Iyer',       'aarush.iyer49@example.com',      '9876500049', '2002-08-14', 'BBA',    'Male',   '149 Egmore, Chennai',      '2022-07-10', 'Active'),
(50, 'Kiara Mehta',       'kiara.mehta50@example.com',      '9876500050', '2000-04-09', 'MCA',    'Female', '150 Powai, Mumbai',        '2020-07-01', 'Active'),
(51, 'Advait Joshi',      'advait.joshi51@example.com',     '9876500051', '2002-06-11', 'BCA',    'Male',   '151 Rohini, Delhi',        '2022-07-01', 'Active'),
(52, 'Zara Rao',          'zara.rao52@example.com',         '9876500052', '2001-05-03', 'B.Tech', 'Female', '152 Rajajinagar, Blr',     '2021-07-15', 'Active'),
(53, 'Kabir Das',         'kabir.das53@example.com',        '9876500053', '2003-10-22', 'MBA',    'Male',   '153 Behala, Kolkata',      '2023-07-01', 'Active'),
(54, 'Riya Bose',         'riya.bose54@example.com',        '9876500054', '2002-01-08', 'BBA',    'Female', '154 Alipore, Kolkata',     '2022-07-10', 'Active'),
(55, 'Rudra Chopra',      'rudra.chopra55@example.com',     '9876500055', '2000-10-17', 'MCA',    'Male',   '155 Vasant Kunj, Delhi',   '2020-07-01', 'Active'),
(56, 'Aarohi Kapoor',     'aarohi.kapoor56@example.com',    '9876500056', '2002-03-28', 'BCA',    'Female', '156 Colaba, Mumbai',       '2022-07-01', 'Active'),
(57, 'Ved Malhotra',      'ved.malhotra57@example.com',     '9876500057', '2001-12-06', 'B.Tech', 'Male',   '157 Malleshwaram, Blr',    '2021-07-15', 'Active'),
(58, 'Aanya Bansal',      'aanya.bansal58@example.com',     '9876500058', '2003-11-13', 'MBA',    'Female', '158 Sector 35, Chandigarh','2023-07-01', 'Active'),
(59, 'Arnav Agarwal',     'arnav.agarwal59@example.com',    '9876500059', '2002-07-20', 'BBA',    'Male',   '159 Malviya Nagar, Jaipur','2022-07-10', 'Active'),
(60, 'Ira Mishra',        'ira.mishra60@example.com',       '9876500060', '2000-02-02', 'MCA',    'Female', '160 Aliganj, Lucknow',     '2020-07-01', 'Active'),
(61, 'Atharv Sharma',     'atharv.sharma61@example.com',    '9876500061', '2002-09-01', 'BCA',    'Male',   '161 Pitampura, Delhi',     '2022-07-01', 'Active'),
(62, 'Amaira Verma',      'amaira.verma62@example.com',     '9876500062', '2001-08-15', 'B.Tech', 'Female', '162 Lower Parel, Mumbai',  '2021-07-15', 'Active'),
(63, 'Reyansh Gupta',     'reyansh.gupta63@example.com',    '9876500063', '2003-01-20', 'MBA',    'Male',   '163 Park Street, Kolkata', '2023-07-01', 'Active'),
(64, 'Saanvi Patel',      'saanvi.patel64@example.com',     '9876500064', '2002-11-28', 'BBA',    'Female', '164 Porur, Chennai',       '2022-07-10', 'Active'),
(65, 'Shaurya Singh',     'shaurya.singh65@example.com',    '9876500065', '2000-12-24', 'MCA',    'Male',   '165 Aundh, Pune',          '2020-07-01', 'Active'),
(66, 'Navya Kumar',       'navya.kumar66@example.com',      '9876500066', '2002-04-05', 'BCA',    'Female', '166 Banjara Hills, Hyd',   '2022-07-01', 'Active'),
(67, 'Darsh Reddy',       'darsh.reddy67@example.com',      '9876500067', '2001-06-10', 'B.Tech', 'Male',   '167 Yelahanka, Blr',       '2021-07-15', 'Active'),
(68, 'Diya Nair',         'diya.nair68@example.com',        '9876500068', '2003-02-16', 'MBA',    'Female', '168 Palarivattom, Kochi',  '2023-07-01', 'Active'),
(69, 'Veer Iyer',         'veer.iyer69@example.com',        '9876500069', '2002-05-25', 'BBA',    'Male',   '169 Mylapore, Chennai',    '2022-07-10', 'Active'),
(70, 'Anvi Mehta',        'anvi.mehta70@example.com',       '9876500070', '2000-03-03', 'MCA',    'Female', '170 Worli, Mumbai',        '2020-07-01', 'Active'),
(71, 'Om Joshi',          'om.joshi71@example.com',         '9876500071', '2002-08-08', 'BCA',    'Male',   '171 Janakpuri, Delhi',     '2022-07-01', 'Active'),
(72, 'Kiara Rao',         'kiara.rao72@example.com',        '9876500072', '2001-01-12', 'B.Tech', 'Female', '172 Hebbal, Blr',          '2021-07-15', 'Active'),
(73, 'Rishabh Das',       'rishabh.das73@example.com',      '9876500073', '2003-03-30', 'MBA',    'Male',   '173 Salt Lake, Kolkata',   '2023-07-01', 'Active'),
(74, 'Mishka Bose',       'mishka.bose74@example.com',      '9876500074', '2002-09-24', 'BBA',    'Female', '174 Gariahat, Kolkata',    '2022-07-10', 'Active'),
(75, 'Aarav Chopra',      'aarav.chopra75@example.com',     '9876500075', '2000-06-15', 'MCA',    'Male',   '175 Greater Kailash, Delhi','2020-07-01','Active'),
(76, 'Nitya Kapoor',      'nitya.kapoor76@example.com',     '9876500076', '2002-02-19', 'BCA',    'Female', '176 Malad, Mumbai',        '2022-07-01', 'Active'),
(77, 'Kiaan Malhotra',    'kiaan.malhotra77@example.com',   '9876500077', '2001-04-11', 'B.Tech', 'Male',   '177 Marathahalli, Blr',    '2021-07-15', 'Active'),
(78, 'Aadya Bansal',      'aadya.bansal78@example.com',     '9876500078', '2003-05-05', 'MBA',    'Female', '178 Sector 8, Chandigarh', '2023-07-01', 'Active'),
(79, 'Vivaan Agarwal',    'vivaan.agarwal79@example.com',   '9876500079', '2002-12-08', 'BBA',    'Male',   '179 Mansarovar, Jaipur',   '2022-07-10', 'Active'),
(80, 'Kyra Mishra',       'kyra.mishra80@example.com',      '9876500080', '2000-01-27', 'MCA',    'Female', '180 Indira Nagar, Lucknow','2020-07-01', 'Active'),
(81, 'Ishaan Sharma',     'ishaan.sharma81@example.com',    '9876500081', '2002-07-15', 'BCA',    'Male',   '181 Punjabi Bagh, Delhi',  '2022-07-01', 'Active'),
(82, 'Riya Verma',        'riya.verma82@example.com',       '9876500082', '2001-09-18', 'B.Tech', 'Female', '182 Vashi, Mumbai',        '2021-07-15', 'Active'),
(83, 'Ayaan Gupta',       'ayaan.gupta83@example.com',      '9876500083', '2003-06-20', 'MBA',    'Male',   '183 Dum Dum, Kolkata',     '2023-07-01', 'Active'),
(84, 'Ananya Patel',      'ananya.patel84@example.com',     '9876500084', '2002-10-01', 'BBA',    'Female', '184 OMR, Chennai',         '2022-07-10', 'Active'),
(85, 'Aryan Singh',       'aryan.singh85@example.com',      '9876500085', '2000-11-26', 'MCA',    'Male',   '185 Wakad, Pune',          '2020-07-01', 'Active'),
(86, 'Tara Kumar',        'tara.kumar86@example.com',       '9876500086', '2002-03-13', 'BCA',    'Female', '186 Madhapur, Hyd',        '2022-07-01', 'Active'),
(87, 'Rudra Reddy',       'rudra.reddy87@example.com',      '9876500087', '2001-07-24', 'B.Tech', 'Male',   '187 JP Nagar, Blr',        '2021-07-15', 'Active'),
(88, 'Naina Nair',        'naina.nair88@example.com',       '9876500088', '2003-08-31', 'MBA',    'Female', '188 Vyttila, Kochi',       '2023-07-01', 'Active'),
(89, 'Aditya Iyer',       'aditya.iyer89@example.com',      '9876500089', '2002-06-02', 'BBA',    'Male',   '189 Nungambakkam, Chennai','2022-07-10', 'Active'),
(90, 'Isha Mehta',        'isha.mehta90@example.com',       '9876500090', '2000-04-30', 'MCA',    'Female', '190 BKC, Mumbai',          '2020-07-01', 'Active'),
(91, 'Kiaan Joshi',       'kiaan.joshi91@example.com',      '9876500091', '2002-05-04', 'BCA',    'Male',   '191 Mayur Vihar, Delhi',   '2022-07-01', 'Active'),
(92, 'Aarohi Rao',        'aarohi.rao92@example.com',       '9876500092', '2001-02-08', 'B.Tech', 'Female', '192 Banashankari, Blr',    '2021-07-15', 'Active'),
(93, 'Vivaan Das',        'vivaan.das93@example.com',       '9876500093', '2003-04-28', 'MBA',    'Male',   '193 Barrackpore, Kolkata', '2023-07-01', 'Active'),
(94, 'Myra Bose',         'myra.bose94@example.com',        '9876500094', '2002-08-22', 'BBA',    'Female', '194 Bhowanipore, Kolkata', '2022-07-10', 'Active'),
(95, 'Ayaan Chopra',      'ayaan.chopra95@example.com',     '9876500095', '2000-07-07', 'MCA',    'Male',   '195 Saket, Delhi',         '2020-07-01', 'Active'),
(96, 'Saanvi Kapoor',     'saanvi.kapoor96@example.com',    '9876500096', '2002-01-17', 'BCA',    'Female', '196 Khar, Mumbai',         '2022-07-01', 'Active'),
(97, 'Reyansh Malhotra',  'reyansh.malhotra97@example.com', '9876500097', '2001-12-21', 'B.Tech', 'Male',   '197 Vijayanagar, Blr',     '2021-07-15', 'Active'),
(98, 'Aadya Bansal',      'aadya.bansal98@example.com',     '9876500098', '2003-07-16', 'MBA',    'Female', '198 Sector 44, Chandigarh','2023-07-01', 'Active'),
(99, 'Arjun Agarwal',     'arjun.agarwal99@example.com',    '9876500099', '2002-11-09', 'BBA',    'Male',   '199 Tonk Road, Jaipur',    '2022-07-10', 'Active'),
(100,'Diya Mishra',       'diya.mishra100@example.com',     '9876500100', '2000-10-13', 'MCA',    'Female', '200 Rajajipuram, Lucknow', '2020-07-01', 'Active');

-- ---------- LIBRARY BOOKS ----------
INSERT IGNORE INTO library_books (book_id, title, author, status) VALUES
('B001', 'Introduction to Algorithms',    'Cormen',              'Available'),
('B002', 'Clean Code',                     'Robert C. Martin',    'Available'),
('B003', 'Design Patterns',                'Erich Gamma',         'Available'),
('B004', 'The Pragmatic Programmer',       'Hunt & Thomas',       'Available'),
('B005', 'Java: The Complete Reference',   'Herbert Schildt',     'Available'),
('B006', 'Database System Concepts',       'Silberschatz',        'Available'),
('B007', 'Operating System Concepts',      'Silberschatz',        'Available'),
('B008', 'Computer Networks',              'Tanenbaum',           'Available'),
('B009', 'Artificial Intelligence',        'Russell & Norvig',    'Available'),
('B010', 'Head First Java',                'Kathy Sierra',        'Available');

-- ---------- FEES PAYMENTS ----------
INSERT IGNORE INTO fees_payments (payment_id, student_id, amount, payment_mode, receipt_no, payment_date) VALUES
('PAY001', 1,  15000, 'Online', 'RCPT001', '2024-07-05'),
('PAY002', 2,  20000, 'Cash',   'RCPT002', '2024-07-06'),
('PAY003', 3,  18000, 'Card',   'RCPT003', '2024-07-07'),
('PAY004', 4,  12000, 'Online', 'RCPT004', '2024-07-08'),
('PAY005', 5,  16000, 'Cheque', 'RCPT005', '2024-07-09'),
('PAY006', 6,  15000, 'Online', 'RCPT006', '2024-07-10'),
('PAY007', 7,  20000, 'Cash',   'RCPT007', '2024-07-11'),
('PAY008', 8,  18000, 'Card',   'RCPT008', '2024-07-12'),
('PAY009', 9,  12000, 'Online', 'RCPT009', '2024-07-13'),
('PAY010', 10, 16000, 'Cheque', 'RCPT010', '2024-07-14'),
('PAY011', 11, 15000, 'Online', 'RCPT011', '2024-07-15'),
('PAY012', 12, 20000, 'Cash',   'RCPT012', '2024-07-16'),
('PAY013', 13, 18000, 'Card',   'RCPT013', '2024-07-17'),
('PAY014', 14, 12000, 'Online', 'RCPT014', '2024-07-18'),
('PAY015', 15, 16000, 'Cheque', 'RCPT015', '2024-07-19'),
('PAY016', 16, 15000, 'Online', 'RCPT016', '2024-07-20'),
('PAY017', 17, 20000, 'Cash',   'RCPT017', '2024-07-21'),
('PAY018', 18, 18000, 'Card',   'RCPT018', '2024-07-22'),
('PAY019', 19, 12000, 'Online', 'RCPT019', '2024-07-23'),
('PAY020', 20, 16000, 'Cheque', 'RCPT020', '2024-07-24'),
('PAY021', 21, 15000, 'Online', 'RCPT021', '2024-07-25'),
('PAY022', 22, 20000, 'Cash',   'RCPT022', '2024-07-26'),
('PAY023', 23, 18000, 'Card',   'RCPT023', '2024-07-27'),
('PAY024', 24, 12000, 'Online', 'RCPT024', '2024-07-28'),
('PAY025', 25, 16000, 'Cheque', 'RCPT025', '2024-07-29'),
('PAY026', 26, 15000, 'Online', 'RCPT026', '2024-08-01'),
('PAY027', 27, 20000, 'Cash',   'RCPT027', '2024-08-02'),
('PAY028', 28, 18000, 'Card',   'RCPT028', '2024-08-03'),
('PAY029', 29, 12000, 'Online', 'RCPT029', '2024-08-04'),
('PAY030', 30, 16000, 'Cheque', 'RCPT030', '2024-08-05'),
('PAY031', 31, 15000, 'Online', 'RCPT031', '2024-08-06'),
('PAY032', 32, 20000, 'Cash',   'RCPT032', '2024-08-07'),
('PAY033', 33, 18000, 'Card',   'RCPT033', '2024-08-08'),
('PAY034', 34, 12000, 'Online', 'RCPT034', '2024-08-09'),
('PAY035', 35, 16000, 'Cheque', 'RCPT035', '2024-08-10'),
('PAY036', 36, 15000, 'Online', 'RCPT036', '2024-08-11'),
('PAY037', 37, 20000, 'Cash',   'RCPT037', '2024-08-12'),
('PAY038', 38, 18000, 'Card',   'RCPT038', '2024-08-13'),
('PAY039', 39, 12000, 'Online', 'RCPT039', '2024-08-14'),
('PAY040', 40, 16000, 'Cheque', 'RCPT040', '2024-08-15'),
('PAY041', 41, 15000, 'Online', 'RCPT041', '2024-08-16'),
('PAY042', 42, 20000, 'Cash',   'RCPT042', '2024-08-17'),
('PAY043', 43, 18000, 'Card',   'RCPT043', '2024-08-18'),
('PAY044', 44, 12000, 'Online', 'RCPT044', '2024-08-19'),
('PAY045', 45, 16000, 'Cheque', 'RCPT045', '2024-08-20'),
('PAY046', 46, 15000, 'Online', 'RCPT046', '2024-08-21'),
('PAY047', 47, 20000, 'Cash',   'RCPT047', '2024-08-22'),
('PAY048', 48, 18000, 'Card',   'RCPT048', '2024-08-23'),
('PAY049', 49, 12000, 'Online', 'RCPT049', '2024-08-24'),
('PAY050', 50, 16000, 'Cheque', 'RCPT050', '2024-08-25');

-- ---------- MARKS (50 students × 5 subjects = 250 rows) ----------
INSERT IGNORE INTO marks (student_id, semester, subject, internal_marks, external_marks) VALUES
(1,'Semester 1','Data Structures',25,60),
(1,'Semester 1','Mathematics',22,55),
(1,'Semester 1','Computer Fundamentals',28,65),
(1,'Semester 1','English Communication',24,58),
(1,'Semester 1','Digital Electronics',26,62),
(2,'Semester 1','Data Structures',20,50),
(2,'Semester 1','Mathematics',18,45),
(2,'Semester 1','Computer Fundamentals',24,55),
(2,'Semester 1','English Communication',22,52),
(2,'Semester 1','Digital Electronics',21,48),
(3,'Semester 1','Data Structures',28,68),
(3,'Semester 1','Mathematics',26,62),
(3,'Semester 1','Computer Fundamentals',29,70),
(3,'Semester 1','English Communication',27,65),
(3,'Semester 1','Digital Electronics',25,60),
(4,'Semester 1','Data Structures',22,52),
(4,'Semester 1','Mathematics',20,48),
(4,'Semester 1','Computer Fundamentals',24,56),
(4,'Semester 1','English Communication',23,54),
(4,'Semester 1','Digital Electronics',21,50),
(5,'Semester 1','Data Structures',30,70),
(5,'Semester 1','Mathematics',29,68),
(5,'Semester 1','Computer Fundamentals',28,66),
(5,'Semester 1','English Communication',27,64),
(5,'Semester 1','Digital Electronics',26,62),
(6,'Semester 1','Data Structures',24,58),
(6,'Semester 1','Mathematics',23,56),
(6,'Semester 1','Computer Fundamentals',25,60),
(6,'Semester 1','English Communication',22,54),
(6,'Semester 1','Digital Electronics',24,58),
(7,'Semester 1','Data Structures',26,64),
(7,'Semester 1','Mathematics',25,62),
(7,'Semester 1','Computer Fundamentals',27,66),
(7,'Semester 1','English Communication',24,58),
(7,'Semester 1','Digital Electronics',25,60),
(8,'Semester 1','Data Structures',21,50),
(8,'Semester 1','Mathematics',20,48),
(8,'Semester 1','Computer Fundamentals',22,52),
(8,'Semester 1','English Communication',21,50),
(8,'Semester 1','Digital Electronics',20,46),
(9,'Semester 1','Data Structures',28,66),
(9,'Semester 1','Mathematics',27,64),
(9,'Semester 1','Computer Fundamentals',29,68),
(9,'Semester 1','English Communication',26,62),
(9,'Semester 1','Digital Electronics',27,64),
(10,'Semester 1','Data Structures',23,54),
(10,'Semester 1','Mathematics',22,52),
(10,'Semester 1','Computer Fundamentals',24,56),
(10,'Semester 1','English Communication',23,54),
(10,'Semester 1','Digital Electronics',22,52),
(11,'Semester 2','Data Structures',25,60),
(11,'Semester 2','Mathematics',24,58),
(11,'Semester 2','Computer Fundamentals',26,62),
(11,'Semester 2','English Communication',25,60),
(11,'Semester 2','Digital Electronics',24,58),
(12,'Semester 2','Data Structures',27,64),
(12,'Semester 2','Mathematics',26,62),
(12,'Semester 2','Computer Fundamentals',28,66),
(12,'Semester 2','English Communication',27,64),
(12,'Semester 2','Digital Electronics',26,62),
(13,'Semester 2','Data Structures',22,52),
(13,'Semester 2','Mathematics',21,50),
(13,'Semester 2','Computer Fundamentals',23,54),
(13,'Semester 2','English Communication',22,52),
(13,'Semester 2','Digital Electronics',21,50),
(14,'Semester 2','Data Structures',29,68),
(14,'Semester 2','Mathematics',28,66),
(14,'Semester 2','Computer Fundamentals',30,70),
(14,'Semester 2','English Communication',29,68),
(14,'Semester 2','Digital Electronics',28,66),
(15,'Semester 2','Data Structures',24,56),
(15,'Semester 2','Mathematics',23,54),
(15,'Semester 2','Computer Fundamentals',25,58),
(15,'Semester 2','English Communication',24,56),
(15,'Semester 2','Digital Electronics',23,54),
(16,'Semester 2','Data Structures',26,60),
(16,'Semester 2','Mathematics',25,58),
(16,'Semester 2','Computer Fundamentals',27,62),
(16,'Semester 2','English Communication',26,60),
(16,'Semester 2','Digital Electronics',25,58),
(17,'Semester 2','Data Structures',20,48),
(17,'Semester 2','Mathematics',19,46),
(17,'Semester 2','Computer Fundamentals',21,50),
(17,'Semester 2','English Communication',20,48),
(17,'Semester 2','Digital Electronics',19,46),
(18,'Semester 2','Data Structures',28,66),
(18,'Semester 2','Mathematics',27,64),
(18,'Semester 2','Computer Fundamentals',29,68),
(18,'Semester 2','English Communication',28,66),
(18,'Semester 2','Digital Electronics',27,64),
(19,'Semester 2','Data Structures',22,52),
(19,'Semester 2','Mathematics',21,50),
(19,'Semester 2','Computer Fundamentals',23,54),
(19,'Semester 2','English Communication',22,52),
(19,'Semester 2','Digital Electronics',21,50),
(20,'Semester 2','Data Structures',25,60),
(20,'Semester 2','Mathematics',24,58),
(20,'Semester 2','Computer Fundamentals',26,62),
(20,'Semester 2','English Communication',25,60),
(20,'Semester 2','Digital Electronics',24,58),
(21,'Semester 3','Data Structures',27,64),
(21,'Semester 3','Mathematics',26,62),
(21,'Semester 3','Computer Fundamentals',28,66),
(21,'Semester 3','English Communication',27,64),
(21,'Semester 3','Digital Electronics',26,62),
(22,'Semester 3','Data Structures',24,56),
(22,'Semester 3','Mathematics',23,54),
(22,'Semester 3','Computer Fundamentals',25,58),
(22,'Semester 3','English Communication',24,56),
(22,'Semester 3','Digital Electronics',23,54),
(23,'Semester 3','Data Structures',29,68),
(23,'Semester 3','Mathematics',28,66),
(23,'Semester 3','Computer Fundamentals',30,70),
(23,'Semester 3','English Communication',29,68),
(23,'Semester 3','Digital Electronics',28,66),
(24,'Semester 3','Data Structures',21,50),
(24,'Semester 3','Mathematics',20,48),
(24,'Semester 3','Computer Fundamentals',22,52),
(24,'Semester 3','English Communication',21,50),
(24,'Semester 3','Digital Electronics',20,46),
(25,'Semester 3','Data Structures',26,60),
(25,'Semester 3','Mathematics',25,58),
(25,'Semester 3','Computer Fundamentals',27,62),
(25,'Semester 3','English Communication',26,60),
(25,'Semester 3','Digital Electronics',25,58),
(26,'Semester 3','Data Structures',23,54),
(26,'Semester 3','Mathematics',22,52),
(26,'Semester 3','Computer Fundamentals',24,56),
(26,'Semester 3','English Communication',23,54),
(26,'Semester 3','Digital Electronics',22,52),
(27,'Semester 3','Data Structures',28,66),
(27,'Semester 3','Mathematics',27,64),
(27,'Semester 3','Computer Fundamentals',29,68),
(27,'Semester 3','English Communication',28,66),
(27,'Semester 3','Digital Electronics',27,64),
(28,'Semester 3','Data Structures',22,52),
(28,'Semester 3','Mathematics',21,50),
(28,'Semester 3','Computer Fundamentals',23,54),
(28,'Semester 3','English Communication',22,52),
(28,'Semester 3','Digital Electronics',21,50),
(29,'Semester 3','Data Structures',25,60),
(29,'Semester 3','Mathematics',24,58),
(29,'Semester 3','Computer Fundamentals',26,62),
(29,'Semester 3','English Communication',25,60),
(29,'Semester 3','Digital Electronics',24,58),
(30,'Semester 3','Data Structures',27,64),
(30,'Semester 3','Mathematics',26,62),
(30,'Semester 3','Computer Fundamentals',28,66),
(30,'Semester 3','English Communication',27,64),
(30,'Semester 3','Digital Electronics',26,62),
(31,'Semester 4','Data Structures',25,60),
(31,'Semester 4','Mathematics',24,58),
(31,'Semester 4','Computer Fundamentals',26,62),
(31,'Semester 4','English Communication',25,60),
(31,'Semester 4','Digital Electronics',24,58),
(32,'Semester 4','Data Structures',28,66),
(32,'Semester 4','Mathematics',27,64),
(32,'Semester 4','Computer Fundamentals',29,68),
(32,'Semester 4','English Communication',28,66),
(32,'Semester 4','Digital Electronics',27,64),
(33,'Semester 4','Data Structures',22,52),
(33,'Semester 4','Mathematics',21,50),
(33,'Semester 4','Computer Fundamentals',23,54),
(33,'Semester 4','English Communication',22,52),
(33,'Semester 4','Digital Electronics',21,50),
(34,'Semester 4','Data Structures',29,68),
(34,'Semester 4','Mathematics',28,66),
(34,'Semester 4','Computer Fundamentals',30,70),
(34,'Semester 4','English Communication',29,68),
(34,'Semester 4','Digital Electronics',28,66),
(35,'Semester 4','Data Structures',24,56),
(35,'Semester 4','Mathematics',23,54),
(35,'Semester 4','Computer Fundamentals',25,58),
(35,'Semester 4','English Communication',24,56),
(35,'Semester 4','Digital Electronics',23,54),
(36,'Semester 4','Data Structures',26,60),
(36,'Semester 4','Mathematics',25,58),
(36,'Semester 4','Computer Fundamentals',27,62),
(36,'Semester 4','English Communication',26,60),
(36,'Semester 4','Digital Electronics',25,58),
(37,'Semester 4','Data Structures',20,48),
(37,'Semester 4','Mathematics',19,46),
(37,'Semester 4','Computer Fundamentals',21,50),
(37,'Semester 4','English Communication',20,48),
(37,'Semester 4','Digital Electronics',19,46),
(38,'Semester 4','Data Structures',28,66),
(38,'Semester 4','Mathematics',27,64),
(38,'Semester 4','Computer Fundamentals',29,68),
(38,'Semester 4','English Communication',28,66),
(38,'Semester 4','Digital Electronics',27,64),
(39,'Semester 4','Data Structures',22,52),
(39,'Semester 4','Mathematics',21,50),
(39,'Semester 4','Computer Fundamentals',23,54),
(39,'Semester 4','English Communication',22,52),
(39,'Semester 4','Digital Electronics',21,50),
(40,'Semester 4','Data Structures',25,60),
(40,'Semester 4','Mathematics',24,58),
(40,'Semester 4','Computer Fundamentals',26,62),
(40,'Semester 4','English Communication',25,60),
(40,'Semester 4','Digital Electronics',24,58),
(41,'Semester 4','Data Structures',27,64),
(41,'Semester 4','Mathematics',26,62),
(41,'Semester 4','Computer Fundamentals',28,66),
(41,'Semester 4','English Communication',27,64),
(41,'Semester 4','Digital Electronics',26,62),
(42,'Semester 4','Data Structures',24,56),
(42,'Semester 4','Mathematics',23,54),
(42,'Semester 4','Computer Fundamentals',25,58),
(42,'Semester 4','English Communication',24,56),
(42,'Semester 4','Digital Electronics',23,54),
(43,'Semester 4','Data Structures',29,68),
(43,'Semester 4','Mathematics',28,66),
(43,'Semester 4','Computer Fundamentals',30,70),
(43,'Semester 4','English Communication',29,68),
(43,'Semester 4','Digital Electronics',28,66),
(44,'Semester 4','Data Structures',21,50),
(44,'Semester 4','Mathematics',20,48),
(44,'Semester 4','Computer Fundamentals',22,52),
(44,'Semester 4','English Communication',21,50),
(44,'Semester 4','Digital Electronics',20,46),
(45,'Semester 4','Data Structures',26,60),
(45,'Semester 4','Mathematics',25,58),
(45,'Semester 4','Computer Fundamentals',27,62),
(45,'Semester 4','English Communication',26,60),
(45,'Semester 4','Digital Electronics',25,58),
(46,'Semester 4','Data Structures',23,54),
(46,'Semester 4','Mathematics',22,52),
(46,'Semester 4','Computer Fundamentals',24,56),
(46,'Semester 4','English Communication',23,54),
(46,'Semester 4','Digital Electronics',22,52),
(47,'Semester 4','Data Structures',28,66),
(47,'Semester 4','Mathematics',27,64),
(47,'Semester 4','Computer Fundamentals',29,68),
(47,'Semester 4','English Communication',28,66),
(47,'Semester 4','Digital Electronics',27,64),
(48,'Semester 4','Data Structures',22,52),
(48,'Semester 4','Mathematics',21,50),
(48,'Semester 4','Computer Fundamentals',23,54),
(48,'Semester 4','English Communication',22,52),
(48,'Semester 4','Digital Electronics',21,50),
(49,'Semester 4','Data Structures',25,60),
(49,'Semester 4','Mathematics',24,58),
(49,'Semester 4','Computer Fundamentals',26,62),
(49,'Semester 4','English Communication',25,60),
(49,'Semester 4','Digital Electronics',24,58),
(50,'Semester 4','Data Structures',27,64),
(50,'Semester 4','Mathematics',26,62),
(50,'Semester 4','Computer Fundamentals',28,66),
(50,'Semester 4','English Communication',27,64),
(50,'Semester 4','Digital Electronics',26,62);

-- ---------- VERIFY ----------
SELECT 'Students:'  AS table_name, COUNT(*) AS row_count FROM students
UNION ALL SELECT 'Courses:',       COUNT(*) FROM courses
UNION ALL SELECT 'Payments:',      COUNT(*) FROM fees_payments
UNION ALL SELECT 'Library Books:', COUNT(*) FROM library_books
UNION ALL SELECT 'Marks:',         COUNT(*) FROM marks;
```

---

## Troubleshooting

### "Access denied for user 'root'@'localhost'"
Wrong password in `DatabaseConnection.java`. Double-check Step 3.

### "Unknown database 'Student_Database'"
You haven't run `seed.sql` yet. Run it (Step 2).

### "No suitable driver found for jdbc:mysql://..."
The MySQL Connector JAR is missing from `lib/` or not referenced in `.vscode/settings.json`.

### "Table 'Student_Database.students' doesn't exist"
Partial seed. Drop the DB and re-run `seed.sql`:
```sql
DROP DATABASE Student_Database;
```
Then re-run the full script.

### Java compile error: "package com.mysql.cj.jdbc does not exist"
Same as above — JAR not on classpath. Fix `lib/` and `settings.json`.

---

## Reset the database

To wipe everything and start clean:

```sql
USE Student_Database;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE fees_payments;
TRUNCATE TABLE marks;
TRUNCATE TABLE students;
TRUNCATE TABLE library_books;
TRUNCATE TABLE courses;
SET FOREIGN_KEY_CHECKS = 1;
```

Then run `seed.sql` again to repopulate.

---

## Notes

- No login/authentication — anyone running the app sees all data.
- Password for MySQL is stored in `DatabaseConnection.java` as a plain string. For production use, switch to an environment variable.
- This project is intended for learning purposes.

---

## License

Free to use for educational purposes.
