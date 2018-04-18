package com.example.abano.quizyourbrain.Mcq;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abano.quizyourbrain.Models.Question;
import com.example.abano.quizyourbrain.QuizMainActivity;
import com.example.abano.quizyourbrain.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link McqFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class McqFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHOICES_LIST = "CHOICES_LIST";
    private static final String QUESTION_NUMBER = "QUESTIONS_NUMBER";
    private static final String QUESTION_CHECK_ANSWERS = "QUESTIONS_CHECK_ANSWERS";
    private static final String QUESTION_ID = "QUESTION_ID";
    private static final String QUESTION_IS_ACTIVE = "IS_ACTIVE";
    private static final String QUESTION_IMAGE = "QUESTION_IMAGE";
    private static final String QUESTION_CATEGORY = "QUESTIONS_CATEGORY";
    private static final String QUESTION_TITLE = "QUESTIONS_TITLE";
    private static final String QUESTION_TYPE = "QUESTIONS_TYPE";

    // TODO: Rename and change types of parameters
    private Long questionId;
    private int questionIsActive;
    private String questionImage;
    private String questionCategory;
    private String questionTitle;
    private String questionType;
    private int questionNumber;
    private ArrayList<String> choices_list;
    private ArrayList<Integer> right_answers_list;


    public McqFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static McqFragment newInstance(Question question, int questionNumber, ArrayList<String> choices_list, ArrayList<Integer> right_answers_list) {
        McqFragment fragment = new McqFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_TITLE, question.getQuestionTitle());
        args.putString(QUESTION_TYPE, question.getQuestionType());
        args.putString(QUESTION_CATEGORY, question.getCategory());
        args.putString(QUESTION_IMAGE, question.getCategory());
        args.putInt(QUESTION_IS_ACTIVE, question.getIsActive());
        args.putLong(QUESTION_ID, question.getId());
        args.putInt(QUESTION_NUMBER, questionNumber);

        args.putStringArrayList(CHOICES_LIST, choices_list);
        args.putIntegerArrayList(QUESTION_CHECK_ANSWERS, right_answers_list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // question arguments
            questionTitle = getArguments().getString(QUESTION_TITLE);
            questionCategory = getArguments().getString(QUESTION_CATEGORY);
            questionType = getArguments().getString(QUESTION_TYPE);
            questionImage = getArguments().getString(QUESTION_IMAGE);
            questionIsActive = getArguments().getInt(QUESTION_IS_ACTIVE);
            questionId = getArguments().getLong(QUESTION_ID);
            questionNumber = getArguments().getInt(QUESTION_NUMBER);

            // choices arguments
            choices_list = getArguments().getStringArrayList(CHOICES_LIST);
            right_answers_list = getArguments().getIntegerArrayList(QUESTION_CHECK_ANSWERS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // display next question
//         generateQuestions(choices_list, questionNumber, right_answers_list);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mcq_fragment, container, false);
        // main question title
        TextView mQuestionEv = (TextView) v.findViewById(R.id.main_question);
        mQuestionEv.setText(questionTitle);
        // add category
        TextView categoryTv = (TextView) v.findViewById(R.id.category);
        categoryTv.setText(questionCategory);

        // get question number
        TextView numberTv = (TextView) v.findViewById(R.id.question_number);
        String num = "Question(" + (questionNumber + 1) + "/7" + ")";
        numberTv.setText(num);

        // setup the buttons view

        return v;
    }


    // set the data and display
    private void generateQuestions(ArrayList<String> choices_list, final int questionNumber, ArrayList<Integer> right_answers_list) {

        choices_list.clear();
        right_answers_list.clear();

        int next_question = questionNumber + 1;
        Question nextQuestion = QuizMainActivity.getAllQuestions().get(next_question);
        Fragment nextFragment = McqFragment.newInstance(nextQuestion, next_question, choices_list, right_answers_list);


       getFragmentManager().beginTransaction().replace(R.id.fragmentContainer,nextFragment).addToBackStack(null).commit();


    }


}
