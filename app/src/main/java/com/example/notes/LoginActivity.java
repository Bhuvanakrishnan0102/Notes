package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        createAccountTextView = findViewById(R.id.create_account_text);
        progressBar  = findViewById(R.id.progress_bar);

        loginBtn.setOnClickListener(v-> loginUser());
        createAccountTextView.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this,CreateAccountAcitivity.class)));
    }

    void loginUser(){
        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        boolean isValidate = validateData(email,pass);

        if(!isValidate){
            return;
        }
        loginFirebase(email,pass);
    }
    void loginFirebase(String email,String pass){
        changeInProgress(true);

        //getting instance for firebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //the email should be verified atleast once
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){

                        //once the signIn task is successfull then the session is stored in local device.
                        //session is stored in the database
                        //using that same user can be used without logging in evertime
                        //when we close and open the app again it will not ask again for login
                        changeInProgress(false);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        //finish() is used to remove the current page and move to the previous page like back
                        finish();
                    }
                    else{
                        changeInProgress(false);
                        Toast.makeText(LoginActivity.this,"Email is not verified, please verify your email",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    changeInProgress(false);
                    Toast.makeText(LoginActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void changeInProgress(boolean check){
        if(check){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String pass){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(pass.length()<6) {
            passwordEditText.setError("Less than 6 characters");
            return false;
        }
        return true;
    }
}