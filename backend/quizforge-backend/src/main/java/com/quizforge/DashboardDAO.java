package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    // ==========================================
    // GET TOTAL QUIZZES TAKEN
    // ==========================================

    public int getTotalQuizzes(String email) {

        String sql =
                "SELECT COUNT(*) " +
                "FROM quiz_results " +
                "WHERE user_email = ?";

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

                return resultSet.getInt(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }


    // ==========================================
    // GET AVERAGE PERCENTAGE
    // ==========================================

    public double getAverageScore(String email) {

        String sql =
                "SELECT AVG(percentage) " +
                "FROM quiz_results " +
                "WHERE user_email = ?";

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

                return resultSet.getDouble(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }


    // ==========================================
    // GET BEST SCORE
    // ==========================================

    public double getBestScore(String email) {

        String sql =
                "SELECT MAX(percentage) " +
                "FROM quiz_results " +
                "WHERE user_email = ?";

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

                return resultSet.getDouble(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }


    // ==========================================
    // GET LAST QUIZ SUBJECT
    // ==========================================

    public String getLastQuizSubject(String email) {

        String sql =
                "SELECT subject " +
                "FROM quiz_results " +
                "WHERE user_email = ? " +
                "ORDER BY quiz_date DESC " +
                "LIMIT 1";

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

                return resultSet.getString("subject");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "No quiz yet";
    }
}