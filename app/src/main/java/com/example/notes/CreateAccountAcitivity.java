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

public class CreateAccountAcitivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_acitivity);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        loginBtnTextView = findViewById(R.id.login_text);
        progressBar  = findViewById(R.id.progress_bar);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> startActivity(new Intent(CreateAccountAcitivity.this,LoginActivity.class)));
    }
    void createAccount(){
        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();
        String cpass = confirmPasswordEditText.getText().toString();
        boolean isValidate = validateData(email,pass,cpass);

        if(!isValidate){
            return;
        }
        createFirebase(email,pass);
    }
    void createFirebase(String email, String pass){
        changeInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(CreateAccountAcitivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            changeInProgress(false);
                            Toast.makeText(CreateAccountAcitivity.this,"SuccessFully created, Check email to verify",Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();

                        }
                        else{
                            Toast.makeText(CreateAccountAcitivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            changeInProgress(false);
                        }
                    }
                }
        );
    }
    void changeInProgress(boolean check){
        if(check){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String pass, String cpass){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if(pass.length()<6){
            passwordEditText.setError("Less than 6 characters");
            return false;
        }
        if(!pass.equals(cpass)){
            confirmPasswordEditText.setError("Password doesn't match");
            return false;
        }
        return true;
    }
}