package com.example.abano.quizyourbrain.End_UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.R;
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
                    Toast.makeText(getActivity(), "Please wait the share dialog is loading...", Toast.LENGTH_LONG).show();
                    shareBtnActivation();
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_win, container, false);
            Button button = view.findViewById(R.id.playBtn);
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
                    Toast.makeText(getActivity(), "Please wait the share dialog is loading...", Toast.LENGTH_LONG).show();
                    shareBtnActivation();
                }
            });
        }


        return view;
    }


    private void shareBtnActivation() {
        String APP_NAME = "com.example.abano.quizyourbrain";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String appLink = "https://play.google.com/store/apps/details?id=" + APP_NAME + "  \n\n";
        String shareBody = getResources().getString(R.string.shareBody) + " \"" + score + " \" \n\n" + appLink + " You can win too";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quiz Game");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
