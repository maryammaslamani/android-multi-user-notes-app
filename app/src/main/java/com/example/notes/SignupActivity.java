package com.example.notes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private Button backToLoginButton;
    private UserDatabaseHelper userDatabaseHelper;
    private ColorStateList defaultEditTextTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = (EditText) findViewById(R.id.editText_signupEmail);
        firstNameEditText = (EditText) findViewById(R.id.editText_signupFirstName);
        lastNameEditText = (EditText) findViewById(R.id.editText_signupLastName);
        passwordEditText = (EditText) findViewById(R.id.editText_signupPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.editText_signupConfirmPassword);
        registerButton = (Button) findViewById(R.id.button_register);
        backToLoginButton = (Button) findViewById(R.id.button_backToLogin);
        defaultEditTextTint = emailEditText.getBackgroundTintList();
        userDatabaseHelper = new UserDatabaseHelper(SignupActivity.this, "USER_DATABASE", null, 1);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerUser() {
        resetSignupFields();

        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean valid = true;

        if (email.isEmpty()) {
            showFieldError(emailEditText, "Email is required");
            valid = false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            showFieldError(emailEditText, "Invalid email address");
            valid = false;
        }

        if (firstName.isEmpty()) {
            showFieldError(firstNameEditText, "First name is required");
            valid = false;
        } else if (!ValidationUtils.isValidName(firstName)) {
            showFieldError(firstNameEditText, "First name must contain 3-10 characters");
            valid = false;
        }

        if (lastName.isEmpty()) {
            showFieldError(lastNameEditText, "Last name is required");
            valid = false;
        } else if (!ValidationUtils.isValidName(lastName)) {
            showFieldError(lastNameEditText, "Last name must contain 3-10 characters");
            valid = false;
        }

        if (password.isEmpty()) {
            showFieldError(passwordEditText, "Password is required");
            valid = false;
        } else if (!ValidationUtils.isValidPassword(password)) {
            showFieldError(passwordEditText, "Password must contain 6-12 characters, uppercase, lowercase and number");
            valid = false;
        }

        if (confirmPassword.isEmpty()) {
            showFieldError(confirmPasswordEditText, "Confirm password is required");
            valid = false;
        } else if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
            showFieldError(confirmPasswordEditText, "Passwords do not match");
            valid = false;
        }

        if (valid && userDatabaseHelper.emailExists(email)) {
            showFieldError(emailEditText, "Email is already registered");
            valid = false;
        }

        if (!valid)
            return;

        User user = new User(email, firstName, lastName, password);
        userDatabaseHelper.insertUser(user);

        Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void showFieldError(EditText editText, String message) {
        editText.setError(message);
        editText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
    }

    private void resetSignupFields() {
        emailEditText.setError(null);
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        emailEditText.setBackgroundTintList(defaultEditTextTint);
        firstNameEditText.setBackgroundTintList(defaultEditTextTint);
        lastNameEditText.setBackgroundTintList(defaultEditTextTint);
        passwordEditText.setBackgroundTintList(defaultEditTextTint);
        confirmPasswordEditText.setBackgroundTintList(defaultEditTextTint);
    }
}