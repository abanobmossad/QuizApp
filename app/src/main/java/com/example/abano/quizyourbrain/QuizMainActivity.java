package com.example.abano.quizyourbrain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Mcq.McqFragment;

import java.util.ArrayList;

public class QuizMainActivity extends AppCompatActivity {
    private int next_question_number = 0;
    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<Integer> isRight = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.main_container);
        if (fragment == null) {
            fragment = McqFragment.newInstance("MCQ", "Sport", next_question_number, getResources().getString(R.string.main_question), questions,isRight);
            next_question_number = 1;
            manager.beginTransaction().add(R.id.main_container, fragment).commit();
        }
    }

    public void nextQuestion(View view) {
        next_question_number = McqFragment.getQus_number()+1;
        Fragment next_fragment = McqFragment.newInstance("MCQ", "Physics", next_question_number, "this is the next question ok??", questions,isRight);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, next_fragment).addToBackStack(null).commit();



    }


    private void checkRightAnswer(int qusNumber, int answer) {
        Toast.makeText(this, String.valueOf(qusNumber) + "  " + String.valueOf(answer), Toast.LENGTH_LONG).show();

    }


}
