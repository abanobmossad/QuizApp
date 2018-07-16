package com.quizMoney.abanob.quizyourbrain.End_UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quizMoney.abanob.quizyourbrain.Mailing;
import com.quizMoney.abanob.quizyourbrain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EndFragment extends Fragment {

    private static final String END_MOOD = "END_MOOD";
    private static final String SCORE = "SCORE";
    public static String SUCCESS = "SUCCESS";
    public static String LOSE = "LOSE";
    private String endMood;
    private String score;
    public static Toast mToast;

    public EndFragment() {
        // Required empty public constructor
    }


    public static EndFragment newInstance(String endMood, String score) {
        EndFragment fragment = new EndFragment();
        Bundle args = new Bundle();
        args.putString(END_MOOD, endMood);
        args.putString(SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            endMood = getArguments().getString(END_MOOD);
            score = getArguments().getString(SCORE);
        }
    }

    private TextView maxScore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lose, container, false);
        maxScore = view.findViewById(R.id.maxScore);
        if (endMood.equals(LOSE)) {
            ImageView shareBtn = view.findViewById(R.id.share_btn);
            Button button = view.findViewById(R.id.playBtn);
            TextView scoreTv = view.findViewById(R.id.scoreE);
            String showScore = getResources().getString(R.string.score) + " :" + score;
            scoreTv.setText(showScore);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            loadUserMaxScore();


            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast=Toast.makeText(getActivity(), "Please wait the share dialog is loading...", Toast.LENGTH_LONG);
                    mToast.show();
                    shareBtnActivation();
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_win, container, false);
            Button button = view.findViewById(R.id.playBtn);
            final Button sendEmail = view.findViewById(R.id.sendMail);
            final EditText phoneNumber = view.findViewById(R.id.phoneNumber);
            final TextView sendInfo = view.findViewById(R.id.winNotify);
            ImageView shareBtn = view.findViewById(R.id.share_btn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    mToast=Toast.makeText(getActivity(), "Please wait the share dialog is loading...", Toast.LENGTH_LONG);
                    mToast.show();
                    shareBtnActivation();
                }
            });
            sendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendEmail(phoneNumber, sendInfo, sendEmail);
                }
            });
        }
        return view;
    }


    private void shareBtnActivation() {
        String APP_NAME = "com.quizMoney.abanob.quizyourbrain";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String appLink = "https://play.google.com/store/apps/details?id=" + APP_NAME + "  \n\n";
        String shareBody = getResources().getString(R.string.shareBody) + " \"" + score + " \" \n\n" + appLink + " You can win too";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quiz Game");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void sendEmail(EditText phoneNumber, TextView sendInfo, Button ok) {
        String number = phoneNumber.getText().toString();
        String username = "quizchallenge47@gmail.com";//change accordingly
        String password = "quizchallenge";//change accordingly
        String from = "quizchallenge47@gmail.com";
        String to = "quizchallenge47@gmail.com";
        String subject = getResources().getString(R.string.mailSubjectRewarded);

        if (phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter your phone number!!", Toast.LENGTH_SHORT).show();
        } else {
            if (numberValidation(number)) {
                String num = PhoneNumberUtils.formatNumber(number);
                String messageBody = getResources().getString(R.string.mailMessageRewarded) + " " + num;
                Mailing sendMail = new Mailing(getContext(), from, to, username, password, subject, messageBody);
                sendMail.sendEmail();
                sendInfo.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
            } else {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast=Toast.makeText(getActivity(), "Please enter a valid phone number", Toast.LENGTH_LONG);
                mToast.show();
            }
        }
    }

    private boolean numberValidation(String number) {
        return (number.substring(0, 3).equals("012") || number.substring(0, 3).equals("011") || number.substring(0, 3).equals("010")) && number.length() == 11;
    }

    private void loadUserMaxScore() {
        Intent intent = getActivity().getIntent();
        final String id = intent.getStringExtra("user_id");
        final DatabaseReference coinDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + id + "/MaxScore");
        coinDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userCoins = (String) dataSnapshot.getValue();
                if (userCoins != null)
                    maxScore.setText(userCoins);
                else
                    maxScore.setText("0");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

