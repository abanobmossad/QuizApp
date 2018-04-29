package com.example.abano.quizyourbrain;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.abano.quizyourbrain.Models.Choice;
import com.example.abano.quizyourbrain.Models.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class LoadData extends AsyncTask<Void, Integer, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ProgressDialog processDialog;
    private String userId;
    private ArrayList<Question> complete = new ArrayList<>();
    private ArrayList<Question> highLevel6 = new ArrayList<>();
    private ArrayList<Question> highLevel7 = new ArrayList<>();
    private static ArrayList<Question> questions = new ArrayList<>();

    LoadData(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        processDialog = new ProgressDialog(context);
        processDialog.setMessage("Loading Questions...");
        processDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        firebaseConnection();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        processDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        processDialog.dismiss();
        Toast.makeText(context, "Done retrieving the questions" + questions.size(), Toast.LENGTH_LONG).show();
    }

    private void firebaseConnection() {
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/addNewQuestion/en-US");
        final DatabaseReference visitedDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + userId + "/visitedQuestions");
        questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                visitedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot VisitedSnap) {
                        generateAllQuestions(dataSnapshot, VisitedSnap);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebaseQuestion", "Failed to read value.", error.toException());
            }
        });
    }

    private void generateAllQuestions(DataSnapshot dataSnapshot, DataSnapshot visitedQuestions) {
        int i = 0;
        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
            if (questions.size() == 7)
                break;
            Long id = (Long) questionSnapshot.child("id").getValue();
            if (visitedQuestions.hasChild(id.toString()))
                continue;
            ArrayList<Choice> questionChoices = new ArrayList<>();
            // get question data
            String question_title = (String) questionSnapshot.child("questionTitle").getValue();
            String question_category = (String) questionSnapshot.child("category").getValue();
            Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
            String question_type = (String) questionSnapshot.child("questionType").getValue();
            String question_Level = (String) questionSnapshot.child("level").getValue();
            if ((question_Level.equals("6") && highLevel6.size() == 1) || ((question_Level.equals("7")) && highLevel7.size() == 1))
                continue;
            Object __meta__ = questionSnapshot.child("__meta__").getValue();
            int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;

            // get choices data
            String choiceTitle1 = (String) questionSnapshot.child("choices").child("ansTitle1").getValue();
            int choiceIsRight1 = (boolean) questionSnapshot.child("choices").child("isRight1").getValue() ? 1 : 0;
            Choice choice1 = new Choice(id, choiceTitle1, choiceIsRight1);
            String choiceTitle2 = (String) questionSnapshot.child("choices").child("ansTitle2").getValue();
            int choiceIsRight2 = (boolean) questionSnapshot.child("choices").child("isRight2").getValue() ? 1 : 0;
            Choice choice2 = new Choice(id, choiceTitle2, choiceIsRight2);
            String choiceTitle3 = (String) questionSnapshot.child("choices").child("ansTitle3").getValue();
            int choiceIsRight3 = (boolean) questionSnapshot.child("choices").child("isRight3").getValue() ? 1 : 0;
            Choice choice3 = new Choice(id, choiceTitle3, choiceIsRight3);
            String choiceTitle4 = (String) questionSnapshot.child("choices").child("ansTitle4").getValue();
            int choiceIsRight4 = (boolean) questionSnapshot.child("choices").child("isRight4").getValue() ? 1 : 0;
            Choice choice4 = new Choice(id, choiceTitle4, choiceIsRight4);
            if (choiceTitle1 != null)
                questionChoices.add(choice1);
            if (choiceTitle2 != null)
                questionChoices.add(choice2);
            if (choiceTitle3 != null)
                questionChoices.add(choice3);
            if (choiceTitle4 != null)
                questionChoices.add(choice4);
            Question question = new Question(__meta__, id, question_title, question_category, question_type, question_Level, 10, isActive, imageID, questionChoices);
            orderQuestions(question);
            publishProgress(i);
            i++;
        }
        questions.addAll(complete);
        questions.addAll(highLevel6);
        questions.addAll(highLevel7);
    }

    private void orderQuestions(Question question) {

        if (question.getIsActive() == 1) {
            if (question.getQuestionType().equals("MCQ") && question.getQuestionLevel().equals("normal")) {
                questions.add(question);
            } else if (question.getQuestionType().equals("complete") && question.getQuestionLevel().equals("normal")) {
                complete.add(question);
            } else if (question.getQuestionLevel().equals("6")) {
                highLevel6.add(question);
            } else if (question.getQuestionLevel().equals("7")) {
                highLevel7.add(question);
            }
        }
    }

    public static ArrayList<Question> getQuestions() {
        return questions;
    }
}


