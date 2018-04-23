package com.example.abano.quizyourbrain;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class QuestionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHOICES_LIST = "CHOICES_LIST";
    private static final String QUESTION_NUMBER = "QUESTIONS_NUMBER";
    private static final String QUESTION_ID = "QUESTION_ID";
    private static final String QUESTION_IS_ACTIVE = "IS_ACTIVE";
    private static final String QUESTION_IMAGE = "QUESTION_IMAGE";
    private static final String QUESTION_CATEGORY = "QUESTIONS_CATEGORY";
    private static final String QUESTION_TITLE = "QUESTIONS_TITLE";
    private static final String QUESTION_TYPE = "QUESTIONS_TYPE";
    private static final String QUESTION_TIME = "QUESTION_TIME";

    // TODO: Rename and change types of parameters
    private Long questionId;
    private int questionIsActive;
    private int questionTime;
    private String questionImage;
    private String questionCategory;
    private String questionTitle;
    private String questionType;
    private int questionNumber;
    private ArrayList<Choice> choices_list;
    private ProgressBar questionTimeBar;


    public QuestionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance(Question question, int questionNumber) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_TITLE, question.getQuestionTitle());
        args.putString(QUESTION_TYPE, question.getQuestionType());
        args.putString(QUESTION_CATEGORY, question.getCategory());
        args.putString(QUESTION_IMAGE, question.getCategory());
        args.putInt(QUESTION_IS_ACTIVE, question.getIsActive());
        args.putLong(QUESTION_ID, question.getId());
        args.putInt(QUESTION_NUMBER, questionNumber);
        args.putInt(QUESTION_TIME, question.getTime());
        args.putParcelableArrayList(CHOICES_LIST, question.getChoices());
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
            questionTime = getArguments().getInt(QUESTION_TIME);
            // choices arguments
            choices_list = getArguments().getParcelableArrayList(CHOICES_LIST);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.question_fragment, container, false);
        // main question title
        TextView mQuestionEv = (TextView) view.findViewById(R.id.main_question);
        mQuestionEv.setText(questionTitle);
        // add category
        TextView categoryTv = (TextView) view.findViewById(R.id.category);
        categoryTv.setText(questionCategory);

        // get question number
        TextView numberTv = (TextView) view.findViewById(R.id.question_number);
        String num = "Question(" + (questionNumber + 1) + "/7" + ")";
        numberTv.setText(num);
        // get question time
        questionTimeBar = (ProgressBar) view.findViewById(R.id.question_time);
        questionTimeBar.setMax(questionTime * 1000);
        questionTimeBar.setProgress(questionTime * 1000);

        // set question choices
        if (questionType.equals("MCQ")) {
            displayChoices(view);
        } else if (questionType.equals("complete")) {
            displayCompleteFeilds(view);
        }

        // load the end activity
        final Intent endIntent = new Intent(getContext(), EndActivity.class);
        endIntent.putExtra("NOT_SOLVED_QUESTIONS", true);

        // set timer to the question
        QuestionCountTimer myCountDownTimer;
        myCountDownTimer = new QuestionCountTimer(questionTime * 1000, 100);
        myCountDownTimer.start();

        return view;
    }

    private void displayCompleteFeilds(View view) {
        LinearLayout answerContainer = (LinearLayout) view.findViewById(R.id.answersContainer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);

        int i = 0;

        for (final Choice choice : choices_list) {

            // making the complete editText
            final EditText chET = new EditText(getContext());
            chET.setLayoutParams(params);
            chET.setHint("Enter the answer number ");
            chET.setBackground(getResources().getDrawable(R.drawable.edit_text_style));
            chET.setId(i);
            chET.setTextColor(getResources().getColor(R.color.textFgColor));
            answerContainer.addView(chET);
            final EditText actionText = ((EditText) view.findViewById(i));

            // adding action
            actionText.addTextChangedListener(new TextWatcher() {
                Timer timer;
                boolean wrongAnswer;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // user is typing: reset already started timer (if existing)
                    if (timer != null) {
                        timer.cancel();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (actionText.getText().toString().equals(choice.getAnsTitle())) {
                                generateNextQuestion(questionNumber);
                            } else {
                                wrongAnswer = true;
                            }
                        }
                    }, 1000); // 600ms delay before the timer executes the „run“ method from TimerTask
                    if (wrongAnswer) {
                        Toast.makeText(getContext(), "Sorry it's a wrong answer! please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            i++;
        }


    }

    private void displayChoices(View view) {
        LinearLayout answerContainer = (LinearLayout) view.findViewById(R.id.answersContainer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 0);

        int i = 0;
        final ArrayList<Button> rightAnswerBtn = new ArrayList<>();
        for (final Choice choice : choices_list) {
            // making the choices buttons
            final Button chBtn = new Button(getContext());
            chBtn.setLayoutParams(params);
            chBtn.setText(choice.getAnsTitle());
            chBtn.setBackground(getResources().getDrawable(R.drawable.buttonshape));
            chBtn.setId(i);
            chBtn.setTextColor(getResources().getColor(R.color.textBgColor));
            answerContainer.addView(chBtn);
            // get the right button
            final Button actionBtn = ((Button) view.findViewById(i));
            if (choice.getIsRight() == 1) {
                rightAnswerBtn.add(actionBtn);
            }
            // adding actions to the button
            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (choice.getIsRight() == 1) {
                        rightAnswerEffect(actionBtn);
                        generateNextQuestion(questionNumber);
                    } else {
                        wrongAnswerEffect(actionBtn);
                        // show the right answer
                        rightAnswerEffect(rightAnswerBtn.get(0));
                    }
                }
            });

            i++;
        }

    }

    private void rightAnswerEffect(Button button) {
        button.setBackground(getResources().getDrawable(R.drawable.button_shape_right));
    }

    private void wrongAnswerEffect(Button button) {
        button.setBackground(getResources().getDrawable(R.drawable.button_shape_wrong));
    }

    // set the data and display
    private void generateNextQuestion(final int questionNumber) {

        choices_list.clear();
        int next_question = questionNumber + 1;
        Question nextQuestion = QuizMainActivity.getAllQuestions().get(next_question);
        if (nextQuestion != null) {
            Fragment nextFragment = QuestionFragment.newInstance(nextQuestion, next_question);
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, nextFragment).commit();
        } else {
            Toast.makeText(getContext(), "This the last question", Toast.LENGTH_SHORT).show();
        }

    }

    /*----------------Time Timer-------------------*/
    private class QuestionCountTimer extends CountDownTimer {

        QuestionCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished);
            Log.d("timeOfTheProcess", " " + progress);
            questionTimeBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            questionTimeBar.setProgress(0);
        }

    }
}
