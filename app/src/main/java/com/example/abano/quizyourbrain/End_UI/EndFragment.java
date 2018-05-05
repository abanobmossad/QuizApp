package com.example.abano.quizyourbrain.End_UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.abano.quizyourbrain.R;

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
        getActivity().stopService(new Intent(getContext(), SoundService.class));
        if (getArguments() != null) {
            endMood = getArguments().getString(END_MOOD);
            score = getArguments().getString(SCORE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lose, container, false);
        if (endMood.equals(LOSE)) {
            Button button = view.findViewById(R.id.playBtn);
            TextView scoreTv = view.findViewById(R.id.scoreE);
            String showScore = getResources().getString(R.string.score)+" :"+ score;
            scoreTv.setText(showScore);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        } else {
            view = inflater.inflate(R.layout.fragment_win, container, false);
            Button button = view.findViewById(R.id.playBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                    assert i != null;
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }

        return view;
    }

}
