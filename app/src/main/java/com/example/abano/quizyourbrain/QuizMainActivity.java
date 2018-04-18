package com.example.abano.quizyourbrain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.abano.quizyourbrain.Mcq.McqFragment;
import com.example.abano.quizyourbrain.Models.Question;
import com.example.abano.quizyourbrain.QuestionControl.AddQuestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizMainActivity extends AppCompatActivity {
    private static ArrayList<Question> allQuestions = new ArrayList<>();
    private ArrayList<String> choices_list = new ArrayList<>();
    private ArrayList<Integer> checks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
                Log.w("firebasequestion", "Failed to read value.", error.toException());
            }
        });

    }

    public void generateAllQuestions(DataSnapshot questionSnapshot) {
        String question_title = (String) questionSnapshot.child("questionTitle").getValue();
        String question_category = (String) questionSnapshot.child("category").getValue();
        String image = (String) questionSnapshot.child("image").getValue();
        String question_type = (String) questionSnapshot.child("questionType").getValue();
        Object __meta__ = questionSnapshot.child("__meta__").getValue();
        Long id = (Long) questionSnapshot.child("id").getValue();
        int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;
        Question question = new Question(__meta__, id, question_title, question_category, question_type, isActive, image);
        allQuestions.add(question);
    }

    public void startQuestions(View view) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = McqFragment.newInstance(allQuestions.get(0), 0, choices_list, checks);
            manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }

    }

    public void addQuestions(View view) {
        Intent addQuestionsIntent = new Intent(this, AddQuestion.class);
        startActivity(addQuestionsIntent);
    }

    public static ArrayList<Question> getAllQuestions() {
        return allQuestions;
    }
}
