package com.quizforge;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String subject =
                request.getParameter("subject");

        String difficulty =
                request.getParameter("difficulty");

        String numberText =
                request.getParameter("number");

        String contextPath =
                request.getContextPath();

        // ==========================================
        // LOGIN CHECK
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
        // VALIDATE QUIZ SETTINGS
        // ==========================================

        if (subject == null
                || subject.isBlank()
                || difficulty == null
                || difficulty.isBlank()
                || numberText == null
                || numberText.isBlank()) {

            response.sendRedirect(
                    contextPath + "/quiz.html"
            );

            return;
        }

        int number;

        try {

            number =
                    Integer.parseInt(
                            numberText
                    );

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    contextPath + "/quiz.html"
            );

            return;
        }

        if (number <= 0) {

            response.sendRedirect(
                    contextPath + "/quiz.html"
            );

            return;
        }

        // ==========================================
        // GET QUESTIONS
        // ==========================================

        QuestionDAO questionDAO =
                new QuestionDAO();

        List<Question> questions =
                questionDAO.getQuestions(
                        subject,
                        difficulty,
                        number
                );

        // ==========================================
        // STORE QUIZ DATA IN SESSION
        // ==========================================

        session.setAttribute(
                "quizQuestions",
                questions
        );

        session.setAttribute(
                "quizSubject",
                subject
        );

        session.setAttribute(
                "quizDifficulty",
                difficulty
        );

        // ==========================================
        // RESPONSE
        // ==========================================

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        var out =
                response.getWriter();

        // ==========================================
        // HTML START
        // ==========================================

        out.println("""
                <!DOCTYPE html>

                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>
                        QuizForge | Quiz
                    </title>

                    <link
                        rel="stylesheet"
                        href="
                """);

        out.println(
                contextPath + "/CSS/style.css"
        );

        out.println("""
                        "
                    >

                </head>

                <body class="quiz-page">

                    <main class="container">

                        <div class="quiz-card">

                            <div class="quiz-header">

                                <span class="dashboard-badge">
                                    QUIZFORGE
                                </span>

                                <h1>
                                    🎯 QuizForge Quiz
                                </h1>

                                <p>
                """);

        out.println(
                escapeHtml(subject)
                        + " • "
                        + escapeHtml(difficulty)
        );

        out.println("""
                                </p>

                            </div>

                            <div
                                class="quiz-timer"
                                style="
                                    position: sticky;
                                    top: 12px;
                                    z-index: 1000;
                                "
                            >

                                ⏱️ Time Left:

                                <span id="timer">
                                    00:00
                                </span>

                            </div>
                """);

        // ==========================================
        // NO QUESTIONS
        // ==========================================

        if (questions == null
                || questions.isEmpty()) {

            out.println("""
                    <div class="question-card">

                        <h3>
                            No questions found.
                        </h3>

                        <p>
                            Try another subject or difficulty.
                        </p>

                    </div>

                    <a href="
                """);

            out.println(
                    contextPath + "/quiz.html"
            );

            out.println("""
                    ">
                        ← Back to Quiz Selection
                    </a>
                """);

        } else {

            int totalQuestions =
                    questions.size();

            // ======================================
            // PROGRESS HEADER
            // ======================================

            out.println("""
                    <div class="quiz-progress-header">

                        <div class="quiz-progress-info">

                            <span id="answeredText">
                                Answered 0 of
                """);

            out.println(
                    totalQuestions
            );

            out.println("""
                            </span>

                            <span id="progressPercent">
                                0%
                            </span>

                        </div>

                        <div class="question-progress-track">

                            <div
                                class="question-progress-fill"
                                id="progressFill"
                                style="width: 0%"
                            >
                            </div>

                        </div>

                    </div>

                    <form
                        id="quizForm"
                        action="
                """);

            out.println(
                    contextPath + "/submit-quiz"
            );

            out.println("""
                        "
                        method="post"
                    >
                """);

            int questionNumber = 1;

            // ======================================
            // QUESTIONS
            // ======================================

            for (Question question : questions) {

                int questionId =
                        question.getId();

                out.println("""
                        <div class="question-card">

                            <p class="question-number">
                                Question
                """);

                out.println(
                        questionNumber
                );

                out.println(
                        " of " + totalQuestions
                );

                out.println("""
                            </p>

                            <h3>
                """);

                out.println(
                        escapeHtml(
                                question.getQuestionText()
                        )
                );

                out.println("""
                            </h3>

                """);

                // ==================================
                // OPTION A
                // ==================================

                /*
                 * IMPORTANT:
                 *
                 * The complete name attribute is
                 * generated in ONE println().
                 *
                 * Result:
                 *
                 * name="q123"
                 *
                 * NOT:
                 *
                 * name="q
                 * 123"
                 */

                out.println(
                        "<label class=\"answer-option\">"
                );

                out.println(
                        "<input "
                        + "type=\"radio\" "
                        + "name=\"q"
                        + questionId
                        + "\" "
                        + "value=\"A\">"
                );

                out.println(
                        "<span>"
                        + escapeHtml(
                                question.getOptionA()
                        )
                        + "</span>"
                );

                out.println(
                        "</label>"
                );

                // ==================================
                // OPTION B
                // ==================================

                out.println(
                        "<label class=\"answer-option\">"
                );

                out.println(
                        "<input "
                        + "type=\"radio\" "
                        + "name=\"q"
                        + questionId
                        + "\" "
                        + "value=\"B\">"
                );

                out.println(
                        "<span>"
                        + escapeHtml(
                                question.getOptionB()
                        )
                        + "</span>"
                );

                out.println(
                        "</label>"
                );

                // ==================================
                // OPTION C
                // ==================================

                out.println(
                        "<label class=\"answer-option\">"
                );

                out.println(
                        "<input "
                        + "type=\"radio\" "
                        + "name=\"q"
                        + questionId
                        + "\" "
                        + "value=\"C\">"
                );

                out.println(
                        "<span>"
                        + escapeHtml(
                                question.getOptionC()
                        )
                        + "</span>"
                );

                out.println(
                        "</label>"
                );

                // ==================================
                // OPTION D
                // ==================================

                out.println(
                        "<label class=\"answer-option\">"
                );

                out.println(
                        "<input "
                        + "type=\"radio\" "
                        + "name=\"q"
                        + questionId
                        + "\" "
                        + "value=\"D\">"
                );

                out.println(
                        "<span>"
                        + escapeHtml(
                                question.getOptionD()
                        )
                        + "</span>"
                );

                out.println(
                        "</label>"
                );

                out.println("""
                        </div>
                """);

                questionNumber++;
            }

            // ======================================
            // SUBMIT BUTTON
            // ======================================

            out.println("""
                        <button
                            type="submit"
                            class="primary-button"
                        >

                            Submit Quiz

                        </button>

                    </form>
                """);
        }

        // ==========================================
        // BACK TO DASHBOARD
        // ==========================================

        out.println("""
                    <a href="
                """);

        out.println(
                contextPath + "/dashboard.html"
        );

        out.println("""
                    ">
                        ← Back to Dashboard
                    </a>

                </div>

            </main>
        """);

        // ==========================================
        // TIMER + PROGRESS JAVASCRIPT
        // ==========================================

        if (questions != null
                && !questions.isEmpty()) {

            int totalQuestions =
                    questions.size();

            /*
             * 5 questions  = 60 seconds
             * 10 questions = 120 seconds
             *
             * Therefore:
             *
             * 12 seconds per question.
             */

            int totalTime =
                    totalQuestions * 45;

            out.println("""
                <script>

                    const totalQuestions =
                """);

            out.println(
                    totalQuestions
            );

            out.println("""
                    ;

                    let timeLeft =
                """);

            out.println(
                    totalTime
            );

            out.println("""
                    ;

                    const timerElement =
                        document.getElementById(
                            "timer"
                        );

                    const quizForm =
                        document.getElementById(
                            "quizForm"
                        );


                    // =================================
                    // TIMER
                    // =================================

                    function updateTimer() {

                        const minutes =
                            Math.floor(
                                timeLeft / 60
                            );

                        const seconds =
                            timeLeft % 60;

                        timerElement.textContent =
                            String(minutes)
                                .padStart(2, "0")
                            + ":"
                            + String(seconds)
                                .padStart(2, "0");


                        if (timeLeft <= 0) {

                            clearInterval(
                                timerInterval
                            );

                            /*
                             * submit() sends all selected
                             * radio values to the servlet.
                             */

                            quizForm.submit();

                            return;
                        }

                        timeLeft--;
                    }


                    const timerInterval =
                        setInterval(
                            updateTimer,
                            1000
                        );


                    updateTimer();


                    // =================================
                    // PROGRESS
                    // =================================

                    function updateProgress() {

                        const answeredQuestions =
                            new Set();


                        document
                            .querySelectorAll(
                                'input[type="radio"]:checked'
                            )
                            .forEach(
                                function(input) {

                                    answeredQuestions.add(
                                        input.name
                                    );

                                }
                            );


                        const answered =
                            answeredQuestions.size;


                        const percentage =
                            Math.round(
                                (
                                    answered
                                    / totalQuestions
                                ) * 100
                            );


                        document.getElementById(
                            "progressFill"
                        ).style.width =
                            percentage + "%";


                        document.getElementById(
                            "answeredText"
                        ).textContent =
                            "Answered "
                            + answered
                            + " of "
                            + totalQuestions;


                        document.getElementById(
                            "progressPercent"
                        ).textContent =
                            percentage + "%";
                    }


                    // =================================
                    // ANSWER CHANGE LISTENER
                    // =================================

                    document
                        .querySelectorAll(
                            'input[type="radio"]'
                        )
                        .forEach(
                            function(input) {

                                input.addEventListener(
                                    "change",
                                    updateProgress
                                );

                            }
                        );

                </script>
            """);
        }

        // ==========================================
        // HTML END
        // ==========================================

        out.println("""
            </body>

            </html>
        """);
    }

    // ==========================================
    // ESCAPE HTML
    // ==========================================

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