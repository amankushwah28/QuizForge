package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");

        String sql =
                "SELECT role " +
                "FROM users " +
                "WHERE email = ? " +
                "AND password = ?";

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

            statement.setString(
                    2,
                    password
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    String role =
                            resultSet.getString("role");

                    if ("ADMIN".equalsIgnoreCase(role)) {

                        HttpSession session =
                                request.getSession();

                        /*
                         * Store both values.
                         *
                         * adminAuthenticated is used
                         * for Admin pages.
                         *
                         * userEmail is used by the
                         * normal Dashboard/Profile/
                         * History pages.
                         */

                        session.setAttribute(
                                "adminAuthenticated",
                                true
                        );

                        session.setAttribute(
                                "adminEmail",
                                email
                        );

                        session.setAttribute(
                                "userEmail",
                                email
                        );

                        response.sendRedirect(
                                request.getContextPath()
                                + "/admin"
                        );

                        return;
                    }
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        response.sendRedirect(
                request.getContextPath()
                + "/admin-login.html?error=true"
        );
    }
}