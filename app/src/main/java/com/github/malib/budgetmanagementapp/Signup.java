package com.github.malib.budgetmanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {
    EditText email, password;
    Button signin, signup;
    ProgressDialog mDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        signin = findViewById(R.id.button_signin_2);
        signup = findViewById(R.id.button_sign_up_2);

        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Signup.super.finish();
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field");
                return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Required error");
                return;
                }

                if (mPassword.length() < 6){
                    password.setError("Password should be more than 6 characters ");
                }

                mDialog.show();
                mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(Signup.this, "You are Signed up", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(Signup.this, "Try again ", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });



            }
        });
    }
}
