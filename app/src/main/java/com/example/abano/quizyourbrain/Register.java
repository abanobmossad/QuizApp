package com.example.abano.quizyourbrain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.graphics.Color;

public class Register extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();

        mEmailView=findViewById(R.id.login_name);
        mPasswordView=findViewById(R.id.register_password);
        mConfirmPassword=findViewById(R.id.confirm_password);



        mConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                String enteredText = s.toString();
                String password=mPasswordView.getText().toString();
                if(enteredText.equals(password)){
                   mConfirmPassword.setTextColor(Color.GREEN);
                   mPasswordView.setTextColor(Color.GREEN);
                }else {
                    mConfirmPassword.setTextColor(Color.YELLOW);
                    mPasswordView.setTextColor(Color.YELLOW);
                }

            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                String enteredText = s.toString();
                String confirm=mConfirmPassword.getText().toString();
                if(enteredText.equals(confirm)){
                    mPasswordView.setTextColor(Color.RED);
                    mConfirmPassword.setTextColor(Color.GREEN);
                }else {
                    mConfirmPassword.setTextColor(Color.RED);
                    mPasswordView.setTextColor(Color.YELLOW);
                }

            }
        });

    }

    public void registerClicked(View view) {
        String email=mEmailView.getText().toString();
        String password=mPasswordView.getText().toString();
        String confirm=mConfirmPassword.getText().toString();



        if(!email.matches("")&&!password.matches("")&&!confirm.matches("")){

            if(password.equals(confirm)){

                if(isEmailValid(email)){
                    if(isOnline()){
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            sendVerificationEmail();

                                        } else {


                                        }

                                        // ...
                                    }
                                });
                    }else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.checkconnection)
                                .setTitle(R.string.noconnection);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_invalid_email)
                            .setTitle(R.string.app_name);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.not_match)
                        .setTitle(R.string.app_name);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }



        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.reqired)
                    .setTitle(R.string.app_name);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


        }

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage(R.string.youraccountiscreated)
                                    .setTitle(R.string.app_name);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();


                            // after email is sent just logout the user and finish this activity

                        }
                        else
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage(R.string.Emailnottrue)
                                    .setTitle(R.string.app_name);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

}
