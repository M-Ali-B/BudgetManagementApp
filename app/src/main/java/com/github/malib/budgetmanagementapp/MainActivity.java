package com.github.malib.budgetmanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    EditText email, password;
    Button signin, signup;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private SignInButton mGoogleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    //setting requestcode
    private int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        signin = findViewById(R.id.button_sign_in);
        signup = findViewById(R.id.button_signup);

        mGoogleSignInButton = findViewById(R.id.sign_in_button);


// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();



        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        account =GoogleSignIn.getLastSignedInAccount(this) ;

        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);

          }
        });




        mDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser != null){
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



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //  Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            mFirebaseUser = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                               Log.w("sign-in-google", "signInWithCredential:failure", task.getException());
                            //  Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
//            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("google-signin", "signInResult:failed code=" + e.getMessage());
            //updateUI(null);
            Toast.makeText(this, "google SignIn Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
//            GoogleSignInAccount account = result.getSignInAccount();
//            firebaseAuthWithGoogle(account);
        }
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
