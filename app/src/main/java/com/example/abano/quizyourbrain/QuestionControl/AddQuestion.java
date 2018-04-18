package com.example.abano.quizyourbrain.QuestionControl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.abano.quizyourbrain.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddQuestion extends AppCompatActivity {

    private EditText questionTitle;
    private EditText questionCatecory;
    private Spinner questionType;
    private Switch isActiveSw;
    private DatabaseReference questionsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_question);
        // get question info
        Button save = (Button) findViewById(R.id.saveQuestion);
        questionTitle = (EditText) findViewById(R.id.question_title);
        questionCatecory = (EditText) findViewById(R.id.question_category);
        questionType = (Spinner) findViewById(R.id.question_type);
        isActiveSw = (Switch) findViewById(R.id.isActive);
        questionsDatabase = FirebaseDatabase.getInstance().getReference("Questions");


        // save the info
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });
    }

    private void addQuestion() {
        String question_title = String.valueOf(questionTitle.getText()).trim();
        String question_category = String.valueOf(questionCatecory.getText()).trim();
        String question_type = String.valueOf(questionType.getSelectedItem());
        int isActive = isActiveSw.isChecked() ? 1 : 0;

        if (!TextUtils.isEmpty(question_title) && !TextUtils.isEmpty(question_category) && !TextUtils.isEmpty(question_type)) {
// Write a message to the database
//           String id = questionsDatabase.push().getKey();
//            Question ques = new Question(id,question_title,question_category,question_type,isActive,"");
//            questionsDatabase.child(id).setValue(ques);

            Toast.makeText(this, "Question created successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please fill all the fields and save", Toast.LENGTH_LONG).show();
        }
    }
}
