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

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // ==========================================
        // CHECK ADMIN LOGIN
        // ==========================================

        if (!isAdminAuthenticated(
                request,
                response)) {

            return;
        }

        response.setContentType(
                "text/html;charset=UTF-8"
        );

        String contextPath =
                request.getContextPath();

        String editId =
                request.getParameter("edit");

        String status =
                request.getParameter("status");

        String filterDifficulty =
                request.getParameter("difficultyFilter");

        if (filterDifficulty == null ||
                !("Easy".equalsIgnoreCase(filterDifficulty)
                || "Medium".equalsIgnoreCase(filterDifficulty)
                || "Hard".equalsIgnoreCase(filterDifficulty))) {

            filterDifficulty = "All";
        }

        // ==========================================
        // DEFAULT FORM VALUES
        // ==========================================

        String subject = "";
        String topic = "";
        String difficulty = "";

        String questionText = "";

        String optionA = "";
        String optionB = "";
        String optionC = "";
        String optionD = "";

        String correctAnswer = "";

        boolean editMode =
                editId != null
                && !editId.isBlank();

        // ==========================================
        // LOAD QUESTION FOR EDIT
        // ==========================================

        if (editMode) {

            String sql =
                    "SELECT subject, topic, difficulty, "
                    + "question_text, option_a, option_b, "
                    + "option_c, option_d, correct_answer "
                    + "FROM questions "
                    + "WHERE id = ?";

            try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
            ) {

                statement.setInt(
                        1,
                        Integer.parseInt(editId)
                );

                try (
                    ResultSet resultSet =
                            statement.executeQuery()
                ) {

                    if (resultSet.next()) {

                        subject =
                                resultSet.getString(
                                        "subject"
                                );

                        topic =
                                resultSet.getString(
                                        "topic"
                                );

                        difficulty =
                                resultSet.getString(
                                        "difficulty"
                                );

                        questionText =
                                resultSet.getString(
                                        "question_text"
                                );

                        optionA =
                                resultSet.getString(
                                        "option_a"
                                );

                        optionB =
                                resultSet.getString(
                                        "option_b"
                                );

                        optionC =
                                resultSet.getString(
                                        "option_c"
                                );

                        optionD =
                                resultSet.getString(
                                        "option_d"
                                );

                        correctAnswer =
                                resultSet.getString(
                                        "correct_answer"
                                );
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // ==========================================
        // BUILD HTML
        // ==========================================

        StringBuilder html =
                new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");

        html.append("<head>");

        html.append(
                "<meta charset=\"UTF-8\">"
        );

        html.append(
                "<meta name=\"viewport\" "
                + "content=\"width=device-width, "
                + "initial-scale=1.0\">"
        );

        html.append(
                "<title>QuizForge | Admin</title>"
        );

        // ==========================================
        // CSS
        // ==========================================

        html.append(
                "<link rel=\"stylesheet\" "
                + "href=\""
                + contextPath
                + "/CSS/style.css\">"
        );

        html.append("""
                <style>
                    .admin-filter-card {
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        gap: 18px;
                        margin: 22px 0 16px;
                        padding: 16px 18px;
                        border: 1px solid #e5e7eb;
                        border-radius: 14px;
                        background: #f8fafc;
                    }

                    .admin-filter-title {
                        display: flex;
                        flex-direction: column;
                        gap: 4px;
                    }

                    .admin-filter-title strong {
                        font-size: 15px;
                        color: #111827;
                    }

                    .admin-filter-title span {
                        font-size: 12px;
                        color: #667085;
                    }

                    .admin-filter-form select {
                        min-width: 150px;
                        padding: 10px 12px;
                        border: 1px solid #d0d5dd;
                        border-radius: 10px;
                        background: #ffffff;
                        font-size: 13px;
                        font-weight: 600;
                        color: #344054;
                        cursor: pointer;
                    }

                    .admin-question-count {
                        display: inline-flex;
                        align-items: center;
                        justify-content: center;
                        min-width: 28px;
                        height: 28px;
                        padding: 0 8px;
                        margin-left: 6px;
                        border-radius: 999px;
                        background: #eef2ff;
                        color: #4f46e5;
                        font-size: 12px;
                        vertical-align: middle;
                    }

                    @media (max-width: 600px) {
                        .admin-filter-card {
                            align-items: stretch;
                            flex-direction: column;
                        }

                        .admin-filter-form select {
                            width: 100%;
                        }
                    }
                </style>
        """);

        html.append("</head>");

        // ==========================================
        // BODY
        // ==========================================

        html.append(
                "<body class=\"admin-page\">"
        );

        html.append(
                "<main class=\"admin-wrapper\">"
        );

        html.append(
                "<section class=\"admin-card\">"
        );

        // ==========================================
        // HEADER
        // ==========================================

        html.append(
                "<div class=\"admin-top\">"
        );

        html.append("<div>");

        html.append(
                "<span class=\"admin-badge\">"
                + "QUIZFORGE ADMIN"
                + "</span>"
        );

        html.append(
                "<h1>"
                + "Question Management"
                + "</h1>"
        );

        html.append(
                "<p>"
                + "Add, edit and remove quiz questions."
                + "</p>"
        );

        html.append("</div>");

        // ==========================================
        // HEADER BUTTONS
        // ==========================================

        html.append(
                "<div class=\"admin-header-actions\">"
        );

        html.append(
                "<a "
                + "class=\"admin-dashboard-button\" "
                + "href=\""
                + contextPath
                + "/profile\">"
                + "My Profile"
                + "</a>"
        );

        html.append(
                "<a "
                + "class=\"admin-dashboard-button\" "
                + "href=\""
                + contextPath
                + "/dashboard\">"
                + "← Dashboard"
                + "</a>"
        );

        html.append(
                "<a "
                + "class=\"admin-logout-button\" "
                + "href=\""
                + contextPath
                + "/logout\">"
                + "Logout"
                + "</a>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "</div>"
        );

        // ==========================================
        // STATUS MESSAGE
        // ==========================================

        if ("added".equals(status)) {

            html.append(
                    "<div class=\"admin-success-message\">"
                    + "Question added successfully."
                    + "</div>"
            );

        } else if ("updated".equals(status)) {

            html.append(
                    "<div class=\"admin-success-message\">"
                    + "Question updated successfully."
                    + "</div>"
            );

        } else if ("deleted".equals(status)) {

            html.append(
                    "<div class=\"admin-success-message\">"
                    + "Question deleted successfully."
                    + "</div>"
            );

        } else if ("error".equals(status)) {

            html.append(
                    "<div class=\"admin-error-message\">"
                    + "Question operation failed. "
                    + "Check the Tomcat console."
                    + "</div>"
            );
        }

        // ==========================================
        // FORM CARD
        // ==========================================

        html.append(
                "<div class=\"admin-form-card\">"
        );

        html.append(
                "<h2>"
                + (
                    editMode
                        ? "Edit Question"
                        : "Add New Question"
                )
                + "</h2>"
        );

        // ==========================================
        // FORM
        // ==========================================

        html.append(
                "<form "
                + "method=\"post\" "
                + "action=\""
                + contextPath
                + "/admin-action\" "
                + "class=\"admin-form\">"
        );

        // ==========================================
        // ACTION
        // ==========================================

        html.append(
                "<input "
                + "type=\"hidden\" "
                + "name=\"action\" "
                + "value=\""
                + (
                    editMode
                        ? "update"
                        : "add"
                )
                + "\">"
        );

        // ==========================================
        // ID FOR UPDATE
        // ==========================================

        if (editMode) {

            html.append(
                    "<input "
                    + "type=\"hidden\" "
                    + "name=\"id\" "
                    + "value=\""
                    + escapeHtml(editId)
                    + "\">"
            );
        }

        // ==========================================
        // SUBJECT
        // ==========================================

        html.append(
                "<div class=\"admin-form-grid\">"
        );

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Subject</label>"
        );

        html.append(
                "<select "
                + "name=\"subject\" "
                + "required>"
        );

        html.append(
                createOption(
                        "Java",
                        subject
                )
        );

        html.append(
                createOption(
                        "HTML",
                        subject
                )
        );

        html.append(
                createOption(
                        "CSS",
                        subject
                )
        );

        html.append(
                createOption(
                        "JavaScript",
                        subject
                )
        );

        html.append(
                createOption(
                        "MySQL",
                        subject
                )
        );

        html.append("</select>");

        html.append("</div>");

        // ==========================================
        // TOPIC
        // ==========================================

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Topic</label>"
        );

        html.append(
                "<input "
                + "type=\"text\" "
                + "name=\"topic\" "
                + "value=\""
                + escapeHtml(topic)
                + "\" "
                + "required>"
        );

        html.append("</div>");

        // ==========================================
        // DIFFICULTY
        // ==========================================

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Difficulty</label>"
        );

        html.append(
                "<select "
                + "name=\"difficulty\" "
                + "required>"
        );

        html.append(
                createOption(
                        "Easy",
                        difficulty
                )
        );

        html.append(
                createOption(
                        "Medium",
                        difficulty
                )
        );

        html.append(
                createOption(
                        "Hard",
                        difficulty
                )
        );

        html.append("</select>");

        html.append("</div>");

        html.append("</div>");

        // ==========================================
        // QUESTION
        // ==========================================

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Question</label>"
        );

        html.append(
                "<textarea "
                + "name=\"question_text\" "
                + "rows=\"3\" "
                + "required>"
        );

        html.append(
                escapeHtml(questionText)
        );

        html.append("</textarea>");

        html.append("</div>");

        // ==========================================
        // OPTIONS
        // ==========================================

        html.append(
                "<div class=\"admin-form-grid\">"
        );

        // ------------------------------------------
        // OPTION A
        // ------------------------------------------

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Option A</label>"
        );

        html.append(
                "<input "
                + "type=\"text\" "
                + "name=\"option_a\" "
                + "value=\""
                + escapeHtml(optionA)
                + "\" "
                + "required>"
        );

        html.append("</div>");

        // ------------------------------------------
        // OPTION B
        // ------------------------------------------

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Option B</label>"
        );

        html.append(
                "<input "
                + "type=\"text\" "
                + "name=\"option_b\" "
                + "value=\""
                + escapeHtml(optionB)
                + "\" "
                + "required>"
        );

        html.append("</div>");

        // ------------------------------------------
        // OPTION C
        // ------------------------------------------

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Option C</label>"
        );

        html.append(
                "<input "
                + "type=\"text\" "
                + "name=\"option_c\" "
                + "value=\""
                + escapeHtml(optionC)
                + "\" "
                + "required>"
        );

        html.append("</div>");

        // ------------------------------------------
        // OPTION D
        // ------------------------------------------

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Option D</label>"
        );

        html.append(
                "<input "
                + "type=\"text\" "
                + "name=\"option_d\" "
                + "value=\""
                + escapeHtml(optionD)
                + "\" "
                + "required>"
        );

        html.append("</div>");

        html.append("</div>");

        // ==========================================
        // CORRECT ANSWER
        // ==========================================

        html.append(
                "<div class=\"admin-field\">"
        );

        html.append(
                "<label>Correct Answer</label>"
        );

        html.append(
                "<select "
                + "name=\"correct_answer\" "
                + "required>"
        );

        html.append(
                createOption(
                        "A",
                        correctAnswer
                )
        );

        html.append(
                createOption(
                        "B",
                        correctAnswer
                )
        );

        html.append(
                createOption(
                        "C",
                        correctAnswer
                )
        );

        html.append(
                createOption(
                        "D",
                        correctAnswer
                )
        );

        html.append("</select>");

        html.append("</div>");

        // ==========================================
        // FORM BUTTONS
        // ==========================================

        html.append(
                "<div class=\"admin-form-actions\">"
        );

        html.append(
                "<button "
                + "type=\"submit\" "
                + "class=\"admin-save-button\">"
                + (
                    editMode
                        ? "Update Question"
                        : "Add Question"
                )
                + "</button>"
        );

        if (editMode) {

            html.append(
                    "<a "
                    + "href=\""
                    + contextPath
                    + "/admin\" "
                    + "class=\"admin-cancel-button\">"
                    + "Cancel"
                    + "</a>"
            );
        }

        html.append("</div>");

        html.append("</form>");

        html.append("</div>");

        // ==========================================
        // QUESTION COUNT
        // ==========================================

        int displayedQuestionCount = 0;

        String countSql;

        if ("All".equalsIgnoreCase(filterDifficulty)) {
            countSql = "SELECT COUNT(*) FROM questions";
        } else {
            countSql =
                    "SELECT COUNT(*) FROM questions "
                    + "WHERE difficulty = ?";
        }

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(countSql)
        ) {

            if (!"All".equalsIgnoreCase(filterDifficulty)) {
                statement.setString(1, filterDifficulty);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    displayedQuestionCount = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // ==========================================
        // QUESTION FILTER
        // ==========================================

        html.append(
                "<div class=\"admin-filter-card\">"
        );

        html.append(
                "<div class=\"admin-filter-title\">"
                + "<strong>Filter Questions</strong>"
                + "<span>View questions by difficulty</span>"
                + "</div>"
        );

        html.append(
                "<form method=\"get\" action=\""
                + contextPath
                + "/admin\" class=\"admin-filter-form\">"
        );

        html.append(
                "<select name=\"difficultyFilter\" "
                + "onchange=\"this.form.submit()\">"
        );

        html.append(
                createOption("All", filterDifficulty)
        );
        html.append(
                createOption("Easy", filterDifficulty)
        );
        html.append(
                createOption("Medium", filterDifficulty)
        );
        html.append(
                createOption("Hard", filterDifficulty)
        );

        html.append("</select>");
        html.append("</form>");
        html.append("</div>");

        // ==========================================
        // ALL QUESTIONS
        // ==========================================

        html.append(
                "<div class=\"admin-list-header\">"
        );

        html.append(
                "<h2>"
                + ("All".equalsIgnoreCase(filterDifficulty)
                    ? "All Questions"
                    : filterDifficulty + " Questions")
                + " <span class=\"admin-question-count\">"
                + displayedQuestionCount
                + "</span></h2>"
        );

        html.append("</div>");

        html.append(
                "<div class=\"admin-table-wrapper\">"
        );

        html.append(
                "<table class=\"admin-table\">"
        );

        html.append("<thead>");

        html.append("<tr>");

        html.append("<th>ID</th>");
        html.append("<th>Subject</th>");
        html.append("<th>Topic</th>");
        html.append("<th>Difficulty</th>");
        html.append("<th>Question</th>");
        html.append("<th>Answer</th>");
        html.append("<th>Actions</th>");

        html.append("</tr>");

        html.append("</thead>");

        html.append("<tbody>");

        // ==========================================
        // GET QUESTIONS
        // ==========================================

        String sql;

        if ("All".equalsIgnoreCase(filterDifficulty)) {
            sql =
                    "SELECT id, subject, topic, difficulty, "
                    + "question_text, correct_answer "
                    + "FROM questions "
                    + "ORDER BY id DESC";
        } else {
            sql =
                    "SELECT id, subject, topic, difficulty, "
                    + "question_text, correct_answer "
                    + "FROM questions "
                    + "WHERE difficulty = ? "
                    + "ORDER BY id DESC";
        }

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            if (!"All".equalsIgnoreCase(filterDifficulty)) {
                statement.setString(1, filterDifficulty);
            }

            try (ResultSet resultSet =
                         statement.executeQuery()) {

            while (resultSet.next()) {

                int id =
                        resultSet.getInt("id");

                String tableSubject =
                        resultSet.getString(
                                "subject"
                        );

                String tableTopic =
                        resultSet.getString(
                                "topic"
                        );

                String tableDifficulty =
                        resultSet.getString(
                                "difficulty"
                        );

                String tableQuestion =
                        resultSet.getString(
                                "question_text"
                        );

                String tableAnswer =
                        resultSet.getString(
                                "correct_answer"
                        );

                html.append("<tr>");

                html.append("<td>");
                html.append(id);
                html.append("</td>");

                html.append("<td>");
                html.append(
                        escapeHtml(tableSubject)
                );
                html.append("</td>");

                html.append("<td>");
                html.append(
                        escapeHtml(tableTopic)
                );
                html.append("</td>");

                html.append("<td>");
                html.append(
                        escapeHtml(tableDifficulty)
                );
                html.append("</td>");

                html.append(
                        "<td class=\"admin-question-text\">"
                );

                html.append(
                        escapeHtml(tableQuestion)
                );

                html.append("</td>");

                html.append("<td>");
                html.append(
                        escapeHtml(tableAnswer)
                );
                html.append("</td>");

                // ==================================
                // ACTIONS
                // ==================================

                html.append(
                        "<td class=\"admin-actions\">"
                );

                // EDIT

                html.append(
                        "<a "
                        + "class=\"admin-edit-button\" "
                        + "href=\""
                        + contextPath
                        + "/admin?edit="
                        + id
                        + "\">"
                        + "Edit"
                        + "</a>"
                );

                // DELETE

                html.append(
                        "<form "
                        + "method=\"post\" "
                        + "action=\""
                        + contextPath
                        + "/admin-action\">"
                );

                html.append(
                        "<input "
                        + "type=\"hidden\" "
                        + "name=\"action\" "
                        + "value=\"delete\">"
                );

                html.append(
                        "<input "
                        + "type=\"hidden\" "
                        + "name=\"id\" "
                        + "value=\""
                        + id
                        + "\">"
                );

                html.append(
                        "<button "
                        + "type=\"submit\" "
                        + "class=\"admin-delete-button\" "
                        + "onclick=\"return confirm('Delete this question?');\">"
                        + "Delete"
                        + "</button>"
                );

                html.append("</form>");

                html.append("</td>");

                html.append("</tr>");
            }

            }

        } catch (SQLException e) {

            e.printStackTrace();

            html.append(
                    "<tr>"
                    + "<td colspan=\"7\" "
                    + "class=\"admin-error\">"
                    + "Unable to load questions."
                    + "</td>"
                    + "</tr>"
            );
        }

        html.append("</tbody>");

        html.append("</table>");

        html.append("</div>");

        html.append("</section>");

        html.append("</main>");

        html.append("</body>");

        html.append("</html>");

        response.getWriter().println(
                html.toString()
        );
    }


    // ==================================================
    // ADMIN LOGIN CHECK
    // ==================================================

    private boolean isAdminAuthenticated(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
            !Boolean.TRUE.equals(
                    session.getAttribute(
                            "adminAuthenticated"
                    )
            )) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/admin-login.html"
            );

            return false;
        }

        return true;
    }


    // ==================================================
    // CREATE SELECT OPTION
    // ==================================================

    private String createOption(
            String value,
            String selectedValue) {

        StringBuilder html =
                new StringBuilder();

        html.append(
                "<option value=\""
        );

        html.append(
                escapeHtml(value)
        );

        html.append("\"");

        if (value.equalsIgnoreCase(
                selectedValue == null
                        ? ""
                        : selectedValue
        )) {

            html.append(
                    " selected"
            );
        }

        html.append(">");

        html.append(
                escapeHtml(value)
        );

        html.append("</option>");

        return html.toString();
    }


    // ==================================================
    // HTML ESCAPING
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