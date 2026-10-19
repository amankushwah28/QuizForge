package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QuizHistoryDAO {

    // ==========================================
    // SAVE QUIZ RESULT
    // ==========================================

    public boolean saveHistory(
            String userEmail,
            String subject,
            int score,
            int totalQuestions,
            double percentage) {

        String sql =
                "INSERT INTO quiz_history " +
                "(user_email, subject, score, total_questions, percentage) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userEmail);
            statement.setString(2, subject);
            statement.setInt(3, score);
            statement.setInt(4, totalQuestions);
            statement.setDouble(5, percentage);

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    // ==========================================
    // GET QUIZ HISTORY FOR USER
    // ==========================================

    public List<QuizHistory> getHistory(
            String userEmail) {

        List<QuizHistory> history =
                new ArrayList<>();

        String sql =
                "SELECT * FROM quiz_history " +
                "WHERE user_email = ? " +
                "ORDER BY quiz_date DESC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, userEmail);

            ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                QuizHistory quizHistory =
                        new QuizHistory();

                quizHistory.setId(
                        resultSet.getInt("id"));

                quizHistory.setUserEmail(
                        resultSet.getString("user_email"));

                quizHistory.setSubject(
                        resultSet.getString("subject"));

                quizHistory.setScore(
                        resultSet.getInt("score"));

                quizHistory.setTotalQuestions(
                        resultSet.getInt("total_questions"));

                quizHistory.setPercentage(
                        resultSet.getDouble("percentage"));

                quizHistory.setQuizDate(
                        resultSet.getTimestamp("quiz_date"));

                history.add(quizHistory);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return history;
    }
}