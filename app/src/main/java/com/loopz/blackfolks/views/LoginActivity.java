package com.loopz.blackfolks.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.Utilities;
import com.loopz.blackfolks.figerprintLock.LockScreenActivity;
import com.loopz.blackfolks.model.User;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.loopz.blackfolks.Utilities.checkNotNull;
import static com.loopz.blackfolks.constants.FirebaseConstants.USERS;


public class LoginActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    PhoneAuthProvider phoneAuthProvider;

    TextView tvVerification;
    TextView tvMessageText;
    EditText etOTP;
    Button btCancel;
    Button btVerify;
    TextInputLayout inOtp;
    AlertDialog dialog;
    private String mVerificationId;
    ProgressDialog progressDialog;
    EditText etUsername;
    Button btLogin;
    CollectionReference userReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        userReference = FirebaseFirestore.getInstance().collection(USERS);
        etUsername = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        btLogin = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utilities.checkNotNull(etUsername.getText().toString())) {
                    etUsername.setError("Phone number required");
                } else {
                    signInWithPhone();
                }
            }
        });

    }

    private void signInWithPhone() {
        phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(Utilities.addCountryCode(etUsername.getText().toString()), 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
        progressDialog.dismiss();
        loadOtpScreen();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                etOTP.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            dialog.dismiss();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.e("onCodeSent", s);
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        progressDialog.setMessage("Verifying code..");
        //creating the credential
        progressDialog.show();
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Invalid Otp", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    getUserDetails();
                } else {
                    Toast.makeText(LoginActivity.this, "Somethimg went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserDetails() {
        userReference.document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    getToRoom();
                } else {
                    userReference.document(firebaseAuth.getUid()).set(new User(etUsername.getText().toString(), new Random().nextInt() + ""));
                    getToRoom();
                }
            }
        });
    }

    private void getToRoom() {
        Intent intent = new Intent(getApplicationContext(), LockScreenActivity.class);
        startActivity(intent);
        finish();
    }

    protected void loadOtpScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.custom_otp_alert_dialogue, null);
        builder.setView(view);

        inOtp = view.findViewById(R.id.inOtp);
        tvVerification = view.findViewById(R.id.tvVerification);
        tvMessageText = view.findViewById(R.id.tvMessageText);
        etOTP = view.findViewById(R.id.etOTP);
        btCancel = view.findViewById(R.id.btCancel);
        btVerify = view.findViewById(R.id.btVerify);
//        Button btResend = (Button) view.findViewById(R.id.btResend);

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = etOTP.getText().toString();
                if (!checkNotNull(OTP)) {
                    etOTP.setError("Enter OTP");
                } else {
                    progressDialog.setMessage("Verifying code..");
                    progressDialog.show();
                    verifyVerificationCode(OTP);
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        // btResend.setVisibility(View.GONE);
/*        countDown(btResend);
        btResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    dialog.dismiss();

                //   btResend.setVisibility(View.GONE);
                resendotp();
                success_tv.setVisibility(View.VISIBLE);
                success_tv.setText("Your OTP have been resend successfully");
                countDown(btResend);

            }
        });*/


    }

}