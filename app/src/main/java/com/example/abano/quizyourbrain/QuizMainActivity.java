package com.example.abano.quizyourbrain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.abano.quizyourbrain.QuestionControl.AddQuestion;

import java.util.ArrayList;

public class QuizMainActivity extends AppCompatActivity {
    private int next_question_number = 0;
    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<Integer> isRight = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);


    }


    public void addQuestions(View view) {
        Intent addQuestionsIntent = new Intent(this,AddQuestion.class);
        startActivity(addQuestionsIntent);
    }
}
