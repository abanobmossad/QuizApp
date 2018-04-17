package com.example.abano.quizyourbrain.Models;

public class Question {
    private String questionID;
    private int isActive;
    private String image;
    private String category;
    private String questionTitle;
    private String questionType;

    public Question(String questionID, String questionTitle, String category, String questionType, int isActive, String image) {
        this.questionID = questionID;
        this.isActive = isActive;
        this.category = category;
        this.image = image;
        this.questionType = questionType;
        this.questionTitle = questionTitle;

    }

    public String getQuestionType() {
        return questionType;
    }

    public String getImage() {
        return image;
    }

    public String getQuestionID() {
        return questionID;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }


}
