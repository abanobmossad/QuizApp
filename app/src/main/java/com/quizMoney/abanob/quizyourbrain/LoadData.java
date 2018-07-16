package com.quizMoney.abanob.quizyourbrain;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.quizMoney.abanob.quizyourbrain.Models.Choice;
import com.quizMoney.abanob.quizyourbrain.Models.Question;
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
    private ArrayList<Question> mcq = new ArrayList<>();
    private static ArrayList<Question> questions = new ArrayList<>();
    private int number_mcq = 3;
    private int number_complete = 2;
    private int number_level6 = 1;
    private int number_level7 = 1;

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
        McqFirebaseConnection();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        processDialog.setProgress(values[0]);
    }

    private void McqFirebaseConnection() {
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/addNewQuestion/en-US");
        final DatabaseReference visitedDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + userId + "/visitedQuestions/MCQ");
        questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                visitedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot VisitedSnap) {
                        generateAllMcqQuestions(dataSnapshot, VisitedSnap);
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

    private void generateAllMcqQuestions(DataSnapshot dataSnapshot, DataSnapshot visitedQuestions) {
        int i = 0;
        Long visitedCount =visitedQuestions.getChildrenCount();
        Long dataCount =dataSnapshot.getChildrenCount();
        Long displayCount =dataCount-visitedCount;
        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
            if (mcq.size() >= number_mcq)
                break;
            Long id = (Long) questionSnapshot.child("id").getValue();
            if ((visitedQuestions.hasChild(id.toString())) && !visitedCount.equals(dataCount) && displayCount>=number_mcq)
                continue;
            ArrayList<Choice> questionChoices = new ArrayList<>();
            // get question data
            String question_title = (String) questionSnapshot.child("questionTitle").getValue();
            String question_category = (String) questionSnapshot.child("category").getValue();
            Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
            String question_type = "MCQ";
            String question_Level = "normal";
            int questionTime=((Long) questionSnapshot.child("time").getValue()).intValue();
            Object __meta__ = questionSnapshot.child("__meta__").getValue();
            int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;
            Log.d("Array_sizes", question_title + "");

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
            Question question = new Question(__meta__, id, question_title, question_category, question_type, question_Level, questionTime, isActive, imageID, questionChoices);
            if (isActive == 1)
                mcq.add(question);
            publishProgress(i);
            i++;
        }
        questions.addAll(mcq);
        completeFirebaseConnection();
    }

    private void completeFirebaseConnection() {
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/completeQuestions/en-US");
        final DatabaseReference visitedDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + userId + "/visitedQuestions/complete");
        questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                visitedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot VisitedSnap) {
                        generateAllCompleteQuestions(dataSnapshot, VisitedSnap);
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

    private void generateAllCompleteQuestions(DataSnapshot dataSnapshot, DataSnapshot visitedQuestions) {
        int i = 0;
        Long visitedCount =visitedQuestions.getChildrenCount();
        Long dataCount =dataSnapshot.getChildrenCount();
        Long displayCount =dataCount-visitedCount;
        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
            if (complete.size() >= number_complete)
                break;
            Long id = (Long) questionSnapshot.child("id").getValue();
            if ((visitedQuestions.hasChild(id.toString())) && !visitedCount.equals(dataCount) && displayCount>=number_complete)
                continue;
            ArrayList<Choice> questionChoices = new ArrayList<>();
            // get question data
            String question_title = (String) questionSnapshot.child("questionTitle").getValue();
            String question_category = (String) questionSnapshot.child("category").getValue();
            Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
            String question_type = "complete";
            String question_Level = "normal";
            int questionTime=((Long) questionSnapshot.child("time").getValue()).intValue();
            Object __meta__ = questionSnapshot.child("__meta__").getValue();
            int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;
            Log.d("Array_sizes", question_title + "");

            // get choices data
            String choiceTitle1 = (String) questionSnapshot.child("choices").child("ansTitle1").getValue();
            int choiceIsRight1 = 1;
            Choice choice1 = new Choice(id, choiceTitle1, choiceIsRight1);
            String choiceTitle2 = (String) questionSnapshot.child("choices").child("ansTitle2").getValue();
            int choiceIsRight2 = 1;
            Choice choice2 = new Choice(id, choiceTitle2, choiceIsRight2);
            String choiceTitle3 = (String) questionSnapshot.child("choices").child("ansTitle3").getValue();
            int choiceIsRight3 = 1;
            Choice choice3 = new Choice(id, choiceTitle3, choiceIsRight3);
            String choiceTitle4 = (String) questionSnapshot.child("choices").child("ansTitle4").getValue();
            int choiceIsRight4 = 1;
            Choice choice4 = new Choice(id, choiceTitle4, choiceIsRight4);
            if (choiceTitle1 != null)
                questionChoices.add(choice1);
            if (choiceTitle2 != null)
                questionChoices.add(choice2);
            if (choiceTitle3 != null)
                questionChoices.add(choice3);
            if (choiceTitle4 != null)
                questionChoices.add(choice4);
            Question question = new Question(__meta__, id, question_title, question_category, question_type, question_Level, questionTime, isActive, imageID, questionChoices);
            if (isActive == 1)
                complete.add(question);
            publishProgress(i);
            i++;
        }
        questions.addAll(complete);
        level6FirebaseConnection();

    }

    private void level6FirebaseConnection() {
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/level6Questions/en-US");
        final DatabaseReference visitedDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + userId + "/visitedQuestions/level6");
        questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                visitedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot VisitedSnap) {
                        generateAllLevel6Questions(dataSnapshot, VisitedSnap);
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

    private void generateAllLevel6Questions(DataSnapshot dataSnapshot, DataSnapshot visitedQuestions) {
        int i = 0;
        Long visitedCount =visitedQuestions.getChildrenCount();
        Long dataCount =dataSnapshot.getChildrenCount();
        Long displayCount =dataCount-visitedCount;
        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
            if (highLevel6.size() >= number_level6)
                break;
            Long id = (Long) questionSnapshot.child("id").getValue();
            if ((visitedQuestions.hasChild(id.toString())) && !visitedCount.equals(dataCount) && displayCount>=number_level6)
                continue;
            ArrayList<Choice> questionChoices = new ArrayList<>();
            // get question data
            String question_title = (String) questionSnapshot.child("questionTitle").getValue();
            String question_category = (String) questionSnapshot.child("category").getValue();
            Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
            String question_type = (String) questionSnapshot.child("questionType").getValue();
            String question_Level = "6";
            Object __meta__ = questionSnapshot.child("__meta__").getValue();
            int questionTime=((Long) questionSnapshot.child("time").getValue()).intValue();
            int isActive = (boolean) questionSnapshot.child("isActive").getValue() ? 1 : 0;
            Log.d("Array_sizes", question_title + "");

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
            Question question = new Question(__meta__, id, question_title, question_category, question_type, question_Level, questionTime, isActive, imageID, questionChoices);
            if (isActive == 1)
                highLevel6.add(question);
            publishProgress(i);
            i++;
        }
        questions.addAll(highLevel6);
        level7FirebaseConnection();
    }

    private void level7FirebaseConnection() {
        DatabaseReference questionsDatabase = FirebaseDatabase.getInstance().getReference("flamelink/environments/production/content/level7Questions/en-US");
        final DatabaseReference visitedDatabase = FirebaseDatabase.getInstance().getReference("flamelink/Users/" + userId + "/visitedQuestions/level7");
        questionsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                visitedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot VisitedSnap) {
                        generateAllLevel7Questions(dataSnapshot, VisitedSnap);
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

    private void generateAllLevel7Questions(DataSnapshot dataSnapshot, DataSnapshot visitedQuestions) {
        int i = 0;
        Long visitedCount =visitedQuestions.getChildrenCount();
        Long dataCount =dataSnapshot.getChildrenCount();
        Long displayCount =dataCount-visitedCount;
        for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
            if (highLevel7.size() >= number_level7)
                break;
            Long id = (Long) questionSnapshot.child("id").getValue();
            if ((visitedQuestions.hasChild(id.toString())) && !visitedCount.equals(dataCount)  && displayCount>=number_level7)
                continue;
            ArrayList<Choice> questionChoices = new ArrayList<>();
            // get question data
            String question_title = (String) questionSnapshot.child("questionTitle").getValue();
            String question_category = (String) questionSnapshot.child("category").getValue();
            Long imageID = (Long) questionSnapshot.child("image").child("0").getValue();
            String question_type = (String) questionSnapshot.child("questionType").getValue();
            int questionTime=((Long) questionSnapshot.child("time").getValue()).intValue();
            String question_Level = "7";
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
            Question question = new Question(__meta__, id, question_title, question_category, question_type, question_Level, questionTime, isActive, imageID, questionChoices);
            if (isActive == 1)
                highLevel7.add(question);
            publishProgress(i);
            i++;
        }
        questions.addAll(highLevel7);
        try {
            processDialog.dismiss();
        }catch (Exception ignored){}
    }

    public static ArrayList<Question> getQuestions() {
        return questions;
    }
}


