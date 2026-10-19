package com.quizforge;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();

        boolean loginSuccessful =
                userDAO.loginUser(email, password);

        if (loginSuccessful) {

            HttpSession session = request.getSession();

            session.setAttribute("userEmail", email);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } else {

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h1>Invalid email or password!</h1>"
            );
        }
    }
}