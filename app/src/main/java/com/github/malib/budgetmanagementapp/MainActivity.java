package com.github.malib.budgetmanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    EditText email, password;
    Button signin, signup;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        signin = findViewById(R.id.button_sign_in);
        signup = findViewById(R.id.button_signup);

        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Required error");
                    return;
                }

                mDialog.show();

                mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    mDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    mDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();


                                }

                                // ...
                            }
                        });


            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup.class));
                MainActivity.super.finish();
            }
        });






    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("email", null);
        if (restoredText != null) {
            String sharedPrefEmail = prefs.getString("email", " ");//"No name defined" is the default value.
            email.setText(sharedPrefEmail);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        //  FirebaseUser currentUser = mAuth.getCurrentUser();

//
        //String userEmail = getIntent().getExtras().getString("email");
        //Log.d("mail",userEmail);
//   if(userEmail != null)
//    email.setText(userEmail);


//        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        String restoredText = prefs.getString("email", null);
//        if (restoredText != null) {
//            String sharedPrefEmail = prefs.getString("email", " ");//"No name defined" is the default value.
//            email.setText(sharedPrefEmail);
//        }


    }


}
