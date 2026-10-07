package com.example.notes;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button saveProfileButton;
    private UserDatabaseHelper userDatabaseHelper;
    private SharedPrefManager sharedPrefManager;
    private ColorStateList defaultEditTextTint;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = (EditText) view.findViewById(R.id.editText_profileEmail);
        firstNameEditText = (EditText) view.findViewById(R.id.editText_profileFirstName);
        lastNameEditText = (EditText) view.findViewById(R.id.editText_profileLastName);
        passwordEditText = (EditText) view.findViewById(R.id.editText_profilePassword);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.editText_profileConfirmPassword);
        saveProfileButton = (Button) view.findViewById(R.id.button_saveProfile);
        defaultEditTextTint = firstNameEditText.getBackgroundTintList();
        userDatabaseHelper = new UserDatabaseHelper(requireContext(), "USER_DATABASE", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        loadUserInformation();

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void loadUserInformation() {
        String currentEmail = sharedPrefManager.readString("CURRENT_USER_EMAIL", "");
        if (currentEmail.isEmpty()) {
            Toast.makeText(requireContext(), "No logged-in user found", Toast.LENGTH_SHORT).show();
            return;
        }
        User user = userDatabaseHelper.getUserByEmail(currentEmail);
        if (user != null) {
            emailEditText.setText(user.getEmail());
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
        } else
            Toast.makeText(requireContext(), "User information not found", Toast.LENGTH_SHORT).show();
    }

    private void updateProfile() {
        resetProfileFields();
        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean valid = true;

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

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            if (password.isEmpty()) {
                showFieldError(passwordEditText, "New password is required");
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
        }

        if (!valid)
            return;

        User user = new User(email, firstName, lastName, password);
        if (userDatabaseHelper.updateUser(user)) {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
            confirmPasswordEditText.setText("");
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).refreshNavigationHeader();
        } else
            Toast.makeText(requireContext(), "Profile could not be updated", Toast.LENGTH_SHORT).show();
    }

    private void showFieldError(EditText editText, String message) {
        editText.setError(message);
        editText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
    }

    private void resetProfileFields() {
        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        firstNameEditText.setBackgroundTintList(defaultEditTextTint);
        lastNameEditText.setBackgroundTintList(defaultEditTextTint);
        passwordEditText.setBackgroundTintList(defaultEditTextTint);
        confirmPasswordEditText.setBackgroundTintList(defaultEditTextTint);
    }
}