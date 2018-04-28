package com.example.abano.quizyourbrain.Models;

import java.util.ArrayList;

public class Question {
    private Long id;
    private int isActive;
    private Long image;
    private String category;
    private String questionTitle;
    private String questionType;
    private String questionLevel;
    private int time;
    private Object __meta__;
    private ArrayList<Choice> choice;

    public Question(Object __meta__, Long id, String questionTitle, String category, String questionType,String questionLevel, int time, int isActive, Long image,ArrayList<Choice> choice) {
        this.id = id;
        this.isActive = isActive;
        this.category = category;
        this.image = image;
        this.__meta__ = __meta__;
        this.questionType = questionType;
        this.questionTitle = questionTitle;
        this.choice=choice;
        this.questionLevel = questionLevel;
        this.time = time;

    }

    public String getQuestionLevel() {
        return questionLevel;
    }

    public int getTime() {
        return time;
    }

    public ArrayList<Choice> getChoices() {
        return choice;
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

    public Long getImage() {
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
