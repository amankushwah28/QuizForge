// ===============================
// REGISTER FORM
// ===============================

const registerForm =
    document.getElementById("registerForm");

if (registerForm) {

    registerForm.addEventListener(
        "submit",
        function (event) {

            const name =
                document.getElementById("name").value.trim();

            const username =
                document.getElementById("username").value.trim();

            const email =
                document.getElementById("registerEmail").value.trim();

            const password =
                document.getElementById("registerPassword").value;

            const confirmPassword =
                document.getElementById("confirmPassword").value;


            if (name === "") {

                event.preventDefault();

                alert("Please enter your full name.");

                return;
            }


            if (username === "") {

                event.preventDefault();

                alert("Please enter a username.");

                return;
            }


            if (email === "") {

                event.preventDefault();

                alert("Please enter your Gmail address.");

                return;
            }


            if (password.length < 6) {

                event.preventDefault();

                alert(
                    "Password must contain at least 6 characters."
                );

                return;
            }


            if (password !== confirmPassword) {

                event.preventDefault();

                alert("Passwords do not match.");

                return;
            }


            /*
             * Do NOT prevent the form here.
             *
             * The form will now be submitted
             * to RegisterServlet.
             */

        }
    );
}