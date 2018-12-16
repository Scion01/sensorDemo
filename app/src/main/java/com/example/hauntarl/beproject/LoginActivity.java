package com.example.hauntarl.beproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Button btnGoogle, btnOTP;
    private EditText phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnGoogle = findViewById(R.id.btn_google);
        btnOTP = findViewById(R.id.btn_otp);
        phoneNum = findViewById(R.id.phone);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("277801141933-3b77h8b6vqeimi0nb7jrm85qapfra22u.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        //onGoogleSuccess();
                        //onGoogleFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 999);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 999) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getApplicationContext(),"SignIn Success",Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GAuth failed!", "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Acc ID", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showDialog
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignInSuccess", "signInWithCredential:success");


                            FirebaseUser user = mAuth.getCurrentUser();
                            //most probably email=user.email and name is user.name
                            //sharedPred add karega and check that from next time in splash and destroy on a sign Out
                            //updateUI(user);
                            Toast.makeText(getApplicationContext(),"User Found!",Toast.LENGTH_LONG).show();
                            Log.d("User Object",user.toString());
                            try{
                                Log.d("User Object",user.getDisplayName().toString());
                                Log.d("User Object",user.getEmail().toString());
                            }catch (Exception e){

                            }
                            SharedPreferences sharedPref= getSharedPreferences("mypref", 0);
                            SharedPreferences.Editor editor= sharedPref.edit();
                            editor.putInt("logged", 1);
                            editor.putString("Display Name",user.getDisplayName());
                            editor.putString("Email",user.getEmail());
                            editor.commit();


                            updateDatabase(user.getEmail().toString(), user.getDisplayName().toString());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign In failed", "signInWithCredential:failure", task.getException());

                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateDatabase(final String email,final String name) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child("users").push();
        databaseReference.child("email").setValue(email);
        databaseReference.child("displayName").setValue(name);
        Intent intent = new Intent(getApplicationContext(), DashActivity.class);
        startActivity(intent);
        finish();    Log.d("DBREF",databaseReference.toString());

    }

}
