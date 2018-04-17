package com.example.abano.quizyourbrain.Models;

public class Choice {
    private String ansTitle;
    private int isRight;
    private int qustionId;
    private int choiceId;

    public Choice(int choicId, int qustionId, String ansTitle, int isRight) {
        this.ansTitle = ansTitle;
        this.isRight = isRight;
        this.qustionId = qustionId;
        this.choiceId = choicId;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public int getQustionId() {
        return qustionId;
    }

    public String getAnsTitle() {
        return ansTitle;

    }

    public int getIsRight() {
        return isRight;
    }
}
