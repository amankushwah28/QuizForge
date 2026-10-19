package com.quizforge;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/profile-image")
public class ProfileImageServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String email = (String) request.getSession()
                .getAttribute("userEmail");

        if (email == null || email.isBlank()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        String sql = """
                SELECT profile_image, profile_image_type
                FROM users
                WHERE email = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    byte[] imageData =
                            resultSet.getBytes("profile_image");

                    String imageType =
                            resultSet.getString("profile_image_type");

                    if (imageData != null && imageData.length > 0) {

                        response.setContentType(
                                imageType != null
                                        ? imageType
                                        : "image/jpeg"
                        );

                        try (OutputStream outputStream =
                                     response.getOutputStream()) {

                            outputStream.write(imageData);
                        }

                        return;
                    }
                }

                // No uploaded image: don't return 404
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load profile image"
            );
        }
    }
}