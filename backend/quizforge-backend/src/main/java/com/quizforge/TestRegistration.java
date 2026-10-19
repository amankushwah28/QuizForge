package com.quizforge;

public class TestRegistration {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        boolean result = userDAO.registerUser(
                "Aman Test",
                "aman_test_123",
                "aman.test123@gmail.com",
                "123456"
        );

        if (result) {

            System.out.println(
                    "User registered successfully!"
            );

        } else {

            System.out.println(
                    "User registration failed!"
            );
        }
    }
}