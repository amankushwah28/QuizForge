package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Prevent the browser from caching the dashboard.
         * This forces a fresh request when the page is opened.
         */
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        HttpSession session =
                request.getSession(false);

        String contextPath =
                request.getContextPath();

        /*
         * Check whether the user is still logged in.
         */
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

        DashboardDAO dashboardDAO =
                new DashboardDAO();

        int totalQuizzes =
                dashboardDAO.getTotalQuizzes(email);

        double averageScore =
                dashboardDAO.getAverageScore(email);

        double bestScore =
                dashboardDAO.getBestScore(email);

        String lastQuizSubject =
                dashboardDAO.getLastQuizSubject(email);

        String userRole =
                getUserRole(email);

        request.setAttribute(
                "totalQuizzes",
                totalQuizzes
        );

        request.setAttribute(
                "averageScore",
                averageScore
        );

        request.setAttribute(
                "bestScore",
                bestScore
        );

        request.setAttribute(
                "lastQuizSubject",
                lastQuizSubject
        );

        request.setAttribute(
                "userRole",
                userRole
        );

        request.getRequestDispatcher(
                "/dashboard.jsp"
        ).forward(
                request,
                response
        );
    }

    private String getUserRole(
            String email) {

        String sql =
                "SELECT role " +
                "FROM users " +
                "WHERE email = ?";

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

                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "USER";
    }
}