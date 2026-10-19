<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        QuizForge | Dashboard
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/CSS/style.css">

</head>


<body class="dashboard-page">


<div class="dashboard-wrapper">


    <div class="dashboard-card">


        <!-- =====================================
             BRAND
             ===================================== -->

        <span class="dashboard-badge">

            QUIZFORGE

        </span>


        <!-- =====================================
             WELCOME
             ===================================== -->

        <h1>

            Welcome Back 👋

        </h1>


        <p>

            Ready to test your knowledge?

            Choose an option below to continue.

        </p>


        <!-- =====================================
             DASHBOARD STATISTICS
             ===================================== -->

        <div class="dashboard-stats">


            <!-- TOTAL QUIZZES -->

            <div class="dashboard-stat">

                <div class="dashboard-stat-icon">

                    🎯

                </div>


                <div class="dashboard-stat-value">

                    ${totalQuizzes}

                </div>


                <div class="dashboard-stat-label">

                    Quizzes Taken

                </div>

            </div>


            <!-- AVERAGE SCORE -->

            <div class="dashboard-stat">

                <div class="dashboard-stat-icon">

                    📈

                </div>


                <div class="dashboard-stat-value">

                    <%= String.format(
                            "%.1f",
                            (Double)
                            request.getAttribute(
                                    "averageScore"
                            )
                       ) %>%

                </div>


                <div class="dashboard-stat-label">

                    Average Score

                </div>

            </div>


            <!-- BEST SCORE -->

            <div class="dashboard-stat">

                <div class="dashboard-stat-icon">

                    🏆

                </div>


                <div class="dashboard-stat-value">

                    <%= String.format(
                            "%.1f",
                            (Double)
                            request.getAttribute(
                                    "bestScore"
                            )
                       ) %>%

                </div>


                <div class="dashboard-stat-label">

                    Best Score

                </div>

            </div>


            <!-- LAST QUIZ -->

            <div class="dashboard-stat">

                <div class="dashboard-stat-icon">

                    📚

                </div>


                <div class="dashboard-stat-value
                            dashboard-last-subject">

                    <%= request.getAttribute(
                            "lastQuizSubject"
                       ) %>

                </div>


                <div class="dashboard-stat-label">

                    Last Quiz

                </div>

            </div>


        </div>


        <!-- =====================================
             DASHBOARD ACTIONS
             ===================================== -->

        <div class="dashboard-actions">


            <!-- START QUIZ -->

            <a
                href="${pageContext.request.contextPath}/quiz.html"
                class="dashboard-action primary">

                🎯<br>

                Start Quiz

            </a>


            <!-- PROFILE -->

            <a
                href="${pageContext.request.contextPath}/profile"
                class="dashboard-action">

                👤<br>

                My Profile

            </a>


            <!-- QUIZ HISTORY -->

            <a
                href="${pageContext.request.contextPath}/quiz-history"
                class="dashboard-action">

                📊<br>

                Quiz History

            </a>


            <!-- LOGOUT -->

            <a
                href="${pageContext.request.contextPath}/logout"
                class="dashboard-action danger">

                🚪<br>

                Logout

            </a>


            <!-- =================================
                 ADMIN PANEL
                 ONLY FOR ADMIN
                 ================================= -->

            <%
                String userRole =
                        (String)
                        request.getAttribute(
                                "userRole"
                        );

                if ("ADMIN".equalsIgnoreCase(
                        userRole
                )) {
            %>

                <a
                    href="${pageContext.request.contextPath}/admin"
                    class="dashboard-action admin-action">

                    🛠️<br>

                    Admin Panel

                </a>

            <%
                }
            %>


        </div>


    </div>


</div>


</body>

</html>