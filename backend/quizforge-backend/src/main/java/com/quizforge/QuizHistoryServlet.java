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

@WebServlet("/quiz-history")
public class QuizHistoryServlet extends HttpServlet {

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

        if (session == null ||
            session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    contextPath + "/login.html"
            );

            return;
        }


        String email =
                (String)
                session.getAttribute(
                        "userEmail"
                );


        response.setContentType(
                "text/html;charset=UTF-8"
        );


        StringBuilder html =
                new StringBuilder();


        html.append("""
            <!DOCTYPE html>

            <html lang="en">

            <head>

                <meta charset="UTF-8">

                <meta name="viewport"
                      content="width=device-width, initial-scale=1.0">

                <title>QuizForge | Quiz History</title>

                <link rel="stylesheet"
                      href="
        """);


        html.append(
                contextPath
                + "/CSS/style.css"
        );


        html.append("""
                ">

            </head>


            <body class="history-page">


                <main class="history-wrapper">


                    <section class="history-card">


                        <div class="history-header">


                            <span class="dashboard-badge">
                                QUIZFORGE
                            </span>


                            <h1>
                                Quiz History
                            </h1>


                            <p>
                                Review your previous quiz attempts
                                and detailed answers.
                            </p>


                        </div>


                        <div class="history-table-wrapper">


                            <table class="history-table">


                                <thead>


                                    <tr>

                                        <th>
                                            Subject
                                        </th>

                                        <th>
                                            Score
                                        </th>

                                        <th>
                                            Total
                                        </th>

                                        <th>
                                            Percentage
                                        </th>

                                        <th>
                                            Date
                                        </th>

                                        <th>
                                            Details
                                        </th>

                                    </tr>


                                </thead>


                                <tbody>
        """);


        // ==========================================
        // GET HISTORY
        // ==========================================

        String sql =
                "SELECT "
                + "subject, "
                + "score, "
                + "total_questions, "
                + "percentage, "
                + "quiz_date, "
                + "attempt_id "
                + "FROM quiz_results "
                + "WHERE user_email = ? "
                + "ORDER BY quiz_date DESC";


        boolean hasHistory =
                false;


        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email
            );


            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    hasHistory = true;


                    String subject =
                            resultSet.getString(
                                    "subject"
                            );


                    int score =
                            resultSet.getInt(
                                    "score"
                            );


                    int total =
                            resultSet.getInt(
                                    "total_questions"
                            );


                    double percentage =
                            resultSet.getDouble(
                                    "percentage"
                            );


                    String attemptId =
                            resultSet.getString(
                                    "attempt_id"
                            );


                    String scoreClass;


                    if (percentage >= 80) {

                        scoreClass =
                                "score-excellent";

                    } else if (percentage >= 50) {

                        scoreClass =
                                "score-good";

                    } else {

                        scoreClass =
                                "score-low";
                    }


                    html.append("""
                                    <tr>


                                        <td>

                                            <span
                                                class="subject-badge">
                    """);


                    html.append(
                            escapeHtml(subject)
                    );


                    html.append("""
                                            </span>

                                        </td>


                                        <td
                                            class="score-cell">
                    """);


                    html.append(
                            score
                    );


                    html.append(
                            " / "
                    );


                    html.append(
                            total
                    );


                    html.append("""
                                        </td>


                                        <td>
                    """);


                    html.append(
                            total
                    );


                    html.append("""
                                        </td>


                                        <td>


                                            <span
                                                class="percentage-badge
                    """);


                    html.append(
                            " "
                            + scoreClass
                    );


                    html.append("""
                                                ">

                    """);


                    html.append(
                            String.format(
                                    "%.1f%%",
                                    percentage
                            )
                    );


                    html.append("""
                                            </span>


                                        </td>


                                        <td
                                            class="history-date">
                    """);


                    html.append(
                            escapeHtml(
                                    String.valueOf(
                                            resultSet.getTimestamp(
                                                    "quiz_date"
                                            )
                                    )
                            )
                    );


                    html.append("""
                                        </td>


                                        <td>
                    """);


                    if (attemptId != null &&
                        !attemptId.isBlank()) {

                        html.append(
                                "<a "
                                + "class=\"history-view-button\" "
                                + "href=\""
                                + contextPath
                                + "/quiz-history-detail?attemptId="
                                + attemptId
                                + "\">"
                                + "View Details"
                                + "</a>"
                        );

                    } else {

                        html.append(
                                "<span "
                                + "class=\"history-summary-only\">"
                                + "Summary Only"
                                + "</span>"
                        );
                    }


                    html.append("""
                                        </td>


                                    </tr>
                    """);
                }
            }


        } catch (SQLException e) {

            e.printStackTrace();


            html.append("""
                                    <tr>

                                        <td
                                            colspan="6"
                                            class="history-error">

                                            Unable to load quiz history.

                                        </td>

                                    </tr>
            """);
        }


        if (!hasHistory) {

            html.append("""
                                    <tr>

                                        <td
                                            colspan="6"
                                            class="no-history">


                                            <div
                                                class="no-history-icon">

                                                📚

                                            </div>


                                            <h3>
                                                No quizzes taken yet
                                            </h3>


                                            <p>
                                                Start your first quiz
                                                to see your progress here.
                                            </p>


                                        </td>

                                    </tr>
            """);
        }


        html.append("""
                                </tbody>


                            </table>


                        </div>


                        <div class="history-footer">


                            <a
                                href="
        """);


        html.append(
                contextPath
                + "/dashboard"
        );


        html.append("""
                                "
                                class="history-back-button">

                                ← Dashboard

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
    // ESCAPE HTML
    // ==================================================

    private String escapeHtml(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                )
                .replace(
                        "\"",
                        "&quot;"
                )
                .replace(
                        "'",
                        "&#39;"
                );
    }
}