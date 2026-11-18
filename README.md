# QuizForge — Full-Stack Personalized Quiz Platform

QuizForge is a full-stack web application that allows users to practice technical subjects through configurable and timed quizzes. Users can select a subject, difficulty level, and number of questions, attempt quizzes, view results, and track their previous performance.

The project demonstrates Java backend development, frontend technologies, MySQL database integration, and Docker-based application deployment.

## Features

- **User Authentication:** User registration, login, and logout.
- **Configurable Quizzes:** Select a subject, difficulty level, and question count.
- **Timed Quiz Sessions:** Complete quizzes with a countdown timer.
- **Automatic Scoring:** Submit answers and view quiz results.
- **User Profile:** View and manage profile information.
- **Quiz History:** Review previous quiz attempts.
- **Admin Functionality:** Administrative access to platform features.
- **Database Persistence:** Store user and quiz-related information in MySQL.
- **Docker Support:** Run the application and database using Docker Compose.

## Technology Stack

| Area | Technologies |
|---|---|
| Frontend | HTML5, CSS3, JavaScript, Bootstrap |
| Backend | Java, Jakarta Servlets, JDBC |
| Database | MySQL |
| Build Tool | Apache Maven |
| Application Server | Apache Tomcat 11 |
| Containerization | Docker, Docker Compose |
| Version Control | Git, GitHub |

## Project Structure

```text
QuizForge/
│
├── backend/
│   └── quizforge-backend/
│       ├── pom.xml
│       └── src/
│           └── main/
│               ├── java/
│               └── webapp/
│                   ├── CSS/
│                   ├── JS/
│                   ├── login.html
│                   ├── register.html
│                   ├── dashboard.html
│                   └── quiz.html
│
├── docker/
│   └── mysql/
│       └── init/
│
├── Dockerfile
├── docker-compose.yml
├── .gitignore
└── README.md
```

*The structure above highlights the main project files. Additional Java classes, servlets, pages, and resources are included in the repository.*

## Requirements

### Option 1: Run with Docker (Recommended)

Install:

- Docker Desktop
- Git

### Step 1: Clone the repository

```bash
git clone https://github.com/amankushwah28/QuizForge.git
cd QuizForge
```

### Step 2: Configure environment variables

Create a `.env` file in the project root, alongside `docker-compose.yml`.

Add the following configuration:

```dotenv
MYSQL_ROOT_PASSWORD=your_strong_root_password
MYSQL_USER=quizforge_app
MYSQL_PASSWORD=your_strong_app_password
```

Replace the example passwords with your own strong passwords. Keep `.env` private and never commit it to GitHub.

### Step 3: Configure database initialization

The SQL initialization directory is:

```text
docker/mysql/init/
```

Place a compatible MySQL database initialization script in this directory before the first startup. The script should create the required tables and, if needed, insert sample quiz questions.

**Note:** The database backup is not included in this public repository. You must supply your own compatible initialization SQL file.

### Step 4: Build and start the application

```bash
docker compose up -d --build
```

Docker Compose builds the Java application image and starts the application and MySQL services.

### Step 5: Verify the containers

```bash
docker compose ps
```

Wait until the application is running and the database reports healthy.

### Step 6: Open QuizForge

Visit:

```text
http://localhost:8080/quizforge-backend-1.0-SNAPSHOT/dashboard.html
```

### Useful Docker Commands

| Command | Purpose |
|---|---|
| `docker compose up -d` | Start services |
| `docker compose up -d --build` | Build and start services |
| `docker compose ps` | Check service status |
| `docker compose logs -f` | View application and database logs |
| `docker compose stop` | Stop services without deleting data |
| `docker compose start` | Start stopped services |
| `docker compose restart` | Restart services |

**Important:** The MySQL data is stored in a Docker-managed volume. Avoid `docker compose down -v` unless you intentionally want to remove that volume and its database data.

## Option 2: Run Without Docker

For manual development, install:

- Java JDK 25
- Apache Maven 3.9.x
- Apache Tomcat 11
- MySQL Server 8.0 or a compatible version

### Steps

1. Clone the repository.
2. Configure your MySQL database and required tables.
3. Configure the database connection using your local environment settings.
4. Open the backend directory:

```bash
cd QuizForge/backend/quizforge-backend
```

5. Build the application:

```bash
mvn clean package
```

6. Deploy the generated WAR file from the `target` directory to Tomcat's `webapps` directory.
7. Start Tomcat and open the dashboard URL.

## Database

QuizForge uses MySQL to store user accounts, quiz questions, quiz attempts, and related application data.

Configure the database connection using environment variables:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

Never publish database credentials, personal account information, or private SQL backups in the repository.

## Future Improvements

- Email verification and account recovery
- Stronger authentication and password security
- REST API integration
- AI-assisted quiz generation
- Additional subjects and performance analytics

## Project Goal

QuizForge is a full-stack portfolio project built to demonstrate practical knowledge of Java, web technologies, JDBC, MySQL, Maven, Docker, and application deployment.

## Author

**Aman Kushwah**

GitHub: [amankushwah28](https://github.com/amankushwah28)

Project Repository: [QuizForge](https://github.com/amankushwah28/QuizForge)


Developed as a full-stack learning and portfolio project.
