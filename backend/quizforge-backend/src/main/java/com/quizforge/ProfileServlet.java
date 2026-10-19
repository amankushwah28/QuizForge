package com.quizforge;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profile")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class ProfileServlet extends HttpServlet {

    private static final String[] AVATARS = {
            "default",
            "avatar1",
            "avatar2",
            "avatar3",
            "avatar4",
            "avatar5"
    };

    private static final String[] AVATAR_EMOJIS = {
            "🙂",
            "🧑‍💻",
            "👨‍🚀",
            "🦸‍♂️",
            "🧙‍♂️",
            "🥷"
    };

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        showProfile(request, response, null, false);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );
            return;
        }

        String oldEmail =
                String.valueOf(session.getAttribute("userEmail"));

        String fullName =
                clean(request.getParameter("fullName"));

        String username =
                clean(request.getParameter("username"));

        String newEmail =
                clean(request.getParameter("email"));

        String avatar =
                clean(request.getParameter("avatar"));

        boolean removeImage =
                "on".equalsIgnoreCase(
                        request.getParameter("removeImage")
                );

        Part imagePart =
                request.getPart("profileImage");

        /*
         * -----------------------------
         * VALIDATION
         * -----------------------------
         */

        if (fullName.length() < 2 ||
                fullName.length() > 100) {

            showProfile(
                    request,
                    response,
                    "Full name must contain 2–100 characters.",
                    true
            );
            return;
        }

        if (!username.matches(
                "^[A-Za-z0-9._]{3,50}$"
        )) {

            showProfile(
                    request,
                    response,
                    "Username must contain 3–50 letters, numbers, dots, or underscores.",
                    true
            );
            return;
        }

        if (newEmail.isBlank() ||
                newEmail.length() > 150 ||
                (
                        !newEmail.equalsIgnoreCase(oldEmail)
                                &&
                        !newEmail.matches(
                                "^[A-Za-z0-9._%+-]+@gmail\\.com$"
                        )
                )) {

            showProfile(
                    request,
                    response,
                    "Please enter a valid Gmail address.",
                    true
            );
            return;
        }

        /*
         * avatar can also be "uploaded".
         * This means keep the existing uploaded image.
         */
        if (!isAllowedAvatar(avatar)) {
            avatar = "default";
        }

        /*
         * -----------------------------
         * IMAGE PROCESSING
         * -----------------------------
         */

        byte[] imageData = null;
        String imageType = null;

        if (imagePart != null &&
                imagePart.getSize() > 0) {

            imageType = imagePart.getContentType();

            if (imageType == null ||
                    !imageType
                            .toLowerCase()
                            .startsWith("image/")) {

                showProfile(
                        request,
                        response,
                        "Please select a valid image file.",
                        true
                );
                return;
            }

            imageData =
                    imagePart
                            .getInputStream()
                            .readAllBytes();
        }

        /*
         * -----------------------------
         * DUPLICATE CHECKS
         * -----------------------------
         */

        String checkUsername =
                "SELECT COUNT(*) FROM users " +
                "WHERE username = ? AND email <> ?";

        String checkEmail =
                "SELECT COUNT(*) FROM users " +
                "WHERE email = ? AND email <> ?";

        /*
         * -----------------------------
         * UPDATE QUERY
         * -----------------------------
         *
         * Three situations:
         *
         * 1. New image uploaded
         *    -> replace old image
         *
         * 2. Remove image selected
         *    -> delete image
         *
         * 3. No new image and no remove
         *    -> PRESERVE existing image
         *
         */

        String sql;

        if (imageData != null) {

            sql =
                    "UPDATE users SET " +
                    "full_name = ?, " +
                    "username = ?, " +
                    "email = ?, " +
                    "profile_avatar = 'uploaded', " +
                    "profile_image = ?, " +
                    "profile_image_type = ? " +
                    "WHERE email = ?";

        } else if (removeImage) {

            sql =
                    "UPDATE users SET " +
                    "full_name = ?, " +
                    "username = ?, " +
                    "email = ?, " +
                    "profile_avatar = ?, " +
                    "profile_image = NULL, " +
                    "profile_image_type = NULL " +
                    "WHERE email = ?";

        } else if ("uploaded".equals(avatar)) {

            /*
             * Keep existing uploaded image.
             */
            sql =
                    "UPDATE users SET " +
                    "full_name = ?, " +
                    "username = ?, " +
                    "email = ? " +
                    "WHERE email = ?";

        } else {

            /*
             * User selected a built-in avatar.
             * Therefore intentionally remove uploaded image.
             */
            sql =
                    "UPDATE users SET " +
                    "full_name = ?, " +
                    "username = ?, " +
                    "email = ?, " +
                    "profile_avatar = ?, " +
                    "profile_image = NULL, " +
                    "profile_image_type = NULL " +
                    "WHERE email = ?";
        }

        /*
         * -----------------------------
         * DATABASE UPDATE
         * -----------------------------
         */

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                if (exists(
                        connection,
                        checkUsername,
                        username,
                        oldEmail
                )) {

                    connection.rollback();

                    showProfile(
                            request,
                            response,
                            "That username is already taken.",
                            true
                    );

                    return;
                }

                if (exists(
                        connection,
                        checkEmail,
                        newEmail,
                        oldEmail
                )) {

                    connection.rollback();

                    showProfile(
                            request,
                            response,
                            "That email address is already registered.",
                            true
                    );

                    return;
                }

                try (PreparedStatement ps =
                             connection.prepareStatement(sql)) {

                    ps.setString(1, fullName);
                    ps.setString(2, username);
                    ps.setString(3, newEmail);

                    int index = 4;

                    if (imageData != null) {

                        ps.setBytes(
                                index++,
                                imageData
                        );

                        ps.setString(
                                index++,
                                imageType
                        );

                    } else if (removeImage) {

                        ps.setString(
                                index++,
                                avatar
                        );

                    } else if ("uploaded".equals(avatar)) {

                        /*
                         * No avatar parameter required here.
                         * Existing uploaded image is preserved.
                         */

                    } else {

                        ps.setString(
                                index++,
                                avatar
                        );
                    }

                    ps.setString(
                            index,
                            oldEmail
                    );

                    ps.executeUpdate();
                }

                connection.commit();

                /*
                 * Update logged-in email if changed.
                 */
                session.setAttribute(
                        "userEmail",
                        newEmail
                );

                response.sendRedirect(
                        request.getContextPath()
                                + "/profile?success=1"
                );

            } catch (Exception e) {

                connection.rollback();

                e.printStackTrace();

                showProfile(
                        request,
                        response,
                        "Something went wrong while saving your profile.",
                        true
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            showProfile(
                    request,
                    response,
                    "Database connection failed.",
                    true
            );
        }
    }

    /*
     * =========================================================
     * PROFILE PAGE
     * =========================================================
     */

    private void showProfile(
            HttpServletRequest request,
            HttpServletResponse response,
            String message,
            boolean editMode
    ) throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );

            return;
        }

        String email =
                String.valueOf(
                        session.getAttribute("userEmail")
                );

        UserDAO userDAO =
                new UserDAO();

        DashboardDAO dashboardDAO =
                new DashboardDAO();

        String fullName =
                safe(
                        userDAO.getFullName(email),
                        "QuizForge User"
                );

        String username =
                safe(
                        userDAO.getUsername(email),
                        ""
                );

        String avatar =
                safe(
                        userDAO.getProfileAvatar(email),
                        "default"
                );

        boolean uploaded =
                userDAO.hasUploadedImage(email);

        String context =
                request.getContextPath();

        if ("1".equals(
                request.getParameter("success")
        )) {

            message =
                    "Profile updated successfully.";

            editMode = false;
        }

        StringBuilder html =
                new StringBuilder();

        /*
         * =====================================================
         * HTML HEAD
         * =====================================================
         */

        html.append(
                "<!DOCTYPE html>"
        );

        html.append(
                "<html lang='en'>"
        );

        html.append(
                "<head>"
        );

        html.append(
                "<meta charset='UTF-8'>"
        );

        html.append(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
        );

        html.append(
                "<title>QuizForge | Profile</title>"
        );

        html.append(
                "<link rel='stylesheet' href='"
        )
        .append(context)
        .append(
                "/CSS/style.css'>"
        );

        html.append(
                "</head>"
        );

        /*
         * =====================================================
         * BODY
         * =====================================================
         */

        html.append(
                "<body class='profile-page'>"
        );

        html.append(
                "<main class='profile-wrapper'>"
        );

        html.append(
                "<section class='profile-card'>"
        );

        html.append(
                "<span class='profile-badge'>QUIZFORGE</span>"
        );

        /*
         * =====================================================
         * PROFILE IMAGE / AVATAR
         * =====================================================
         */

        html.append(
                "<div class='profile-avatar-wrapper'>"
        );

        html.append(
                "<div class='profile-icon'>"
        );

        if (uploaded) {

            html.append(
                    "<img src='"
            )
            .append(context)
            .append(
                    "/profile-image?time="
            )
            .append(
                    System.currentTimeMillis()
            )
            .append(
                    "' alt='Profile Image'>"
            );

        } else {

            html.append(
                    "<span class='built-in-avatar'>"
            )
            .append(
                    avatarEmoji(avatar)
            )
            .append(
                    "</span>"
            );
        }

        html.append(
                "</div>"
        );

        html.append(
                "</div>"
        );

        /*
         * =====================================================
         * NORMAL PROFILE VIEW
         * =====================================================
         */

        html.append(
                "<div id='profileView' " +
                "style='display:"
        )
        .append(
                editMode ? "none" : "block"
        )
        .append(
                ";'>"
        );

        html.append(
                "<div class='profile-name-row'>"
        );

        html.append(
                "<h1>"
        )
        .append(
                escape(fullName)
        )
        .append(
                "</h1>"
        );

        /*
         * Pencil only.
         *
         * Check and cancel buttons are NOT visible
         * in normal view.
         */
        html.append(
                "<button type='button' " +
                "class='profile-pencil-button' " +
                "onclick='openEditProfile()' " +
                "title='Edit Profile' " +
                "aria-label='Edit Profile'>"
        );

        html.append(
                "✎"
        );

        html.append(
                "</button>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "<p class='profile-subtitle'>" +
                "Manage your QuizForge account" +
                "</p>"
        );

        /*
         * EMAIL
         */

        html.append(
                "<div class='profile-info'>"
        );

        html.append(
                "<span class='profile-label'>EMAIL</span>"
        );

        html.append(
                "<strong>"
        )
        .append(
                escape(email)
        )
        .append(
                "</strong>"
        );

        html.append(
                "</div>"
        );

        /*
         * USERNAME
         */

        html.append(
                "<div class='profile-info'>"
        );

        html.append(
                "<span class='profile-label'>USERNAME</span>"
        );

        html.append(
                "<strong>@"
        )
        .append(
                escape(username)
        )
        .append(
                "</strong>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "</div>"
        );

        /*
         * =====================================================
         * EDIT PROFILE VIEW
         * =====================================================
         */

        html.append(
                "<div id='profileEdit' " +
                "style='display:"
        )
        .append(
                editMode ? "block" : "none"
        )
        .append(
                ";'>"
        );

        html.append(
                "<h1>Edit Profile</h1>"
        );

        html.append(
                "<p class='profile-subtitle'>" +
                "Update your personal information" +
                "</p>"
        );

        html.append(
                "<form action='"
        )
        .append(context)
        .append(
                "/profile' " +
                "method='POST' " +
                "enctype='multipart/form-data' " +
                "class='profile-edit-form'>"
        );

        html.append(
                "<input type='hidden' " +
                "name='action' " +
                "value='saveProfile'>"
        );

        /*
         * FULL NAME
         */

        html.append(
                "<label>Full Name</label>"
        );

        html.append(
                "<input type='text' " +
                "name='fullName' value='"
        )
        .append(
                escape(fullName)
        )
        .append(
                "' required>"
        );

        /*
         * USERNAME
         */

        html.append(
                "<label>Username</label>"
        );

        html.append(
                "<input type='text' " +
                "name='username' value='"
        )
        .append(
                escape(username)
        )
        .append(
                "' required>"
        );

        /*
         * EMAIL
         */

        html.append(
                "<label>Email Address</label>"
        );

        html.append(
                "<input type='email' " +
                "name='email' value='"
        )
        .append(
                escape(email)
        )
        .append(
                "' required>"
        );

        /*
         * =====================================================
         * AVATAR SELECTION
         * =====================================================
         */

        html.append(
                "<label>Choose Cartoon Avatar</label>"
        );

        html.append(
                "<div class='avatar-selection'>"
        );

        /*
         * Existing uploaded image option.
         */
        if (uploaded) {

            html.append(
                    "<label class='avatar-option " +
                    "avatar-uploaded-option'>"
            );

            html.append(
                    "<input type='radio' " +
                    "name='avatar' " +
                    "value='uploaded' checked>"
            );

            html.append(
                    "<span>📷</span>"
            );

            html.append(
                    "</label>"
            );
        }

        /*
         * Built-in avatars.
         */

        for (int i = 0;
             i < AVATARS.length;
             i++) {

            boolean checked =
                    !uploaded &&
                    avatar.equals(
                            AVATARS[i]
                    );

            html.append(
                    "<label class='avatar-option'>"
            );

            html.append(
                    "<input type='radio' " +
                    "name='avatar' value='"
            )
            .append(
                    AVATARS[i]
            )
            .append(
                    "'"
            );

            if (checked) {
                html.append(" checked");
            }

            html.append(
                    ">"
            );

            html.append(
                    "<span>"
            )
            .append(
                    AVATAR_EMOJIS[i]
            )
            .append(
                    "</span>"
            );

            html.append(
                    "</label>"
            );
        }

        html.append(
                "</div>"
        );

        /*
         * =====================================================
         * PROFILE IMAGE UPLOAD
         * =====================================================
         */

        html.append(
                "<label>Profile Image</label>"
        );

        html.append(
                "<input type='file' " +
                "name='profileImage' " +
                "accept='image/*'>"
        );

        /*
         * REMOVE IMAGE
         */

        html.append(
                "<label class='profile-remove-image'>"
        );

        html.append(
                "<input type='checkbox' " +
                "name='removeImage'>"
        );

        html.append(
                " Remove current image"
        );

        html.append(
                "</label>"
        );

        /*
         * =====================================================
         * FORM ACTIONS
         * =====================================================
         */

        html.append(
                "<div class='profile-form-actions'>"
        );

        /*
         * SAVE
         */

        html.append(
                "<button " +
                "class='profile-save-button' " +
                "type='submit'>"
        );

        html.append(
                "Save Changes"
        );

        html.append(
                "</button>"
        );

        /*
         * CANCEL
         */

        html.append(
                "<button " +
                "class='profile-cancel-button' " +
                "type='button' " +
                "onclick='cancelEditProfile()'>"
        );

        html.append(
                "Cancel"
        );

        html.append(
                "</button>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "</form>"
        );

        html.append(
                "</div>"
        );

        /*
         * =====================================================
         * MESSAGE
         * =====================================================
         */

        if (message != null) {

            html.append(
                    "<p class='profile-message'>"
            )
            .append(
                    escape(message)
            )
            .append(
                    "</p>"
            );
        }

        /*
         * =====================================================
         * PROFILE STATISTICS
         * =====================================================
         */

        html.append(
                "<div class='profile-stats'>"
        );

        /*
         * TOTAL QUIZZES
         */

        html.append(
                "<div class='profile-stat'>"
        );

        html.append(
                "<span class='profile-stat-value'>"
        )
        .append(
                dashboardDAO.getTotalQuizzes(email)
        )
        .append(
                "</span>"
        );

        html.append(
                "<span class='profile-stat-label'>" +
                "Quizzes Taken" +
                "</span>"
        );

        html.append(
                "</div>"
        );

        /*
         * AVERAGE SCORE
         */

        html.append(
                "<div class='profile-stat'>"
        );

        html.append(
                "<span class='profile-stat-value'>"
        )
        .append(
                String.format(
                        "%.1f%%",
                        dashboardDAO.getAverageScore(email)
                )
        )
        .append(
                "</span>"
        );

        html.append(
                "<span class='profile-stat-label'>" +
                "Average Score" +
                "</span>"
        );

        html.append(
                "</div>"
        );

        /*
         * BEST SCORE
         */

        html.append(
                "<div class='profile-stat'>"
        );

        html.append(
                "<span class='profile-stat-value'>"
        )
        .append(
                String.format(
                        "%.1f%%",
                        dashboardDAO.getBestScore(email)
                )
        )
        .append(
                "</span>"
        );

        html.append(
                "<span class='profile-stat-label'>" +
                "Best Score" +
                "</span>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "</div>"
        );

        /*
         * =====================================================
         * BOTTOM ACTIONS
         * =====================================================
         */

        html.append(
                "<div class='profile-actions'>"
        );

        html.append(
                "<a class='profile-action-button primary' " +
                "href='"
        )
        .append(context)
        .append(
                "/dashboard'>"
        );

        html.append(
                "← Dashboard"
        );

        html.append(
                "</a>"
        );

        html.append(
                "<a class='profile-action-button danger' " +
                "href='"
        )
        .append(context)
        .append(
                "/logout'>"
        );

        html.append(
                "Logout"
        );

        html.append(
                "</a>"
        );

        html.append(
                "</div>"
        );

        html.append(
                "</section>"
        );

        html.append(
                "</main>"
        );

        /*
         * =====================================================
         * JAVASCRIPT
         * =====================================================
         */

        html.append(
                "<script>"
        );

        html.append(
                "function openEditProfile() {"
        );

        html.append(
                "document.getElementById('profileView')" +
                ".style.display='none';"
        );

        html.append(
                "document.getElementById('profileEdit')" +
                ".style.display='block';"
        );

        html.append(
                "}"
        );

        html.append(
                "function cancelEditProfile() {"
        );

        html.append(
                "document.getElementById('profileEdit')" +
                ".style.display='none';"
        );

        html.append(
                "document.getElementById('profileView')" +
                ".style.display='block';"
        );

        html.append(
                "}"
        );

        html.append(
                "</script>"
        );

        html.append(
                "</body></html>"
        );

        /*
         * =====================================================
         * RESPONSE
         * =====================================================
         */

        response.setContentType(
                "text/html;charset=UTF-8"
        );

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

        response.getWriter().print(
                html
        );
    }

    /*
     * =========================================================
     * CHECK DUPLICATES
     * =========================================================
     */

    private boolean exists(
            Connection connection,
            String sql,
            String firstValue,
            String secondValue
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    firstValue
            );

            statement.setString(
                    2,
                    secondValue
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt(1) > 0;
            }
        }
    }

    /*
     * =========================================================
     * ALLOWED AVATARS
     * =========================================================
     */

    private boolean isAllowedAvatar(
            String value
    ) {

        if ("uploaded".equals(value)) {
            return true;
        }

        for (String avatar : AVATARS) {

            if (avatar.equals(value)) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================================================
     * AVATAR EMOJI
     * =========================================================
     */

    private String avatarEmoji(
            String avatar
    ) {

        for (int i = 0;
             i < AVATARS.length;
             i++) {

            if (AVATARS[i].equals(avatar)) {

                return AVATAR_EMOJIS[i];
            }
        }

        return "🙂";
    }

    /*
     * =========================================================
     * CLEAN INPUT
     * =========================================================
     */

    private String clean(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    /*
     * =========================================================
     * SAFE VALUE
     * =========================================================
     */

    private String safe(
            String value,
            String fallback
    ) {

        return value == null ||
                value.isBlank()
                ? fallback
                : value;
    }

    /*
     * =========================================================
     * HTML ESCAPE
     * =========================================================
     */

    private String escape(
            String value
    ) {

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