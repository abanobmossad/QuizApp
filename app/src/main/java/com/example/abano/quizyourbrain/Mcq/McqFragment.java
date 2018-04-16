package com.example.abano.quizyourbrain.Mcq;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abano.quizyourbrain.QuizMainActivity;
import com.example.abano.quizyourbrain.R;

import java.sql.Array;
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

    // TODO: Rename and change types of parameters
    private String main_q;
    private String category;
    private String qus_type;
    private static  int qus_number;
    private ArrayList<String> quiz_list;
    private ArrayList<Integer> quiz_check_answers;


    public McqFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static McqFragment newInstance(String qus_type, String category, int qus_number, String main_q, ArrayList<String> quiz_list, ArrayList<Integer> quiz_check_answers) {
        McqFragment fragment = new McqFragment();
        Bundle args = new Bundle();
        args.putString(MAIN_QUESTION, main_q);
        args.putString(QUESTIONS_TYPE, qus_type);
        args.putStringArrayList(QUESTIONS_LIST, quiz_list);
        args.putIntegerArrayList(QUESTIONS_CHECK_ANSWERS, quiz_check_answers);
        args.putString(QUESTIONS_CATEGORY, category);
        args.putInt(QUESTIONS_NUMBER, qus_number);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            main_q = getArguments().getString(MAIN_QUESTION);
            category = getArguments().getString(QUESTIONS_CATEGORY);
            quiz_list = getArguments().getStringArrayList(QUESTIONS_LIST);
            qus_type = getArguments().getString(QUESTIONS_TYPE);
            qus_number = getArguments().getInt(QUESTIONS_NUMBER);
            quiz_check_answers = getArguments().getIntegerArrayList(QUESTIONS_CHECK_ANSWERS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Fragment next_fragment = generateQuestions(quiz_list, qus_number, quiz_check_answers);


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mcq_fragment, container, false);
        // main question title
        TextView mQuestionEv = (TextView) v.findViewById(R.id.main_question);
        mQuestionEv.setText(main_q);
        // add category
        TextView categoryTv = (TextView) v.findViewById(R.id.category);
        categoryTv.setText(category);

        // get the score
        TextView scoreTv = (TextView) getActivity().findViewById(R.id.score);
        // get question number
        TextView numberTv = (TextView) v.findViewById(R.id.question_id);
        String num = "Question(" + (qus_number + 1) + "/7" + ")";
        numberTv.setText(num);

        // setup the buttons view

        ListView Listview = (ListView) v.findViewById(R.id.Question_list);

        if (qus_type.equals("MCQ")) {
            ArrayList<mcqAnswersData> mcqData = new ArrayList<>();
            // add data
            for (int i = 0; i < quiz_list.size(); i++) {
                mcqData.add(new mcqAnswersData(quiz_list.get(i), quiz_check_answers.get(i)));
            }
            // setup the adapter
            mcqQuestionsAdapter mcqAdapter = new mcqQuestionsAdapter(next_fragment, getFragmentManager(), getContext(), scoreTv, R.layout.mcq_questions_adapter, mcqData);
            Listview.setAdapter(mcqAdapter);
        }
        return v;
    }

    // set the data and display
    private Fragment generateQuestions(ArrayList<String> quiz_list, int qus_number, ArrayList<Integer> quiz_check_answers) {
        quiz_list.clear();
        quiz_check_answers.clear();
        if (qus_number == 0 || qus_number == 1) {
            quiz_list.add("1");
            quiz_list.add("2");
            quiz_list.add("3");
            quiz_check_answers.add(1);
            quiz_check_answers.add(0);
            quiz_check_answers.add(0);
        } else {
            quiz_list.add("3");
            quiz_list.add("4");
            quiz_list.add("66");
            quiz_check_answers.add(1);
            quiz_check_answers.add(1);
            quiz_check_answers.add(0);
        }

        int next_question = qus_number + 1;
        Fragment next_fragment = McqFragment.newInstance("MCQ", "Physics", next_question, "this is the next question ok??", quiz_list, quiz_check_answers);


        return next_fragment;

    }

    public static  int getQus_number() {
        return qus_number;
    }
}
