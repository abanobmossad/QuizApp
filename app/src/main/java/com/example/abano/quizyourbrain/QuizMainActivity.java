package com.example.abano.quizyourbrain;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Mcq.McqFragment;
import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;
import com.example.abano.quizyourbrain.QuestionControl.AddQuestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

public class QuizMainActivity extends AppCompatActivity {
    private static ArrayList<Question> allQuestions = new ArrayList<>();
    private ArrayList<Question> comQuestion = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/addNewQuestion/en-US");
        // Read from the database
        questionsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    generateAllQuestions(questionSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebaseQuestion", "Failed to read value.", error.toException());
            }
        });
        Toast.makeText(this, "Start the quiz", Toast.LENGTH_SHORT).show();
    }


    public void generateAllQuestions(DataSnapshot questionSnapshot) {
        ArrayList<Choice> questionChoices = new ArrayList<>();
        // get question data
        String question_title = (String) questionSnapshot.child("questionTitle").getValue();
        String question_category = (String) questionSnapshot.child("category").getValue();
        String image = (String) questionSnapshot.child("image").getValue();
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
        if (choiceTitle1 !=null)
            questionChoices.add(choice1);
        if (choiceTitle2 != null)
            questionChoices.add(choice2);
        if (choiceTitle3 !=null)
            questionChoices.add(choice3);
        if (choiceTitle4 !=null)
            questionChoices.add(choice4);
        Question question = new Question(__meta__, id, question_title, question_category, question_type, isActive, image, questionChoices);
        if (isActive == 1) {
            if (question_type.equals("MCQ")) {
                Log.d("MCQ:: ", question_type);
                allQuestions.add(question);
            } else {
                Log.d("complete:: ", question_type);
                comQuestion.add(question);
            }
        }
    }

    public void startQuestions(View view) {
        Collections.shuffle(allQuestions);
        Collections.shuffle(comQuestion);
        allQuestions.addAll(comQuestion);
        Log.d("allQuestions", "" + allQuestions.get(0).getQuestionType());
        Log.d("allQuestions", "" + allQuestions.get(1).getQuestionType());
        Log.d("allQuestions", "" + allQuestions.get(2).getQuestionType());
        Log.d("questionsSize", " " + allQuestions.size());


        Log.d("completeQuestions", "" + comQuestion.get(0).getQuestionType());
        Log.d("completeQuestions", "" + comQuestion.get(1).getQuestionType());
        Log.d("completeQuestions", " " + comQuestion.size());


        if (isOnline()) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
            if (fragment == null) {
                fragment = McqFragment.newInstance(allQuestions.get(0), 0);
                manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
            }
        } else {
            Toast.makeText(this, "Your are not connected to internet!", Toast.LENGTH_SHORT).show();
        }

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
}
