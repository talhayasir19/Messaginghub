package com.example.messaginghub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Registration extends AppCompatActivity {
    EditText Phonenumber;
    AppCompatButton Continue;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    AutoCompleteTextView countrycodes;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    public static String Phoneno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        Phonenumber = findViewById(R.id.Phonenumber);
        Continue = findViewById(R.id.Continue);
        countrycodes = findViewById(R.id.countrycodes);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Registration.this, "Verification failed"+e, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressDialog.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(Registration.this, Verify.class);
                intent.putExtra("mVerificationId", mVerificationId);
                startActivity(intent);
            }
        };
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phoneno=countrycodes.getText().toString()+Phonenumber.getText().toString();
                progressDialog = new ProgressDialog(Registration.this);
                progressDialog.setMessage("Sending OTP...");
                progressDialog.show();
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(firebaseAuth)
                                .setPhoneNumber(Phoneno)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(Registration.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                progressBar.setVisibility(View.VISIBLE);

            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                        } else {

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}