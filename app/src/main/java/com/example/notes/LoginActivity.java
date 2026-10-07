package com.example.notes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button signInButton;
    private Button signUpButton;
    private UserDatabaseHelper userDatabaseHelper;
    private SharedPrefManager sharedPrefManager;
    private ColorStateList defaultEditTextTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.editText_loginEmail);
        passwordEditText = (EditText) findViewById(R.id.editText_loginPassword);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.checkBox_rememberMe);
        signInButton = (Button) findViewById(R.id.button_signIn);
        signUpButton = (Button) findViewById(R.id.button_goToSignup);
        defaultEditTextTint = emailEditText.getBackgroundTintList();
        sharedPrefManager = SharedPrefManager.getInstance(LoginActivity.this);
        userDatabaseHelper = new UserDatabaseHelper(LoginActivity.this, "USER_DATABASE", null, 1);

        loadRememberedEmail();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadRememberedEmail() {
        boolean rememberMe = sharedPrefManager.readBoolean("REMEMBER_ME", false);

        if (rememberMe) {
            String rememberedEmail = sharedPrefManager.readString("REMEMBERED_EMAIL", "");
            emailEditText.setText(rememberedEmail);
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void signIn() {
        resetLoginFields();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        boolean valid = true;

        if (email.isEmpty()) {
            showFieldError(emailEditText, "Email is required");
            valid = false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            showFieldError(emailEditText, "Invalid email address");
            valid = false;
        }

        if (password.isEmpty()) {
            showFieldError(passwordEditText, "Password is required");
            valid = false;
        }

        if (!valid)
            return;

        if (!userDatabaseHelper.authenticateUser(email, password)) {
            showFieldError(passwordEditText, "Incorrect email or password");
            return;
        }

        boolean rememberMe = rememberMeCheckBox.isChecked();
        sharedPrefManager.writeBoolean("REMEMBER_ME", rememberMe);
        if (rememberMe)
            sharedPrefManager.writeString("REMEMBERED_EMAIL", email);
        else
            sharedPrefManager.remove("REMEMBERED_EMAIL");
        sharedPrefManager.writeString("CURRENT_USER_EMAIL", email);
        Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFieldError(EditText editText, String message) {
        editText.setError(message);
        editText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
    }

    private void resetLoginFields() {
        emailEditText.setError(null);
        passwordEditText.setError(null);

        emailEditText.setBackgroundTintList(defaultEditTextTint);
        passwordEditText.setBackgroundTintList(defaultEditTextTint);
    }


}