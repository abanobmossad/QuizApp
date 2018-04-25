package com.example.abano.quizyourbrain;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button loginButton;
    private SignInButton signIn;
    private ProgressBar barProgress;
    private FirebaseAuth mAuth;
    private String AUTHTAG="Auth_Tag";
    public static final String TAG="GoogleActivity";
    public static final int RC_SIGN_IN=100;
    private FirebaseAuth.AuthStateListener mAuthenListner;
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView=findViewById(R.id.login_name);
        mPasswordView=findViewById(R.id.login_password);
        loginButton=findViewById(R.id.login_button);
        barProgress=findViewById(R.id.login_progress);
        signIn = (SignInButton) findViewById(R.id.google_sign_in_button);



        mAuth=FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage(R.string.noconnection)
                            .setTitle(R.string.checkconnection);
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


    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
            mainActivity.putExtra("user_id",currentUser.getUid());
            startActivity(mainActivity);
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.noconnection)
                    .setTitle(R.string.checkconnection);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }



    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        barProgress.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.VISIBLE);
    }

    public void loginClicked(View view) {

        mEmailView=findViewById(R.id.login_name);
        mPasswordView=findViewById(R.id.login_password);
        String email=mEmailView.getText().toString();
        String password=mPasswordView.getText().toString();
        barProgress.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        if(isOnline()){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                        mainActivity.putExtra("user_id",currentUser.getUid());
                        startActivity(mainActivity);
                    }else {

                        Toast.makeText(getApplicationContext(),"notloged",Toast.LENGTH_SHORT).show();
                        barProgress.setVisibility(View.INVISIBLE);
                        loginButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.noconnection)
                    .setTitle(R.string.checkconnection);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            barProgress.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                            mainActivity.putExtra("user_id",currentUser.getUid());
                            startActivity(mainActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"notloged",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



}

