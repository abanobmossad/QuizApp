package com.example.abano.quizyourbrain;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;
import com.example.abano.quizyourbrain.QuestionControl.AddQuestion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class QuizMainActivity extends AppCompatActivity {
    private static ArrayList<Question> allQuestions = new ArrayList<>();
    private ArrayList<Question> comQuestion = new ArrayList<>();
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        new LoadingFirebaseData().execute();
    }

    public void startQuestions(View view) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isOnline()) {
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
                    if (fragment == null) {
                        fragment = QuestionFragment.newInstance(allQuestions.get(0), 0);
                        manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
                    }
                } else {
                    Toast.makeText(QuizMainActivity.this, "Your are not connected to internet! " + allQuestions.size(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 2000);

    }

    public void addQuestions(View view) {
        Intent addQuestionsIntent = new Intent(this, AddQuestion.class);
        startActivity(addQuestionsIntent);
    }

    public static ArrayList<Question> getAllQuestions() {
        return allQuestions;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }


    // do the retrieving  data in background from firebase
    @SuppressLint("StaticFieldLeak")
    private class LoadingFirebaseData extends AsyncTask<Void, Integer, Void> {
        ProgressDialog processDialog = new ProgressDialog(QuizMainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog.setMessage("Loading Questions...");
            processDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            firebaseConnection();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            processDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            processDialog.dismiss();
            Toast.makeText(QuizMainActivity.this, "Done retrieving the questions", Toast.LENGTH_LONG).show();
        }

        private void firebaseConnection() {
            DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/addNewQuestion/en-US");
            // Read from the database
            questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    generateAllQuestions(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("firebaseQuestion", "Failed to read value.", error.toException());
                }
            });
        }

        private void generateAllQuestions(DataSnapshot dataSnapshot) {
            int i = 0;
            for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {

                ArrayList<Choice> questionChoices = new ArrayList<>();
                // get question data
                String question_title = (String) questionSnapshot.child("questionTitle").getValue();
                String question_category = (String) questionSnapshot.child("category").getValue();
                Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
                String question_type = (String) questionSnapshot.child("questionType").getValue();
                Object __meta__ = questionSnapshot.child("__meta__").getValue();
                Long id = (Long) questionSnapshot.child("id").getValue();
                int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;

                // get choices data
                String choiceTitle1 = (String) questionSnapshot.child("choices").child("ansTitle1").getValue();
                int choiceIsRight1 = (boolean) questionSnapshot.child("choices").child("isRight1").getValue() ? 1 : 0;
                Choice choice1 = new Choice(id, choiceTitle1, choiceIsRight1);
                String choiceTitle2 = (String) questionSnapshot.child("choices").child("ansTitle2").getValue();
                int choiceIsRight2 = (boolean) questionSnapshot.child("choices").child("isRight2").getValue() ? 1 : 0;
                Choice choice2 = new Choice(id, choiceTitle2, choiceIsRight2);
                String choiceTitle3 = (String) questionSnapshot.child("choices").child("ansTitle3").getValue();
                int choiceIsRight3 = (boolean) questionSnapshot.child("choices").child("isRight3").getValue() ? 1 : 0;
                Choice choice3 = new Choice(id, choiceTitle3, choiceIsRight3);
                String choiceTitle4 = (String) questionSnapshot.child("choices").child("ansTitle4").getValue();
                int choiceIsRight4 = (boolean) questionSnapshot.child("choices").child("isRight4").getValue() ? 1 : 0;
                Choice choice4 = new Choice(id, choiceTitle4, choiceIsRight4);
                if (choiceTitle1 != null)
                    questionChoices.add(choice1);
                if (choiceTitle2 != null)
                    questionChoices.add(choice2);
                if (choiceTitle3 != null)
                    questionChoices.add(choice3);
                if (choiceTitle4 != null)
                    questionChoices.add(choice4);
                Question question = new Question(__meta__, id, question_title, question_category, question_type, 10, isActive, imageID, questionChoices);
                if (isActive == 1) {
                    if (question_type.equals("MCQ")) {
                        Log.d("MCQ:: ", question_type);
                        allQuestions.add(question);
                    } else {
                        Log.d("complete:: ", question_type);
                        comQuestion.add(question);
                    }
                }
                publishProgress(i);
                i++;
            }
            Collections.shuffle(allQuestions);
            Collections.shuffle(comQuestion);
            allQuestions.addAll(comQuestion);
        }
    }

}
