package com.emergence.pantherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity {
    //define components
    //all the components start with the 'signup' prefix
    private FirebaseAuth signupAuth;
    private String signup_TAG;
    private TextView signup_disclaimer;
    private EditText signup_username;
    private EditText signup_email;
    private EditText signup_password;
    private Button signup_validate;
    private User signup_user;

    private FirebaseDatabase database;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // initialize components
        signupAuth = FirebaseAuth.getInstance();
        signup_TAG = "SignUpActivity";
        signup_disclaimer = findViewById(R.id.signup_disclaimer);
        signup_username = findViewById(R.id.signup_username);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_validate = findViewById(R.id.signup_validate);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        signup_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signup_user = new User(
                        signup_username.getText().toString(),
                        signup_email.getText().toString(),
                        "tempUID"
                );

                String password = signup_password.getText().toString();

                createAccount(signup_user, password);
            }
        });

    }

    private void createAccount(User user, String password)   {
        String email = user.getEmail();

        signupAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(signup_TAG, "createUserWithEmail:success");
                            FirebaseUser newUser = signupAuth.getCurrentUser();
                            assert newUser != null;
                            putNewUserInDatabase(newUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(signup_TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void putNewUserInDatabase(FirebaseUser newUser) {
        userRef.child(newUser.getUid()).child("username").setValue(signup_user.getUsername());
        userRef.child(newUser.getUid()).child("email").setValue(signup_user.getEmail());
    }
}
