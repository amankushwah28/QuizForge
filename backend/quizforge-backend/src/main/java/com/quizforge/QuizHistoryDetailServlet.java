package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/quiz-history-detail")
public class QuizHistoryDetailServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        String contextPath =
                request.getContextPath();

        // ==========================================
        // LOGIN CHECK
        // ==========================================

        if (session == null
                || session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }

        String email =
                (String) session.getAttribute(
                        "userEmail"
                );

        String attemptId =
                request.getParameter(
                        "attemptId"
                );

        if (attemptId == null
                || attemptId.isBlank()) {

            response.sendRedirect(
                    contextPath + "/quiz-history"
            );

            return;
        }

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        StringBuilder html =
                new StringBuilder();

        // ==========================================
        // PAGE HEADER
        // ==========================================

        html.append("""
                <!DOCTYPE html>

                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>
                        QuizForge | Quiz Details
                    </title>

                    <link
                        rel="stylesheet"
                        href="
                """);

        html.append(
                contextPath + "/CSS/style.css"
        );

        html.append("""
                        "
                    >

                </head>

                <body class="history-page">

                    <main class="history-wrapper">

                        <section class="history-card">

                            <div class="history-header">

                                <span class="dashboard-badge">
                                    QUIZFORGE
                                </span>

                                <h1>
                                    Quiz Attempt Details
                                </h1>

                                <p>
                                    Review your answers for this quiz attempt.
                                </p>

                            </div>

                            <div class="history-detail-list">
                """);

        // ==========================================
        // IMPORTANT DATABASE QUERY
        // ==========================================
        //
        // We use user_answer instead of
        // selected_answer because your actual
        // database contains user_answer.
        //
        // We also read the options directly from
        // quiz_attempt_details so the history keeps
        // the exact question snapshot from the
        // original attempt.
        //
        // ==========================================

        String sql = """
                SELECT
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
                FROM quiz_attempt_details
                WHERE attempt_id = ?
                  AND user_email = ?
                ORDER BY question_number ASC
                """;

        boolean hasDetails = false;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    attemptId
            );

            statement.setString(
                    2,
                    email
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    hasDetails = true;

                    int questionNumber =
                            resultSet.getInt(
                                    "question_number"
                            );

                    String questionText =
                            resultSet.getString(
                                    "question_text"
                            );

                    String optionA =
                            resultSet.getString(
                                    "option_a"
                            );

                    String optionB =
                            resultSet.getString(
                                    "option_b"
                            );

                    String optionC =
                            resultSet.getString(
                                    "option_c"
                            );

                    String optionD =
                            resultSet.getString(
                                    "option_d"
                            );

                    String userAnswer =
                            resultSet.getString(
                                    "user_answer"
                            );

                    String correctAnswer =
                            resultSet.getString(
                                    "correct_answer"
                            );

                    String userAnswerLetter =
                            resultSet.getString(
                                    "user_answer_letter"
                            );

                    String correctAnswerLetter =
                            resultSet.getString(
                                    "correct_answer_letter"
                            );

                    boolean isCorrect =
                            resultSet.getBoolean(
                                    "is_correct"
                            );

                    // ==================================
                    // QUESTION CARD
                    // ==================================

                    html.append("""
                            <div class="history-detail-question">

                                <div
                                    class="history-detail-question-number"
                                >
                                    Question
                            """);

                    html.append(
                            questionNumber
                    );

                    html.append("""
                                </div>

                                <h3>
                            """);

                    html.append(
                            escapeHtml(
                                    questionText
                            )
                    );

                    html.append("""
                                </h3>

                                <div class="history-detail-options">
                            """);

                    // ==================================
                    // OPTION A
                    // ==================================

                    appendOption(
                            html,
                            "A",
                            optionA,
                            userAnswer,
                            userAnswerLetter,
                            correctAnswer,
                            correctAnswerLetter
                    );

                    // ==================================
                    // OPTION B
                    // ==================================

                    appendOption(
                            html,
                            "B",
                            optionB,
                            userAnswer,
                            userAnswerLetter,
                            correctAnswer,
                            correctAnswerLetter
                    );

                    // ==================================
                    // OPTION C
                    // ==================================

                    appendOption(
                            html,
                            "C",
                            optionC,
                            userAnswer,
                            userAnswerLetter,
                            correctAnswer,
                            correctAnswerLetter
                    );

                    // ==================================
                    // OPTION D
                    // ==================================

                    appendOption(
                            html,
                            "D",
                            optionD,
                            userAnswer,
                            userAnswerLetter,
                            correctAnswer,
                            correctAnswerLetter
                    );

                    html.append("""
                                </div>

                                <div class="
                            """);

                    if (isCorrect) {

                        html.append(
                                "history-answer-correct"
                        );

                    } else {

                        html.append(
                                "history-answer-wrong"
                        );
                    }

                    html.append("\">");

                    if (isCorrect) {

                        html.append(
                                "✓ Correct Answer"
                        );

                    } else {

                        html.append(
                                "✗ Incorrect Answer"
                        );
                    }

                    html.append("""
                                </div>

                            </div>
                            """);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();

            html.append("""
                    <div class="history-error">

                        Unable to load quiz details.

                    </div>
                    """);
        }

        // ==========================================
        // NO DETAILS
        // ==========================================

        if (!hasDetails) {

            html.append("""
                    <div class="no-history">

                        <div class="no-history-icon">
                            📚
                        </div>

                        <h3>
                            No detailed answers found
                        </h3>

                        <p>
                            This attempt does not contain
                            question-level details.
                        </p>

                    </div>
                    """);
        }

        // ==========================================
        // FOOTER
        // ==========================================

        html.append("""
                            </div>

                            <div class="history-footer">

                                <a
                                    href="
                """);

        html.append(
                contextPath + "/quiz-history"
        );

        html.append("""
                                    "
                                    class="history-back-button"
                                >
                                    ← Quiz History
                                </a>

                            </div>

                        </section>

                    </main>

                </body>

                </html>
                """);

        response.getWriter().println(
                html
        );
    }

    // ==================================================
    // OPTION HTML
    // ==================================================

    private void appendOption(
            StringBuilder html,
            String optionLetter,
            String optionText,
            String userAnswer,
            String userAnswerLetter,
            String correctAnswer,
            String correctAnswerLetter) {

        boolean selected =
                isSameAnswer(
                        optionLetter,
                        optionText,
                        userAnswer,
                        userAnswerLetter
                );

        boolean correct =
                isSameAnswer(
                        optionLetter,
                        optionText,
                        correctAnswer,
                        correctAnswerLetter
                );

        html.append("""
                <div class="history-detail-option
                """);

        if (correct) {

            html.append(
                    " history-option-correct"
            );

        } else if (selected) {

            html.append(
                    " history-option-selected"
            );
        }

        html.append("\">");

        // ==========================================
        // OPTION LETTER
        // ==========================================

        html.append("""
                    <span class="history-option-letter">
                """);

        html.append(
                escapeHtml(
                        optionLetter
                )
        );

        html.append("""
                    </span>

                    <span class="history-option-text">
                """);

        // ==========================================
        // OPTION TEXT
        // ==========================================

        html.append(
                escapeHtml(
                        optionText
                )
        );

        html.append("""
                    </span>
                """);

        // ==========================================
        // LABEL
        // ==========================================

        if (correct) {

            html.append("""
                    <span class="history-option-label">
                        Correct Answer
                    </span>
                    """);

        } else if (selected) {

            html.append("""
                    <span class="history-option-label">
                        Your Answer
                    </span>
                    """);
        }

        html.append("""
                </div>
                """);
    }

    // ==================================================
    // ANSWER COMPARISON
    // ==================================================

    private boolean isSameAnswer(
            String optionLetter,
            String optionText,
            String answer,
            String answerLetter) {

        // ------------------------------------------
        // First use stored answer letter
        // ------------------------------------------

        if (answerLetter != null
                && !answerLetter.isBlank()) {

            if (optionLetter.equalsIgnoreCase(
                    answerLetter.trim()
            )) {

                return true;
            }
        }

        // ------------------------------------------
        // Then compare answer value
        // ------------------------------------------

        if (answer == null
                || answer.isBlank()) {

            return false;
        }

        String cleanAnswer =
                answer.trim();

        // Answer is A/B/C/D
        if (optionLetter.equalsIgnoreCase(
                cleanAnswer
        )) {

            return true;
        }

        // Answer is actual option text
        return optionText != null
                && optionText.trim()
                        .equalsIgnoreCase(
                                cleanAnswer
                        );
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