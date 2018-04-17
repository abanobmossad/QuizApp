package com.example.abano.quizyourbrain.Mcq;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abano.quizyourbrain.Models.Question;
import com.example.abano.quizyourbrain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link McqFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class McqFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MAIN_QUESTION = "MAIN_QUESTION";
    private static final String QUESTIONS_LIST = "QUESTIONS_LIST";
    private static final String QUESTIONS_TYPE = "QUESTIONS_TYPE";
    private static final String QUESTIONS_CATEGORY = "QUESTIONS_CATEGORY";
    private static final String QUESTIONS_NUMBER = "QUESTIONS_NUMBER";
    private static final String QUESTIONS_CHECK_ANSWERS = "QUESTIONS_CHECK_ANSWERS";
    private static final String QUESTIONS_IMAGE = "QUESTIONS_IMAGE";

    // TODO: Rename and change types of parameters
    private String main_q;
    private String category;
    private String qus_type;
    private String image=null;
    private static int questionNumber;
    private ArrayList<String> choices_list;
    private ArrayList<Integer> right_answers_list;


    public McqFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static McqFragment newInstance(String qus_type, String category, int questionNumber, String main_q, ArrayList<String> choices_list, ArrayList<Integer> right_answers_list) {
        McqFragment fragment = new McqFragment();
        Bundle args = new Bundle();
        args.putString(MAIN_QUESTION, main_q);
        args.putString(QUESTIONS_TYPE, qus_type);
        args.putStringArrayList(QUESTIONS_LIST, choices_list);
        args.putIntegerArrayList(QUESTIONS_CHECK_ANSWERS, right_answers_list);
        args.putString(QUESTIONS_CATEGORY, category);
        args.putInt(QUESTIONS_NUMBER, questionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            main_q = getArguments().getString(MAIN_QUESTION);
            category = getArguments().getString(QUESTIONS_CATEGORY);
            choices_list = getArguments().getStringArrayList(QUESTIONS_LIST);
            qus_type = getArguments().getString(QUESTIONS_TYPE);
            questionNumber = getArguments().getInt(QUESTIONS_NUMBER);
            right_answers_list = getArguments().getIntegerArrayList(QUESTIONS_CHECK_ANSWERS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fragment next_fragment = generateQuestions(choices_list, questionNumber, right_answers_list);


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mcq_fragment, container, false);
        // main question title
        TextView mQuestionEv = (TextView) v.findViewById(R.id.main_question);
        mQuestionEv.setText(main_q);
        // add category
        TextView categoryTv = (TextView) v.findViewById(R.id.category);
        categoryTv.setText(category);


        // get question number
        TextView numberTv = (TextView) v.findViewById(R.id.question_number);
        String num = "Question(" + (questionNumber + 1) + "/7" + ")";
        numberTv.setText(num);

        // setup the buttons view

        return v;
    }

    FirebaseDatabase questionsDataBase;

    // set the data and display
    private Fragment generateQuestions(ArrayList<String> choices_list, final int questionNumber, ArrayList<Integer> right_answers_list) {

        questionsDataBase = FirebaseDatabase.getInstance();
        final DatabaseReference questions = questionsDataBase.getReference("questions");
// Read from the database


        final String[] title = new String[1];
        final String category ;
        questions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot q = dataSnapshot.child(String.valueOf(questionNumber));
                Question qq  = q.getValue(Question.class);
                title[0] = qq.getQuestionTitle();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        choices_list.clear();
        right_answers_list.clear();

        int next_question = questionNumber + 1;
        Fragment next_fragment = McqFragment.newInstance("MCQ", "Physics", next_question,"okok", choices_list, right_answers_list);

        return next_fragment;

    }

    public static int getquestionNumber() {
        return questionNumber;
    }
}
