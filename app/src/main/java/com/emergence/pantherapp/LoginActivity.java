package com.emergence.pantherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emergence.pantherapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    //define components
    //all the components start with the 'login' prefix
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference currentUserReference;
    private String TAG;
    private String currentUserUsername;
    private TextView userEmail;
    private TextView userPassword;
    private Button connectButton;
    private TextView createTextView;
    private TextView resetPass;
    private long LastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize components
        firebaseAuth = FirebaseAuth.getInstance();
        TAG = "LoginActivity";
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_pass);
        connectButton = findViewById(R.id.login_connect);
        createTextView = findViewById(R.id.login_sign_up);
        database = FirebaseDatabase.getInstance();
        resetPass = findViewById(R.id.login_reset_pass);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = LoginActivity.this.userEmail.getText().toString().trim();
                String password = LoginActivity.this.userPassword.getText().toString().trim();
                connectWithEmailAndPass(email, password);
            }
        });

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this,
                        SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - LastClickTime < 1000){
                    return;
                }
                LastClickTime = SystemClock.elapsedRealtime();

                if (userEmail.getText().toString().equals("")) {
                    userEmail.setError("This field cannot be empty");
                } else {
                    firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,
                                                "Email to reset password sent",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                task.getException().getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }

                                }
                            });
                }
            }
        });
    }

    private void connectWithEmailAndPass(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update user session and go to main
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = firebaseAuth.getCurrentUser();

                            // Get a reference in the database for the current user
                            assert user != null;
                            currentUserReference = database.getReference("users/" +
                                    user.getUid() +
                                    "/username");

                            // Retrieve the username of this user from the database
                            currentUserReference
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    currentUserUsername = dataSnapshot.getValue(String.class);
                                    // Get every information into a User class
                                    MainActivity.CURRENT_USER_SESSION = new User(
                                            currentUserUsername,
                                            user.getEmail(),
                                            user.getUid()
                                    );
                                    Intent intent = new Intent(
                                            LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    currentUserUsername = "ERROR";
                                }

                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                            // there might be other errors to check ??
                            userEmail.setError("Wrong email/password combination");
                            userPassword.setError("Wrong email/password combination");
                        }
                    }
                });
    }

}
