# QuizForge — Personalized Quiz Platform

QuizForge is a full-stack web application that helps users practice technical subjects through configurable quizzes. Users can choose a subject, difficulty level, and number of questions, then complete a timed quiz and review their performance.

## Features

- **User authentication:** Registration, login, and logout.
- **Configurable quizzes:** Select a subject, difficulty level, and question count.
- **Timed quiz sessions:** Complete questions with a countdown timer.
- **Scoring:** Submit answers and view quiz results.
- **User profile:** View and manage profile information.
- **Quiz history:** Review previous quiz attempts.
- **Admin functionality:** Administrative access for managing the platform.
- **Database persistence:** Store application data in MySQL.

## Technology Stack

| Area | Technologies |
|---|---|
| Frontend | HTML5, CSS3, JavaScript, Bootstrap |
| Backend | Java, Jakarta Servlets, JDBC |
| Database | MySQL |
| Build tool | Apache Maven |
| Application server | Apache Tomcat |

## Project Structure


QuizForge/
├── backend/
│   └── quizforge-backend/
│       ├── pom.xml
│       └── src/
│           └── main/
│               └── webapp/
│                   ├── CSS/
│                   ├── JS/
│                   ├── login.html
│                   ├── register.html
│                   ├── dashboard.html
│                   └── quiz.html
└── README.md
```

*The structure above shows the main project areas; your repository may contain additional Java classes, servlets, pages, and resources.*

## Requirements

- Java JDK 25
- Apache Maven 3.9.x
- Apache Tomcat 11
- MySQL Server

## Run Locally

1. Clone the repository:

   ```bash
   git clone YOUR_GITHUB_REPOSITORY_URL
   ```

2. Open the backend project directory:

   ```bash
   cd backend/quizforge-backend
   ```

3. Configure the MySQL connection using your local database settings. Keep database passwords and other secrets out of source control.

4. Build the application:

   ```bash
   mvn clean package
   ```

5. Deploy the generated WAR file from the `target` directory to your Tomcat `webapps` directory.

6. Start Tomcat and open:

   ```text
   http://localhost:8080/quizforge-backend-1.0-SNAPSHOT/dashboard.html
   ```

   If the deployed WAR filename or context path differs, update the URL accordingly.

## Database

QuizForge uses MySQL to persist application data, including user and quiz-related records. Configure the database and required tables before running the application.

Do not publish database credentials, personal account details, or production secrets in this repository.

## Future Improvements

- Email verification and account recovery
- Password hashing and stronger authentication security
- REST API integration
- AI-assisted quiz generation
- Additional subjects and analytics

## Project Goal

QuizForge is a portfolio project demonstrating full-stack web development, Java backend programming, database integration, and interactive quiz functionality.

## Author

Developed as a full-stack learning and portfolio project.
