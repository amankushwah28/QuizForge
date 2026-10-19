package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/submit-quiz")
public class SubmitQuizServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String contextPath =
                request.getContextPath();

        // ==========================================
        // SESSION CHECK
        // ==========================================

        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }

        // ==========================================
        // GET USER EMAIL
        // ==========================================

        String email =
                (String) session.getAttribute(
                        "userEmail"
                );

        // ==========================================
        // GET QUIZ QUESTIONS
        // ==========================================

        @SuppressWarnings("unchecked")
        List<Question> questions =
                (List<Question>) session.getAttribute(
                        "quizQuestions"
                );

        if (questions == null
                || questions.isEmpty()) {

            response.sendRedirect(
                    contextPath + "/quiz.html"
            );

            return;
        }

        // ==========================================
        // QUIZ INFORMATION
        // ==========================================

        String subject =
                (String) session.getAttribute(
                        "quizSubject"
                );

        String difficulty =
                (String) session.getAttribute(
                        "quizDifficulty"
                );

        if (subject == null || subject.isBlank()) {
            subject = "Quiz";
        }

        if (difficulty == null) {
            difficulty = "";
        }

        // ==========================================
        // CALCULATE SCORE
        // ==========================================

        int score = 0;

        int totalQuestions =
                questions.size();

        int unanswered = 0;

        for (Question question : questions) {

            String parameterName =
                    "q" + question.getId();

            String selectedAnswer =
                    request.getParameter(
                            parameterName
                    );

            String correctAnswer =
                    question.getCorrectAnswer();

            // --------------------------------------
            // SKIPPED QUESTION
            // --------------------------------------

            if (selectedAnswer == null
                    || selectedAnswer.isBlank()) {

                unanswered++;

                continue;
            }

            // --------------------------------------
            // CHECK ANSWER
            // --------------------------------------

            if (isCorrectAnswer(
                    question,
                    selectedAnswer,
                    correctAnswer)) {

                score++;
            }
        }

        // ==========================================
        // CALCULATE PERCENTAGE
        // ==========================================

        double percentage = 0.0;

        if (totalQuestions > 0) {

            percentage =
                    ((double) score
                            / totalQuestions)
                            * 100.0;
        }

        // ==========================================
        // GENERATE UNIQUE ATTEMPT ID
        // ==========================================

        String attemptId =
                UUID.randomUUID().toString();

        // ==========================================
        // SAVE RESULT + QUESTION DETAILS
        // ==========================================

        String resultSql = """
                INSERT INTO quiz_results
                (
                    user_email,
                    subject,
                    score,
                    total_questions,
                    percentage,
                    attempt_id
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        /*
         * IMPORTANT:
         *
         * Your actual database contains:
         *
         * user_answer
         * user_answer_letter
         * correct_answer_letter
         *
         * It does NOT contain:
         *
         * selected_answer
         *
         * So this SQL exactly matches your
         * quiz_attempt_details table.
         */

        String detailSql = """
                INSERT INTO quiz_attempt_details
                (
                    attempt_id,
                    user_email,
                    subject,
                    question_number,
                    question_id,
                    question_text,
                    option_a,
                    option_b,
                    option_c,
                    option_d,
                    user_answer,
                    correct_answer,
                    user_answer_letter,
                    correct_answer_letter,
                    is_correct
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement resultStatement =
                            connection.prepareStatement(
                                    resultSql
                            );

                    PreparedStatement detailStatement =
                            connection.prepareStatement(
                                    detailSql
                            )
            ) {

                // ==================================
                // SAVE QUIZ RESULT
                // ==================================

                resultStatement.setString(
                        1,
                        email
                );

                resultStatement.setString(
                        2,
                        subject
                );

                resultStatement.setInt(
                        3,
                        score
                );

                resultStatement.setInt(
                        4,
                        totalQuestions
                );

                resultStatement.setDouble(
                        5,
                        percentage
                );

                resultStatement.setString(
                        6,
                        attemptId
                );

                resultStatement.executeUpdate();

                // ==================================
                // SAVE EACH QUESTION DETAIL
                // ==================================

                int questionNumber = 1;

                for (Question question : questions) {

                    String selectedAnswer =
                            request.getParameter(
                                    "q" + question.getId()
                            );

                    String correctAnswer =
                            question.getCorrectAnswer();

                    // --------------------------------
                    // USER ANSWER LETTER
                    // --------------------------------

                    String userAnswerLetter =
                            getAnswerLetter(
                                    question,
                                    selectedAnswer
                            );

                    // --------------------------------
                    // CORRECT ANSWER LETTER
                    // --------------------------------

                    String correctAnswerLetter =
                            getAnswerLetter(
                                    question,
                                    correctAnswer
                            );

                    // --------------------------------
                    // USER ANSWER TEXT
                    // --------------------------------

                    String userAnswerText =
                            getOptionText(
                                    question,
                                    userAnswerLetter
                            );

                    /*
                     * If the submitted value is already
                     * full option text instead of A/B/C/D,
                     * preserve that text.
                     */

                    if (userAnswerText == null
                            && selectedAnswer != null
                            && !selectedAnswer.isBlank()) {

                        userAnswerText =
                                selectedAnswer.trim();
                    }

                    // --------------------------------
                    // CORRECT ANSWER TEXT
                    // --------------------------------

                    String correctAnswerText =
                            getOptionText(
                                    question,
                                    correctAnswerLetter
                            );

                    /*
                     * Fallback in case correct_answer
                     * contains the actual answer text.
                     */

                    if (correctAnswerText == null
                            && correctAnswer != null
                            && !correctAnswer.isBlank()) {

                        correctAnswerText =
                                correctAnswer.trim();
                    }

                    // --------------------------------
                    // CHECK QUESTION
                    // --------------------------------

                    boolean isCorrect =
                            isCorrectAnswer(
                                    question,
                                    selectedAnswer,
                                    correctAnswer
                            );

                    // --------------------------------
                    // SET DATABASE VALUES
                    // --------------------------------

                    detailStatement.setString(
                            1,
                            attemptId
                    );

                    detailStatement.setString(
                            2,
                            email
                    );

                    detailStatement.setString(
                            3,
                            subject
                    );

                    detailStatement.setInt(
                            4,
                            questionNumber
                    );

                    detailStatement.setInt(
                            5,
                            question.getId()
                    );

                    detailStatement.setString(
                            6,
                            question.getQuestionText()
                    );

                    detailStatement.setString(
                            7,
                            question.getOptionA()
                    );

                    detailStatement.setString(
                            8,
                            question.getOptionB()
                    );

                    detailStatement.setString(
                            9,
                            question.getOptionC()
                    );

                    detailStatement.setString(
                            10,
                            question.getOptionD()
                    );

                    detailStatement.setString(
                            11,
                            userAnswerText
                    );

                    detailStatement.setString(
                            12,
                            correctAnswerText
                    );

                    detailStatement.setString(
                            13,
                            userAnswerLetter
                    );

                    detailStatement.setString(
                            14,
                            correctAnswerLetter
                    );

                    detailStatement.setBoolean(
                            15,
                            isCorrect
                    );

                    detailStatement.addBatch();

                    questionNumber++;
                }

                // ==================================
                // EXECUTE ALL QUESTION INSERTS
                // ==================================

                detailStatement.executeBatch();

                // ==================================
                // COMMIT EVERYTHING
                // ==================================

                connection.commit();

            } catch (SQLException e) {

                connection.rollback();

                throw e;
            }

        } catch (SQLException e) {

            e.printStackTrace();

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println("""
                    <!DOCTYPE html>

                    <html lang="en">

                    <head>

                        <meta charset="UTF-8">

                        <meta
                            name="viewport"
                            content="width=device-width, initial-scale=1.0"
                        >

                        <title>
                            QuizForge | Error
                        </title>

                        <link
                            rel="stylesheet"
                            href="
                    """);

            response.getWriter().println(
                    contextPath + "/CSS/style.css"
            );

            response.getWriter().println("""
                            "
                        >

                    </head>

                    <body class="result-page">

                        <main class="result-wrapper">

                            <section class="result-card">

                                <div class="result-brand">
                                    <span>QUIZFORGE</span>
                                </div>

                                <div class="result-icon">
                                    ⚠️
                                </div>

                                <p class="result-label">
                                    QUIZ ERROR
                                </p>

                                <h1>
                                    Unable to Save Result
                                </h1>

                                <p class="result-message">
                                    Your quiz could not be saved.
                                    Please try submitting the quiz again.
                                </p>

                                <div class="result-actions">

                                    <a
                                        href="
                    """);

            response.getWriter().println(
                    contextPath + "/quiz.html"
            );

            response.getWriter().println("""
                                        "
                                        class="result-primary-button"
                                    >
                                        Try Again
                                    </a>

                                    <a
                                        href="
                    """);

            response.getWriter().println(
                    contextPath + "/dashboard.html"
            );

            response.getWriter().println("""
                                        "
                                        class="result-secondary-button"
                                    >
                                        Dashboard
                                    </a>

                                </div>

                            </section>

                        </main>

                    </body>

                    </html>
                    """);

            return;
        }

        // ==========================================
        // STORE RESULT IN SESSION
        // ==========================================

        session.setAttribute(
                "quizScore",
                score
        );

        session.setAttribute(
                "quizTotalQuestions",
                totalQuestions
        );

        session.setAttribute(
                "quizPercentage",
                percentage
        );

        session.setAttribute(
                "quizUnanswered",
                unanswered
        );

        session.setAttribute(
                "quizSubject",
                subject
        );

        session.setAttribute(
                "quizDifficulty",
                difficulty
        );

        session.setAttribute(
                "quizAttemptId",
                attemptId
        );

        // ==========================================
        // RESULT PAGE
        // ==========================================

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        response.getWriter().println("""
                <!DOCTYPE html>

                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>
                        QuizForge | Result
                    </title>

                    <link
                        rel="stylesheet"
                        href="
                """);

        response.getWriter().println(
                contextPath + "/CSS/style.css"
        );

        response.getWriter().println("""
                        "
                    >

                </head>

                <body class="result-page">

                    <main class="result-wrapper">

                        <section class="result-card">

                            <div class="result-brand">

                                <span>
                                    QUIZFORGE
                                </span>

                            </div>

                            <div class="result-icon">
                                🎉
                            </div>

                            <p class="result-label">
                                QUIZ COMPLETED
                            </p>

                            <h1>
                                Your Result
                            </h1>

                            <p class="result-message">
                """);

        response.getWriter().println(
                "You completed the "
                        + escapeHtml(subject)
                        + " quiz."
        );

        response.getWriter().println("""
                            </p>

                            <div class="score-circle">

                                <div class="score-number">
                """);

        response.getWriter().println(
                score
        );

        response.getWriter().println("""
                                    <span>
                                        /
                """);

        response.getWriter().println(
                totalQuestions
        );

        response.getWriter().println("""
                                    </span>
                                </div>

                                <div class="score-text">
                                    SCORE
                                </div>

                            </div>

                            <div class="result-stats">

                                <div class="result-stat">

                                    <span class="stat-value">
                """);

        response.getWriter().println(
                totalQuestions
        );

        response.getWriter().println("""
                                    </span>

                                    <span class="stat-label">
                                        Total Questions
                                    </span>

                                </div>

                                <div class="result-stat">

                                    <span class="stat-value">
                """);

        response.getWriter().println(
                String.format(
                        "%.1f%%",
                        percentage
                )
        );

        response.getWriter().println("""
                                    </span>

                                    <span class="stat-label">
                                        Percentage
                                    </span>

                                </div>

                                <div class="result-stat">

                                    <span class="stat-value">
                """);

        response.getWriter().println(
                unanswered
        );

        response.getWriter().println("""
                                    </span>

                                    <span class="stat-label">
                                        Unanswered
                                    </span>

                                </div>

                            </div>

                            <p class="result-message">

                                Unanswered questions are counted
                                as incorrect. You were allowed
                                to skip them.

                            </p>

                            <div class="result-actions">

                                <a
                                    href="
                """);

        response.getWriter().println(
                contextPath + "/quiz.html"
        );

        response.getWriter().println("""
                                    "
                                    class="result-primary-button"
                                >
                                    Try Again
                                </a>

                                <a
                                    href="
                """);

        response.getWriter().println(
                contextPath + "/quiz-history"
        );

        response.getWriter().println("""
                                    "
                                    class="result-secondary-button"
                                >
                                    Quiz History
                                </a>

                                <a
                                    href="
                """);

        response.getWriter().println(
                contextPath + "/dashboard.html"
        );

        response.getWriter().println("""
                                    "
                                    class="result-secondary-button"
                                >
                                    Dashboard
                                </a>

                            </div>

                        </section>

                    </main>

                </body>

                </html>
                """);

        // ==========================================
        // CLEAR CURRENT QUIZ
        // ==========================================

        session.removeAttribute(
                "quizQuestions"
        );
    }

    // ==================================================
    // CHECK ANSWER
    // ==================================================

    private boolean isCorrectAnswer(
            Question question,
            String selectedAnswer,
            String correctAnswer) {

        if (selectedAnswer == null
                || selectedAnswer.isBlank()
                || correctAnswer == null
                || correctAnswer.isBlank()) {

            return false;
        }

        String selected =
                selectedAnswer.trim();

        String correct =
                correctAnswer.trim();

        // First compare A/B/C/D directly.
        if (selected.equalsIgnoreCase(correct)) {
            return true;
        }

        // Otherwise compare actual option text.
        String selectedText =
                getOptionText(
                        question,
                        getAnswerLetter(
                                question,
                                selected
                        )
                );

        String correctText =
                getOptionText(
                        question,
                        getAnswerLetter(
                                question,
                                correct
                        )
                );

        return selectedText != null
                && correctText != null
                && selectedText.trim()
                        .equalsIgnoreCase(
                                correctText.trim()
                        );
    }

    // ==================================================
    // GET OPTION TEXT
    // ==================================================

    private String getOptionText(
            Question question,
            String letter) {

        if (question == null
                || letter == null) {

            return null;
        }

        switch (letter.toUpperCase()) {

            case "A":
                return question.getOptionA();

            case "B":
                return question.getOptionB();

            case "C":
                return question.getOptionC();

            case "D":
                return question.getOptionD();

            default:
                return null;
        }
    }

    // ==================================================
    // GET ANSWER LETTER
    // ==================================================

    private String getAnswerLetter(
            Question question,
            String answer) {

        if (question == null
                || answer == null
                || answer.isBlank()) {

            return null;
        }

        String cleanAnswer =
                answer.trim();

        // Already A/B/C/D
        if (cleanAnswer.equalsIgnoreCase("A")
                || cleanAnswer.equalsIgnoreCase("B")
                || cleanAnswer.equalsIgnoreCase("C")
                || cleanAnswer.equalsIgnoreCase("D")) {

            return cleanAnswer.toUpperCase();
        }

        // Compare actual option text
        if (question.getOptionA() != null
                && cleanAnswer.equalsIgnoreCase(
                        question.getOptionA().trim()
                )) {

            return "A";
        }

        if (question.getOptionB() != null
                && cleanAnswer.equalsIgnoreCase(
                        question.getOptionB().trim()
                )) {

            return "B";
        }

        if (question.getOptionC() != null
                && cleanAnswer.equalsIgnoreCase(
                        question.getOptionC().trim()
                )) {

            return "C";
        }

        if (question.getOptionD() != null
                && cleanAnswer.equalsIgnoreCase(
                        question.getOptionD().trim()
                )) {

            return "D";
        }

        return null;
    }

    // ==================================================
    // ESCAPE HTML
    // ==================================================

    private String escapeHtml(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}