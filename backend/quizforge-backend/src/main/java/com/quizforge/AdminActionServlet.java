package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin-action")
public class AdminActionServlet extends HttpServlet {

    @Override
    protected void doPost(
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

        request.setCharacterEncoding(
                "UTF-8"
        );

        String action =
                request.getParameter("action");

        System.out.println(
                "================================="
        );

        System.out.println(
                "ADMIN ACTION = "
                + action
        );

        System.out.println(
                "================================="
        );


        try (
            Connection connection =
                    DatabaseConnection.getConnection()
        ) {

            // ==========================================
            // ADD
            // ==========================================

            if ("add".equals(action)) {

                String sql =
                        "INSERT INTO questions " +
                        "(subject, topic, difficulty, " +
                        "question_text, option_a, option_b, " +
                        "option_c, option_d, correct_answer) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
                ) {

                    setQuestionValues(
                            statement,
                            request
                    );

                    int rows =
                            statement.executeUpdate();

                    System.out.println(
                            "ADMIN ADD ROWS = "
                            + rows
                    );

                    if (rows > 0) {

                        response.sendRedirect(
                                request.getContextPath()
                                + "/admin?status=added"
                        );

                        return;
                    }
                }
            }


            // ==========================================
            // UPDATE
            // ==========================================

            else if ("update".equals(action)) {

                String sql =
                        "UPDATE questions SET " +
                        "subject = ?, " +
                        "topic = ?, " +
                        "difficulty = ?, " +
                        "question_text = ?, " +
                        "option_a = ?, " +
                        "option_b = ?, " +
                        "option_c = ?, " +
                        "option_d = ?, " +
                        "correct_answer = ? " +
                        "WHERE id = ?";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
                ) {

                    setQuestionValues(
                            statement,
                            request
                    );

                    statement.setInt(
                            10,
                            Integer.parseInt(
                                    request.getParameter(
                                            "id"
                                    )
                            )
                    );

                    int rows =
                            statement.executeUpdate();

                    System.out.println(
                            "ADMIN UPDATE ROWS = "
                            + rows
                    );

                    if (rows > 0) {

                        response.sendRedirect(
                                request.getContextPath()
                                + "/admin?status=updated"
                        );

                        return;
                    }
                }
            }


            // ==========================================
            // DELETE
            // ==========================================

            else if ("delete".equals(action)) {

                String sql =
                        "DELETE FROM questions " +
                        "WHERE id = ?";

                try (
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
                ) {

                    statement.setInt(
                            1,
                            Integer.parseInt(
                                    request.getParameter(
                                            "id"
                                    )
                            )
                    );

                    int rows =
                            statement.executeUpdate();

                    System.out.println(
                            "ADMIN DELETE ROWS = "
                            + rows
                    );

                    if (rows > 0) {

                        response.sendRedirect(
                                request.getContextPath()
                                + "/admin?status=deleted"
                        );

                        return;
                    }
                }
            }


            // ==========================================
            // NO ROW CHANGED
            // ==========================================

            response.sendRedirect(
                    request.getContextPath()
                    + "/admin?status=error"
            );

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "ADMIN DATABASE ERROR"
            );

            System.out.println(
                    e.getClass().getName()
            );

            System.out.println(
                    e.getMessage()
            );

            System.out.println(
                    "================================="
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/admin?status=error"
            );
        }
    }


    // ==================================================
    // SET VALUES
    // ==================================================

    private void setQuestionValues(
            PreparedStatement statement,
            HttpServletRequest request)
            throws Exception {

        statement.setString(
                1,
                request.getParameter(
                        "subject"
                )
        );

        statement.setString(
                2,
                request.getParameter(
                        "topic"
                )
        );

        statement.setString(
                3,
                request.getParameter(
                        "difficulty"
                )
        );

        statement.setString(
                4,
                request.getParameter(
                        "question_text"
                )
        );

        statement.setString(
                5,
                request.getParameter(
                        "option_a"
                )
        );

        statement.setString(
                6,
                request.getParameter(
                        "option_b"
                )
        );

        statement.setString(
                7,
                request.getParameter(
                        "option_c"
                )
        );

        statement.setString(
                8,
                request.getParameter(
                        "option_d"
                )
        );

        statement.setString(
                9,
                request.getParameter(
                        "correct_answer"
                )
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
}