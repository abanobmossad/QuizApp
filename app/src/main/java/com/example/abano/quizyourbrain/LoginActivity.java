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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final int google_SIGN_IN=100;
    public static final int facebook_sign_in=200;
    private FirebaseAuth.AuthStateListener mAuthenListner;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager ;



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
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, google_SIGN_IN);
                }


        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.facebook_sign_in_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        if(isOnline()){

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser!=null){
                Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                mainActivity.putExtra("user_id",currentUser.getUid());
                startActivity(mainActivity);
            }

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


        if(!email.matches("")&&!password.matches("")){
            if(isEmailValid(email)){
                if(isOnline()){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if(currentUser!=null){
                                    Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                                    mainActivity.putExtra("user_id",currentUser.getUid());
                                    startActivity(mainActivity);
                                }
                            }else {

                                Toast.makeText(getApplicationContext(),"notloged",Toast.LENGTH_SHORT).show();
                                barProgress.setVisibility(View.INVISIBLE);
                                loginButton.setVisibility(View.VISIBLE);
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
                    barProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
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
                barProgress.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
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
            barProgress.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);

        }






    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == google_SIGN_IN) {
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

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if(isOnline()){
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if(currentUser!=null){
                                    Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                                    mainActivity.putExtra("user_id",currentUser.getUid());
                                    startActivity(mainActivity);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(getApplicationContext(),"notloged",Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

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



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser!=null){
                                Intent mainActivity = new Intent(getApplicationContext(),QuizMainActivity.class);
                                mainActivity.putExtra("user_id",currentUser.getUid());
                                startActivity(mainActivity);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

}

