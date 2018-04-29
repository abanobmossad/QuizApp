package com.example.abano.quizyourbrain;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class QuestionFragment extends Fragment {
    private static final String CHOICES_LIST = "CHOICES_LIST";
    private static final String QUESTION_NUMBER = "QUESTIONS_NUMBER";
    private static final String QUESTION_ID = "QUESTION_ID";
    private static final String QUESTION_IMAGE = "QUESTION_IMAGE";
    private static final String QUESTION_CATEGORY = "QUESTIONS_CATEGORY";
    private static final String QUESTION_TITLE = "QUESTIONS_TITLE";
    private static final String QUESTION_l = "QUESTION_l";
    private static final String QUESTION_TYPE = "QUESTIONS_TYPE";
    private static final String QUESTION_TIME = "QUESTION_TIME";
    private static final String REWARDED_AD = "REWARDED_AD";
    private Long questionId;
    private int questionTime;
    private String questionL;
    private Long imageId;
    private String questionCategory;
    private String questionTitle;
    private String questionType;
    private int questionNumber;
    private ArrayList<Choice> choices_list;
    private ProgressBar questionTimeBar;
    private ImageView quesImage;
    private RewardedVideoAd mRewardedVideoAd;
    private QuestionCountTimer myCountDownTimer;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Question question, int questionNumber) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_TITLE, question.getQuestionTitle());
        args.putString(QUESTION_TYPE, question.getQuestionType());
        args.putString(QUESTION_CATEGORY, question.getCategory());
        args.putString(QUESTION_l, question.getQuestionLevel());
        if (question.getImage() != null)
            args.putLong(QUESTION_IMAGE, question.getImage());
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
        // initialize the ads video
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
        loadRewardedVideoAd();
        watchingVideoCoinsAds();
        //---------------------------
        if (getArguments() != null) {
            // question arguments
            questionTitle = getArguments().getString(QUESTION_TITLE);
            questionCategory = getArguments().getString(QUESTION_CATEGORY);
            questionType = getArguments().getString(QUESTION_TYPE);
            questionL = getArguments().getString(QUESTION_l);
            imageId = getArguments().getLong(QUESTION_IMAGE);
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
        TextView mQuestionEv = view.findViewById(R.id.main_question);
        mQuestionEv.setText(questionTitle);
        // add category
        TextView categoryTv = view.findViewById(R.id.category);
        categoryTv.setText(questionCategory);
        // get question number
        TextView numberTv = view.findViewById(R.id.question_number);
        String num = "Question(" + (questionL) + "/7" + ")";
        numberTv.setText(num);
        // put question image
        quesImage = view.findViewById(R.id.questionImage);
        connectImages(imageId);
        // get question time
        questionTimeBar = view.findViewById(R.id.question_time);
        questionTimeBar.setMax(questionTime * 1000);
        questionTimeBar.setProgress(questionTime * 1000);
        /*-----------------------------------*/
        //               Ads
        Button watchVideoBtn = view.findViewById(R.id.coinVideo);
        watchVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
            }
        });
        /*------------------------------------*/
        // set question choices
        if (questionType.equals("MCQ")) {
            displayChoices(view);
        } else if (questionType.equals("complete")) {
            displayCompleteFields(view);
        }
        // set timer to the question

        myCountDownTimer = new QuestionCountTimer(questionTime * 1000, 100);

        myCountDownTimer.create();
        return view;
    }

    private void displayCompleteFields(View view) {
        LinearLayout answerContainer = view.findViewById(R.id.answersContainer);
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
            final EditText actionText = (view.findViewById(i));
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
        LinearLayout answerContainer = view.findViewById(R.id.answersContainer);
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
            final Button actionBtn = (view.findViewById(i));
            if (choice.getIsRight() == 1) {
                rightAnswerBtn.add(actionBtn);
            }
            // adding actions to the button
            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (choice.getIsRight() == 1) {
                        rightAnswerEffect(actionBtn);
//                        addVisitedQuestion();
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
        try {
            int next_question = questionNumber + 1;
            Fragment nextFragment = QuestionFragment.newInstance(LoadData.getQuestions().get(next_question), next_question);
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, nextFragment).commit();
        } catch (IndexOutOfBoundsException e) {
            Log.d("generateNextQuestion", e.getMessage());
        }
    }

    // reload image from firebase
    private void displayImage(String imageName) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("flamelink/media/" + imageName);
        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.with(getActivity())
                            .load(uri)
                            .resize(768, 432)
                            .into(quesImage);
                } catch (IllegalArgumentException e) {
                    Log.e("PicassoError", e.getMessage());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void connectImages(final Long imageId) {
        final DatabaseReference mediaDatabase = FirebaseDatabase.getInstance().getReference("flamelink/media/files");
        // Read from the database
        mediaDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot mediaSnapshot : dataSnapshot.getChildren()) {
                    Long id = (Long) mediaSnapshot.child("id").getValue();
                    String image_name = (String) mediaSnapshot.child("file").getValue();
                    if (imageId.equals(id)) {
                        displayImage(image_name);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebaseQuestion", "Failed to read value.", error.toException());
            }
        });
    }

    private void addVisitedQuestion() {
        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra("user_id");
        DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/visitedQuestions");
        usersDatabase.child(questionId.toString()).setValue(questionId.toString());

    }

    /*----------------Time Timer-------------------*/
    private class QuestionCountTimer extends QuestionTimer {

        QuestionCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval, true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int progress = (int) (millisUntilFinished);
            Log.d("timeOfTheProcess", " " + progress);
            questionTimeBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            Toast.makeText(getContext(), "finish", Toast.LENGTH_SHORT).show();
            questionTimeBar.setProgress(0);
        }

    }

    /*---------Ads loaded-----------*/

    private void watchingVideoCoinsAds() {
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewarded(RewardItem reward) {
                Toast.makeText(getContext(), "onRewarded! currency: " + reward.getType() + "  amount: " + reward.getAmount(), Toast.LENGTH_SHORT).show();
                // Reward the user.
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Toast.makeText(getContext(), "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                myCountDownTimer.resume();
                Toast.makeText(getContext(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                Toast.makeText(getContext(), "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                Toast.makeText(getContext(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                myCountDownTimer.pause();
                Toast.makeText(getContext(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
                Toast.makeText(getContext(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoCompleted() {
                Toast.makeText(getContext(), "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());
        }
    }


}
