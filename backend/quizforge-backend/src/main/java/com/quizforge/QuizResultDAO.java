package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class QuizResultDAO {

    // ==================================================
    // SAVE RESULT
    // ==================================================

    public void saveResult(
            String email,
            String subject,
            int score,
            int totalQuestions) {

        saveResult(
                email,
                subject,
                score,
                totalQuestions,
                null
        );
    }


    // ==================================================
    // SAVE RESULT WITH ATTEMPT ID
    // ==================================================

    public void saveResult(
            String email,
            String subject,
            int score,
            int totalQuestions,
            String attemptId) {

        double percentage = 0;

        if (totalQuestions > 0) {

            percentage =
                    ((double) score / totalQuestions)
                    * 100;
        }


        String sql =
                "INSERT INTO quiz_results " +
                "(user_email, subject, score, " +
                "total_questions, percentage, attempt_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";


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
                    subject
            );

            statement.setInt(
                    3,
                    score
            );

            statement.setInt(
                    4,
                    totalQuestions
            );

            statement.setDouble(
                    5,
                    percentage
            );

            statement.setString(
                    6,
                    attemptId
            );


            statement.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}