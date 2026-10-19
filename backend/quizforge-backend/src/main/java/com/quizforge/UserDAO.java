package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // ==========================================
    // REGISTER USER
    // ==========================================

    public boolean registerUser(
            String fullName,
            String username,
            String email,
            String password) {

        String sql =
                "INSERT INTO users " +
                "(full_name, username, email, password) " +
                "VALUES (?, ?, ?, ?)";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, fullName);
            statement.setString(2, username);
            statement.setString(3, email);
            statement.setString(4, password);

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "REGISTRATION ERROR: " + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // CHECK USERNAME
    // ==========================================

    public boolean isUsernameAvailable(String username) {

        String sql =
                "SELECT user_id FROM users WHERE username = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            ResultSet resultSet =
                    statement.executeQuery();

            return !resultSet.next();

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // LOGIN USER
    // ==========================================

    public boolean loginUser(
            String email,
            String password) {

        String sql =
                "SELECT * FROM users " +
                "WHERE email = ? AND password = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet =
                    statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET FULL NAME
    // ==========================================

    public String getFullName(String email) {

        String sql =
                "SELECT full_name FROM users WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getString("full_name");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return "User";
    }


    // ==========================================
    // GET USERNAME
    // ==========================================

    public String getUsername(String email) {

        String sql =
                "SELECT username FROM users WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getString("username");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return "";
    }


    // ==========================================
    // UPDATE USERNAME
    // ==========================================

    public boolean updateUsername(
            String email,
            String newUsername) {

        String sql =
                "UPDATE users " +
                "SET username = ? " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, newUsername);
            statement.setString(2, email);

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "USERNAME UPDATE ERROR: " + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET PROFILE AVATAR
    // ==========================================

    public String getProfileAvatar(String email) {

        String sql =
                "SELECT profile_avatar " +
                "FROM users " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String avatar =
                        resultSet.getString("profile_avatar");

                if (avatar != null &&
                    !avatar.trim().isEmpty()) {

                    return avatar;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "GET AVATAR ERROR: " + e.getMessage()
            );

            e.printStackTrace();
        }

        return "default";
    }


    // ==========================================
    // UPDATE PROFILE AVATAR
    // ==========================================

    public boolean updateProfileAvatar(
            String email,
            String avatarName) {

        String sql =
                "UPDATE users " +
                "SET profile_avatar = ?, " +
                "profile_image = NULL, " +
                "profile_image_type = NULL " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, avatarName);
            statement.setString(2, email);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "AVATAR UPDATE ERROR: " + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // UPDATE PROFILE IMAGE
    // ==========================================

    public boolean updateProfileImage(
            String email,
            byte[] imageData,
            String imageType) {

        String sql =
                "UPDATE users " +
                "SET profile_image = ?, " +
                "profile_image_type = ?, " +
                "profile_avatar = 'uploaded' " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setBytes(1, imageData);
            statement.setString(2, imageType);
            statement.setString(3, email);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "PROFILE IMAGE UPDATE ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET PROFILE IMAGE
    // ==========================================

    public byte[] getProfileImage(String email) {

        String sql =
                "SELECT profile_image " +
                "FROM users " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getBytes(
                        "profile_image"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "GET PROFILE IMAGE ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return null;
    }


    // ==========================================
    // GET PROFILE IMAGE TYPE
    // ==========================================

    public String getProfileImageType(String email) {

        String sql =
                "SELECT profile_image_type " +
                "FROM users " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getString(
                        "profile_image_type"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "GET IMAGE TYPE ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return null;
    }


    // ==========================================
    // CHECK UPLOADED IMAGE
    // ==========================================

    public boolean hasUploadedImage(String email) {

        String sql =
                "SELECT profile_image " +
                "FROM users " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                return resultSet.getBytes(
                        "profile_image"
                ) != null;
            }

        } catch (SQLException e) {

            System.out.println(
                    "CHECK PROFILE IMAGE ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }

        return false;
    }


    // ==========================================
    // REMOVE PROFILE IMAGE
    // ==========================================

    public boolean removeProfileImage(String email) {

        String sql =
                "UPDATE users " +
                "SET profile_image = NULL, " +
                "profile_image_type = NULL, " +
                "profile_avatar = 'default' " +
                "WHERE email = ?";

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println(
                    "REMOVE PROFILE IMAGE ERROR: "
                    + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }
}