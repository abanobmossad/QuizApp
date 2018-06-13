package com.example.abano.quizyourbrain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reset extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    FirebaseAuth auth = FirebaseAuth.getInstance();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mEmailView=findViewById(R.id.login_email);



    }


    public void resetClicked(View view) {

        String email=mEmailView.getText().toString();

        if(!email.matches("")){

                if(isEmailValid(email)){
                    if(isOnline()){

                        auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Reset.this);
                                            builder.setMessage(R.string.passwordsent)
                                                    .setTitle(R.string.app_name);
                                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                    finish();
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();


                                        }else{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Reset.this);
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
}
