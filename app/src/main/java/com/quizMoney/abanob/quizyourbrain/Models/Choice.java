package com.quizMoney.abanob.quizyourbrain.Models;

import android.support.design.internal.ParcelableSparseArray;

public class Choice extends ParcelableSparseArray{
    private String ansTitle;
    private int isRight;
    private Long qustionId;

    public Choice(Long qustionId, String ansTitle, int isRight) {
        this.ansTitle = ansTitle;
        this.isRight = isRight;
        this.qustionId = qustionId;
    }


    public Long getQustionId() {
        return qustionId;
    }

    public String getAnsTitle() {
        return ansTitle;

    }

    public int getIsRight() {
        return isRight;
    }
}
