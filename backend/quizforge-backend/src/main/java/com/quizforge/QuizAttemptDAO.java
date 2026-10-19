package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public class QuizAttemptDAO {

    // ==================================================
    // SAVE ALL QUESTIONS FROM ONE QUIZ ATTEMPT
    // ==================================================

    public void saveAttemptDetails(
            String attemptId,
            String email,
            List<Question> questions,
            HttpServletRequest request)
            throws Exception {

        String sql =
                "INSERT INTO quiz_attempt_details " +
                "(attempt_id, user_email, subject, " +
                "question_number, question_id, question_text, " +
                "option_a, option_b, option_c, option_d, " +
                "user_answer, correct_answer, " +
                "user_answer_letter, correct_answer_letter, " +
                "is_correct) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            int questionNumber = 1;


            for (Question question : questions) {

                // ======================================
                // GET USER ANSWER
                // ======================================

                String userAnswer =
                        request.getParameter(
                                "q" + question.getId()
                        );


                // ======================================
                // FIND CORRECT ANSWER
                // ======================================

                String correctLetter =
                        getCorrectAnswerLetter(
                                question
                        );


                String correctText =
                        getCorrectAnswerText(
                                question
                        );


                String userLetter =
                        getUserAnswerLetter(
                                question,
                                userAnswer
                        );


                String userText =
                        getUserAnswerText(
                                question,
                                userAnswer
                        );


                boolean correct =
                        isCorrectAnswer(
                                question,
                                userAnswer
                        );


                // ======================================
                // SET DATABASE VALUES
                // ======================================

                statement.setString(
                        1,
                        attemptId
                );

                statement.setString(
                        2,
                        email
                );

                statement.setString(
                        3,
                        question.getSubject()
                );

                statement.setInt(
                        4,
                        questionNumber
                );

                statement.setInt(
                        5,
                        question.getId()
                );

                statement.setString(
                        6,
                        question.getQuestionText()
                );

                statement.setString(
                        7,
                        question.getOptionA()
                );

                statement.setString(
                        8,
                        question.getOptionB()
                );

                statement.setString(
                        9,
                        question.getOptionC()
                );

                statement.setString(
                        10,
                        question.getOptionD()
                );

                statement.setString(
                        11,
                        userText
                );

                statement.setString(
                        12,
                        correctText
                );

                statement.setString(
                        13,
                        userLetter
                );

                statement.setString(
                        14,
                        correctLetter
                );

                statement.setBoolean(
                        15,
                        correct
                );


                statement.addBatch();

                questionNumber++;
            }


            statement.executeBatch();
        }
    }


    // ==================================================
    // CHECK ANSWER
    // ==================================================

    private boolean isCorrectAnswer(
            Question question,
            String userAnswer) {

        if (userAnswer == null ||
            userAnswer.trim().isEmpty()) {

            return false;
        }


        String userLetter =
                getUserAnswerLetter(
                        question,
                        userAnswer
                );


        String correctLetter =
                getCorrectAnswerLetter(
                        question
                );


        // ==========================================
        // LETTER COMPARISON
        // ==========================================

        if (userLetter != null &&
            correctLetter != null) {

            return userLetter.equalsIgnoreCase(
                    correctLetter
            );
        }


        // ==========================================
        // TEXT COMPARISON
        // ==========================================

        String correctText =
                getCorrectAnswerText(
                        question
                );


        if (correctText == null) {

            return false;
        }


        return normalizeText(
                userAnswer
        ).equalsIgnoreCase(
                normalizeText(
                        correctText
                )
        );
    }


    // ==================================================
    // GET USER ANSWER LETTER
    // ==================================================

    private String getUserAnswerLetter(
            Question question,
            String userAnswer) {

        if (userAnswer == null ||
            userAnswer.trim().isEmpty()) {

            return null;
        }


        String answer =
                userAnswer.trim();


        if (answer.equalsIgnoreCase("A") ||
            answer.equalsIgnoreCase("B") ||
            answer.equalsIgnoreCase("C") ||
            answer.equalsIgnoreCase("D")) {

            return answer.toUpperCase();
        }


        if (sameText(
                answer,
                question.getOptionA()
        )) {

            return "A";
        }


        if (sameText(
                answer,
                question.getOptionB()
        )) {

            return "B";
        }


        if (sameText(
                answer,
                question.getOptionC()
        )) {

            return "C";
        }


        if (sameText(
                answer,
                question.getOptionD()
        )) {

            return "D";
        }


        return null;
    }


    // ==================================================
    // GET USER ANSWER TEXT
    // ==================================================

    private String getUserAnswerText(
            Question question,
            String userAnswer) {

        if (userAnswer == null ||
            userAnswer.trim().isEmpty()) {

            return null;
        }


        String answer =
                userAnswer.trim();


        if (answer.equalsIgnoreCase("A")) {

            return question.getOptionA();
        }


        if (answer.equalsIgnoreCase("B")) {

            return question.getOptionB();
        }


        if (answer.equalsIgnoreCase("C")) {

            return question.getOptionC();
        }


        if (answer.equalsIgnoreCase("D")) {

            return question.getOptionD();
        }


        return answer;
    }


    // ==================================================
    // GET CORRECT ANSWER LETTER
    // ==================================================

    private String getCorrectAnswerLetter(
            Question question) {

        String correct =
                question.getCorrectAnswer();


        if (correct == null ||
            correct.trim().isEmpty()) {

            return null;
        }


        correct =
                correct.trim();


        if (correct.equalsIgnoreCase("A") ||
            correct.equalsIgnoreCase("B") ||
            correct.equalsIgnoreCase("C") ||
            correct.equalsIgnoreCase("D")) {

            return correct.toUpperCase();
        }


        if (sameText(
                correct,
                question.getOptionA()
        )) {

            return "A";
        }


        if (sameText(
                correct,
                question.getOptionB()
        )) {

            return "B";
        }


        if (sameText(
                correct,
                question.getOptionC()
        )) {

            return "C";
        }


        if (sameText(
                correct,
                question.getOptionD()
        )) {

            return "D";
        }


        return null;
    }


    // ==================================================
    // GET CORRECT ANSWER TEXT
    // ==================================================

    private String getCorrectAnswerText(
            Question question) {

        String correct =
                question.getCorrectAnswer();


        if (correct == null) {

            return "";
        }


        correct =
                correct.trim();


        if (correct.equalsIgnoreCase("A")) {

            return question.getOptionA();
        }


        if (correct.equalsIgnoreCase("B")) {

            return question.getOptionB();
        }


        if (correct.equalsIgnoreCase("C")) {

            return question.getOptionC();
        }


        if (correct.equalsIgnoreCase("D")) {

            return question.getOptionD();
        }


        return correct;
    }


    // ==================================================
    // COMPARE TEXT
    // ==================================================

    private boolean sameText(
            String first,
            String second) {

        if (first == null ||
            second == null) {

            return false;
        }


        return normalizeText(
                first
        ).equalsIgnoreCase(
                normalizeText(
                        second
                )
        );
    }


    // ==================================================
    // NORMALIZE TEXT
    // ==================================================

    private String normalizeText(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace(
                        '\u00A0',
                        ' '
                )
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }
}