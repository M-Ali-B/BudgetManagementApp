package com.github.malib.budgetmanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordReset extends AppCompatActivity {

    EditText mEditText, mEditText2;
    Button mButton;
    FirebaseAuth userAuth;
    FirebaseUser user;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

//        Bundle bundle = getArguments();
//        String value = bundle.getString(key);

        mEditText = findViewById(R.id.reset_email);
        mEditText2 = findViewById(R.id.reset_password);
        mButton = findViewById(R.id.button_reset);



        userAuth = FirebaseAuth.getInstance();
        user = userAuth.getCurrentUser();


        final String userEmail = getIntent().getExtras().getString("email");

        mEditText.setText(userEmail);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userPassword = mEditText2.getText().toString();
                user.updatePassword(userPassword);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("email", userEmail);
                editor.apply();
                startActivity(new Intent(getApplicationContext(),MainActivity.class).putExtra("email",userEmail));

            }
        });


    }
}
