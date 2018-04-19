package com.example.abano.quizyourbrain;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar barProgress;
    private View mLoginFormView;
    FirebaseAuth mAuth;
    private String AUTHTAG="Auth_Tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView=findViewById(R.id.login_name);
        mPasswordView=findViewById(R.id.login_password);
        loginButton=findViewById(R.id.login_button);
        barProgress=findViewById(R.id.login_progress);

        mAuth=FirebaseAuth.getInstance();




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        barProgress.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);
    }

    public void loginClicked(View view) {
        Toast.makeText(getApplicationContext(),"logedtop",Toast.LENGTH_SHORT).show();
        mEmailView=findViewById(R.id.login_name);
        mPasswordView=findViewById(R.id.login_password);
        String email=mEmailView.getText().toString();
        String password=mPasswordView.getText().toString();


        barProgress.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"logedbe",Toast.LENGTH_SHORT).show();
                    Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                    startActivity(mainActivity);
                    Toast.makeText(getApplicationContext(),"logedaf",Toast.LENGTH_SHORT).show();
                }else {
                    Log.w("createUserWithEmail:fa", task.getException());
                    Toast.makeText(getApplicationContext(),"notloged",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
