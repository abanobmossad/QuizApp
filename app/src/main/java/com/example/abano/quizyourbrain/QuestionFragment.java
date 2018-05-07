package com.example.abano.quizyourbrain;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.End_UI.EndFragment;
import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;


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
    private static final String QUESTION_LEVEL = "QUESTION_LEVEL";
    private static final String QUESTION_TYPE = "QUESTIONS_TYPE";
    private static final String QUESTION_TIME = "QUESTION_TIME";
    private static final String SCORE = "SCORE";
    private Long questionId;
    private int questionTime;
    private String questionLevel;
    private Long imageId;
    private String questionCategory;
    private String questionTitle;
    private String questionType;
    private int questionNumber;
    private ArrayList<Choice> choices_list;
    private ProgressBar questionTimeBar;
    private ImageView quesImage;
    private RewardedVideoAd RewardedVideoAd;
    private InterstitialAd mInterstitialAd;
    private QuestionCountTimer myCountDownTimer;
    private boolean watchedQuestionAd = false;
    private TextView coinsTv;
    private TextView scoreTv;
    private String score;
    private boolean pressed = false;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Question question, int questionNumber, String score) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_TITLE, question.getQuestionTitle());
        args.putString(QUESTION_TYPE, question.getQuestionType());
        args.putString(QUESTION_CATEGORY, question.getCategory());
        args.putString(QUESTION_LEVEL, question.getQuestionLevel());
        if (question.getImage() != null)
            args.putLong(QUESTION_IMAGE, question.getImage());
        args.putLong(QUESTION_ID, question.getId());
        args.putInt(QUESTION_NUMBER, questionNumber);
        args.putInt(QUESTION_TIME, question.getTime());
        args.putParcelableArrayList(CHOICES_LIST, question.getChoices());
        args.putString(SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the ads video
        RewardedVideoAd = QuizMainActivity.getRewardedVideoAd();
        watchingVideoCoinsAds();
        //  Interstitial Ad
        mInterstitialAd = QuizMainActivity.getInterstitialAd();
        QuizMainActivity.loadPopupAd();
        //---------------------------
        if (getArguments() != null) {
            // question arguments
            questionTitle = getArguments().getString(QUESTION_TITLE);
            questionCategory = getArguments().getString(QUESTION_CATEGORY);
            questionType = getArguments().getString(QUESTION_TYPE);
            questionLevel = getArguments().getString(QUESTION_LEVEL);
            imageId = getArguments().getLong(QUESTION_IMAGE);
            questionId = getArguments().getLong(QUESTION_ID);
            questionNumber = getArguments().getInt(QUESTION_NUMBER);
            questionTime = getArguments().getInt(QUESTION_TIME);
            // choices arguments
            choices_list = getArguments().getParcelableArrayList(CHOICES_LIST);
            score = getArguments().getString(SCORE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
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
        String num = "Question (" + (questionNumber + 1) + "/7" + ")";
        numberTv.setText(num);
        // put question image
        quesImage = view.findViewById(R.id.questionImage);
        connectImages(imageId);
        // get question time
        questionTimeBar = view.findViewById(R.id.question_time);
        questionTimeBar.setMax(questionTime * 1000);
        questionTimeBar.setProgress(questionTime * 1000);
        // load banner ad
        AdView AdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView.loadAd(adRequest);
        // load coins
        RelativeLayout coinsGroup = view.findViewById(R.id.coinsGroup);
        coinsTv = view.findViewById(R.id.coins);
        scoreTv = view.findViewById(R.id.score);
        scoreTv.setText("Score " + score);

        coinsGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Use Coins ")
                        .setMessage(R.string.useCoins)
                        .setIcon(R.drawable.coins)
                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                useCoins();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        loadUserCoins();
        //               Ads
        ImageView watchVideoBtn = view.findViewById(R.id.coinVideo);
        watchVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    if (RewardedVideoAd.isLoaded() && !watchedQuestionAd) {
                        RewardedVideoAd.show();
                    }
                } else {
                    QuizMainActivity.noInternetDialog(getContext());
                }
            }
        });
        //------------------close the app
        ImageView closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Leaving !")
                        .setMessage(R.string.leaveApp)
                        .setIcon(R.drawable.close)
                        .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(getContext(), QuizMainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        /*------------------------------------*/
        /*-----------------disable Helpers--------------------*/
        ImageView coinIcon = view.findViewById(R.id.coinIcon);
        if (questionLevel.equals("6") || questionLevel.equals("7")) {
            coinIcon.setVisibility(View.INVISIBLE);
            coinsGroup.setVisibility(View.INVISIBLE);
            coinsTv.setVisibility(View.INVISIBLE);
            watchVideoBtn.setVisibility(View.INVISIBLE);
        }
        /*-----------------------------------*/
        // set question choices
        if (questionType.equals("MCQ")) {
            displayChoices(view);
        } else if (questionType.equals("complete")) {
            displayCompleteFields(view);
        }
        // set timer to the question
        myCountDownTimer = new QuestionCountTimer(questionTime * 1000, 100);
        myCountDownTimer.create();

        addVisitedQuestion();
        return view;
    }

    private void displayCompleteFields(View view) {
        final LinearLayout answerContainer = view.findViewById(R.id.answersContainer);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 15, 0, 0);

        int i = 0;
        for (final Choice choice : choices_list) {
            // making the complete editText
            final EditText chET = new EditText(getContext());
            chET.setLayoutParams(params);
            chET.setSingleLine();
            chET.setHint(R.string.comAns);
            chET.setBackground(getResources().getDrawable(R.drawable.edit_text_style));
            chET.setId(i);
            if (i == 0)
                chET.requestFocus();
            chET.setTextColor(getResources().getColor(R.color.textFgColor));
            answerContainer.addView(chET);
            chET.setImeOptions(IME_ACTION_NEXT);
            i++;
        }
        final Button ansButton = new Button(getContext());
        ansButton.setLayoutParams(params);
        ansButton.setText(R.string.submitComplete);
        ansButton.setBackground(getResources().getDrawable(R.drawable.buttonshape));
        ansButton.setId(R.id.button);
        ansButton.setTextColor(getResources().getColor(R.color.textBgColor));
        answerContainer.addView(ansButton);
        ansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = 0;
                int check = 0;
                for (final Choice choice : choices_list) {
                    EditText editText = (EditText) answerContainer.getChildAt(i);
                    if (editText.getText().toString().equalsIgnoreCase(choice.getAnsTitle())) {
                        check++;
                    } else {
                        Toast.makeText(getActivity(), R.string.submitCompleteCheck, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    i++;
                }
                if (check == choices_list.size()) {
                    generateNextQuestion(questionNumber);
                } else
                    Toast.makeText(getActivity(), R.string.submitCompleteCheck, Toast.LENGTH_SHORT).show();
            }
        });

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
            chBtn.setBackground(getResources().getDrawable(R.drawable.button_default_style));
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
                        if (!pressed)
                            generateNextQuestion(questionNumber);
                    } else {
                        wrongAnswerEffect(actionBtn);
                        // show the right answer
                        rightAnswerEffect(rightAnswerBtn.get(0));
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                losing();
                            }
                        }, 2000);
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
        if (!pressed)
            button.setBackground(getResources().getDrawable(R.drawable.button_shape_wrong));
        pressed = true;
    }

    private void losing() {
        Fragment lose = EndFragment.newInstance(EndFragment.LOSE, score);
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, lose).commit();

    }

    private void winning() {
        Fragment lose = EndFragment.newInstance(EndFragment.SUCCESS, score);
        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, lose).commit();

    }

    // set the data and display
    private void generateNextQuestion(final int questionNumber) {
        try {
            myCountDownTimer.cancel();
            if (isOnline()) {
                int next_question = questionNumber + 1;
                String nScore = changeScore();
                Fragment nextFragment = QuestionFragment.newInstance(LoadData.getQuestions().remove(0), next_question, nScore);
                // load Interstitial Ad
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("interstitial", "The interstitial wasn't loaded yet.");
                }


                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragmentContainer, nextFragment).commit();
            } else {
                QuizMainActivity.noInternetDialog(getContext());
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("generateNextQuestion", e.getMessage());
            winning();
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
            questionTimeBar.setProgress(0);
            losing();
        }

    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(QuizMainActivity.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    /*---------Ads loaded-----------*/

    private void watchingVideoCoinsAds() {
        RewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewarded(RewardItem coins) {
                watchedQuestionAd = true;
                // Reward the user.
                addCoins(coins);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                myCountDownTimer.resume();
                Toast.makeText(getContext(), "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                myCountDownTimer.resume();
                QuizMainActivity.loadRewardedVideoAd();
//                Toast.makeText(getContext(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
//                Toast.makeText(getContext(), "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdLoaded() {
//                Toast.makeText(getContext(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                myCountDownTimer.pause();
//                Toast.makeText(getContext(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
//                Toast.makeText(getContext(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoCompleted() {
//                Toast.makeText(getContext(), "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
            }

        });

    }


    private void addCoins(final RewardItem coins) {
        Intent intent = getActivity().getIntent();
        final String id = intent.getStringExtra("user_id");
        final DatabaseReference coinDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/Coins");
        coinDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userCoins = (String) dataSnapshot.getValue();
                if (userCoins == null)
                    userCoins = "0";
                String calcCoins = String.valueOf(Integer.parseInt(userCoins) + coins.getAmount());
                coinDatabase.setValue(calcCoins);
                coinsTv.setText(calcCoins);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadUserCoins() {

        Intent intent = getActivity().getIntent();
        final String id = intent.getStringExtra("user_id");
        final DatabaseReference coinDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/Coins");
        coinDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userCoins = (String) dataSnapshot.getValue();
                if (userCoins != null)
                    coinsTv.setText(userCoins);
                else
                    coinsTv.setText("0");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void useCoins() {
        if (isOnline()) {
            Intent intent = getActivity().getIntent();
            final String id = intent.getStringExtra("user_id");
            final DatabaseReference coinDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/Coins");
            coinDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userCoins = (String) dataSnapshot.getValue();
                    if (userCoins != null) {
                        String useCoinsCalc = String.valueOf(Integer.parseInt(userCoins) - 10);
                        if (Integer.parseInt(userCoins) >= 10) {
                            generateNextQuestion(questionNumber);
                            coinDatabase.setValue(useCoinsCalc);
                            coinsTv.setText(useCoinsCalc);
                        } else {
                            if (RewardedVideoAd.isLoaded()) {
                                RewardedVideoAd.show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "You don't have any coins yet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            QuizMainActivity.noInternetDialog(getContext());
        }
    }

    private String changeScore() {
        Intent intent = getActivity().getIntent();
        final String id = intent.getStringExtra("user_id");
        final DatabaseReference scoreDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/MaxScore");
        int prevScore = Integer.parseInt(score);
        final int calcScoreTv = prevScore + 1;

        scoreDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String maxScore = (String) dataSnapshot.getValue();
                if (maxScore != null) {
                    Log.i("kskdkkskdkkdks", "" + maxScore + "  " + calcScoreTv);
                    int currentMaxScore = Integer.parseInt(maxScore);
                    if (currentMaxScore < calcScoreTv) {
                        scoreDatabase.setValue(String.valueOf(calcScoreTv));
                    }
                } else {
                    scoreDatabase.setValue("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return String.valueOf(calcScoreTv);
    }

}
