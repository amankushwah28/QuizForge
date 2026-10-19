package com.quizforge;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fullName =
                request.getParameter("fullName");

        String username =
                request.getParameter("username");

        String email =
                request.getParameter("email");

        String password =
                request.getParameter("password");


        // ==========================================
        // CHECK REQUIRED FIELDS
        // ==========================================

        if (fullName == null ||
                username == null ||
                email == null ||
                password == null ||
                fullName.trim().isEmpty() ||
                username.trim().isEmpty() ||
                email.trim().isEmpty() ||
                password.trim().isEmpty()) {

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h1>All fields are required!</h1>"
            );

            return;
        }


        // Remove unnecessary spaces
        fullName = fullName.trim();
        username = username.trim();
        email = email.trim().toLowerCase();


        // ==========================================
        // GMAIL VALIDATION
        // ==========================================

        if (!email.matches(
                "^[A-Za-z0-9._%+-]+@gmail\\.com$")) {

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h1>Please use a valid Gmail address!</h1>"
            );

            return;
        }


        // ==========================================
        // USERNAME VALIDATION
        // ==========================================

       if (!username.matches(
        "^[A-Za-z0-9._]{3,50}$")) {

            response.setContentType("text/html");

            response.getWriter().println(
        "<h1>Username can contain only letters, numbers, underscore, and dot!</h1>"
);
            return;
        }


        UserDAO userDAO = new UserDAO();


        // ==========================================
        // CHECK USERNAME AVAILABILITY
        // ==========================================

        if (!userDAO.isUsernameAvailable(username)) {

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h1>Username already exists! Please choose another username.</h1>"
            );

            return;
        }


        // ==========================================
        // REGISTER USER
        // ==========================================

        boolean registered =
                userDAO.registerUser(
                        fullName,
                        username,
                        email,
                        password
                );


        response.setContentType("text/html");


        if (registered) {

            response.getWriter().println(
                    "<h1>Registration Successful!</h1>"
            );

        } else {

            response.getWriter().println(
                    "<h1>Registration Failed!</h1>"
            );
        }
    }
}