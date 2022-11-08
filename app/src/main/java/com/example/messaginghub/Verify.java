package com.example.messaginghub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class Verify extends AppCompatActivity {
    AppCompatButton verifybtn;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private OtpTextView otpTextView;
    String code;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        getSupportActionBar().hide();
        verifybtn=findViewById(R.id.verifybtn);
        otpTextView = findViewById(R.id.code);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }
            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                code=otp;
            }
        });
        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(code!=null) {
                    Intent intent = getIntent();
                    String verificationId = intent.getStringExtra("mVerificationId");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otpTextView.getOTP());
                    signInWithPhoneAuthCredential(credential);
                }
                else{
                    Toast.makeText(Verify.this, "Please enter the code", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent=new Intent(Verify.this,EditProfile.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backdialog();
    }

    void backdialog(){
        AlertDialog.Builder AD=new AlertDialog.Builder(this)
                .setTitle("Choose the following action")
                .setMessage("Do you want to cancel account creation")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AD.show();
    }
}