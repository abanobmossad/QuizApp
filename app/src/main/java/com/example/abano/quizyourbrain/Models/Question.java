package com.example.abano.quizyourbrain.Models;

public class Question {
    private Long id;
    private int isActive;
    private String image;
    private String category;
    private String questionTitle;
    private String questionType;
    private Object __meta__;

    public Question(Object __meta__, Long id, String questionTitle, String category, String questionType, int isActive, String image) {
        this.id = id;
        this.isActive = isActive;
        this.category = category;
        this.image = image;
        this.__meta__ = __meta__;
        this.questionType = questionType;
        this.questionTitle = questionTitle;

    }

    public Long getId() {
        return id;
    }

    public Object get__meta__() {
        return __meta__;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getImage() {
        return image;
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
