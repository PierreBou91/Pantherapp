package com.emergence.pantherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emergence.pantherapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    //define components
    //all the components start with the 'signup' prefix
    private FirebaseAuth firebaseAuth;
    private String TAG;
    private TextView disclaimer;
    private EditText username;
    private EditText userEmail;
    private EditText userPassword;
    private EditText confirmPasswordButton;
    private Button validateButton;
    private User user;
    private DatabaseReference currentUserReference;

    private FirebaseDatabase database;
    private DatabaseReference userRef;

    // This pattern is used to control the password input given by the new user. If this pattern is
    // changed, make sure to also change the setError text in the validatePassword() function.
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
            "(?=.*[0-9]+.*)" +
            "(?=.*[a-zA-Z]+.*)" +
            "[0-9a-zA-Z]" +
            "{6,}" +
            "$");
    private long LastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // initialize components
        firebaseAuth = FirebaseAuth.getInstance();
        TAG = "SignUpActivity";
        disclaimer = findViewById(R.id.signup_disclaimer);
        username = findViewById(R.id.signup_username);
        userEmail = findViewById(R.id.signup_email);
        userPassword = findViewById(R.id.signup_password);
        confirmPasswordButton = findViewById(R.id.signup_confirmPassword);
        validateButton = findViewById(R.id.signup_validate);

        //instance and ref of db used to store user information
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - LastClickTime < 1000){
                    return;
                }
                LastClickTime = SystemClock.elapsedRealtime();

                //checking for valid inputs before committing sign up
                if (isEmailValid() && isPasswordValid()) {
                    user = new User(
                            username.getText().toString().trim(),
                            userEmail.getText().toString().trim(),
                            "tempUID"
                    );

                    String password = SignUpActivity.this.userPassword.getText().toString().trim();

                    createAccount(user, password);
                }
            }
        });

    }

    private void createAccount(final User user, String password) {
        final String email = user.getEmail();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, assign values to CURRENT_USER_SESSION and go to main
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser newUser = firebaseAuth.getCurrentUser();

                            assert newUser != null;
                            putNewUserInDatabase(newUser);
                            Toast.makeText(SignUpActivity.this, "Sign up success.",
                                    Toast.LENGTH_LONG).show();

                            // Assign user values to CURRENT_USER_SESSION
                            MainActivityLoggedIn.CURRENT_USER_SESSION = new User(
                                    user.getUsername(),
                                    email,
                                    newUser.getUid()
                            );

                            Intent intent = new Intent(
                                    SignUpActivity.this, MainActivityLoggedIn.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            userEmail.setError("Email address might already be registered. You" +
                                    "can recover your password.");
                        }
                    }
                });
    }

    private void putNewUserInDatabase(FirebaseUser newUser) {
        // There is no "username" field in the user section for Firebase. So every user has to be
        // entered in the database "again" using the UID given by Firebase.
        userRef.child(newUser.getUid()).child("username").setValue(this.user.getUsername());
        userRef.child(newUser.getUid()).child("email").setValue(this.user.getEmail());
    }

    private boolean isEmailValid() {
        String emailInput = userEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            userEmail.setError("An email address is required.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            userEmail.setError("Please enter a valid email address.");
            return false;
        } else {
            userEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordValid() {
        String passInput = userPassword.getText().toString().trim();
        String passInput2 = confirmPasswordButton.getText().toString().trim();

        if (passInput.isEmpty() || passInput2.isEmpty()) {
            userPassword.setError("Password must contain at least one letter, one number and" +
                    " be longer than 6 characters.");
            confirmPasswordButton.setError("Password must contain at least one letter, one " +
                    "number and be longer than 6 characters.");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passInput).matches() ||
                !PASSWORD_PATTERN.matcher(passInput2).matches()) {
            userPassword.setError("Password must contain at least one letter, one number and" +
                    " be longer than 6 characters.");
            confirmPasswordButton.setError("Password must contain at least one letter, one " +
                    "number and be longer than 6 characters.");
            return false;
        } else if (!passInput.equals(passInput2)) {
            userPassword.setError("Passwords don't match.");
            confirmPasswordButton.setError("Passwords don't match.");
            return false;
        } else {
            userPassword.setError(null);
            confirmPasswordButton.setError(null);
            return true;
        }
    }
}
