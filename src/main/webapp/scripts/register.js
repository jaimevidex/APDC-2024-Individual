function submitRegistration() {
    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim();
    const name = document.getElementById("name").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value.trim();
    const passwordConfirmation = document.getElementById("passwordConfirmation").value.trim();

    // Create data object with user input
    const data = {
        username: username,
        email: email,
        name: name,
        phone: phone,
        password: password,
        passwordConfirmation: passwordConfirmation
    };

    // Send AJAX request
    $.ajax({
        url: '/rest/register/v1', // Update with your actual API endpoint
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        data: JSON.stringify(data),
        success: function(response) {
            console.log("Registration successful:", response);
            // Handle successful registration (e.g., display success message, redirect to login)
            alert("Registration successful!");
            window.location.href = "login.html"; // Example redirect to login page
        },
        error: function(error) {
            console.error("Registration failed:", error);
            // Handle errors by displaying user-friendly messages and logging errors
            alert("Registration failed! Please see console for details.");
        }
    });
}
