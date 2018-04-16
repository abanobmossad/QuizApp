package com.example.abano.quizyourbrain.Mcq;

public class mcqAnswersData {
    private String ansTitle;
    private int isRight;

    public  mcqAnswersData(String ansTitle, int isRight) {
        this.ansTitle = ansTitle;
        this.isRight = isRight;
    }


    public String getAnsTitle() {
        return ansTitle;

    }

    public int getIsRight() {
        return isRight;
    }
}
