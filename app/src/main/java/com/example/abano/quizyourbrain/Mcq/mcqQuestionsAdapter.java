package com.example.abano.quizyourbrain.Mcq;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abano.quizyourbrain.QuizMainActivity;
import com.example.abano.quizyourbrain.R;

import java.util.ArrayList;


public class mcqQuestionsAdapter extends BaseAdapter {
    public Context context;
    public int layout;
    public TextView score;
    public ArrayList<mcqAnswersData> ansData;
    public FragmentManager manager ;
    Fragment next_fragment;

    public mcqQuestionsAdapter(Fragment next_fragment,FragmentManager manager,Context context, TextView score, int layout, ArrayList<mcqAnswersData> ansData) {
        this.context = context;
        this.layout = layout;
        this.ansData = ansData;
        this.score = score;
        this.manager = manager;
        this.next_fragment=next_fragment;
    }

    @Override
    public int getCount() {
        return ansData.size();
    }

    @Override
    public Object getItem(int i) {
        return ansData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder") View v = LayoutInflater.from(context).inflate(layout, viewGroup, false);

        Button anserBtn = (Button) v.findViewById(R.id.ans);
        String title = ansData.get(i).getAnsTitle();
        final int isRight = ansData.get(i).getIsRight();
        // add category


        anserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(view, isRight);
            }
        });
        anserBtn.setText(title);

        return v;
    }

    private void checkAnswer(View view, int isRight) {
        manager.beginTransaction().replace(R.id.main_container, next_fragment).addToBackStack(null).commit();

        if (isRight==1) {
            Toast.makeText(context, "is right", Toast.LENGTH_LONG).show();
            int passScore = Integer.parseInt(String.valueOf(score.getText()));
            passScore++;
            String theScore = String.valueOf(passScore);
            score.setText(theScore);
        } else {
            Toast.makeText(context, "is Wrong", Toast.LENGTH_LONG).show();

        }

    }
}
