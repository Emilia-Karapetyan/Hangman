package com.example.hangman;

import java.util.List;

public class MyListData {
    private List<String> description;
    private List<String> num;
    private List<String> userScore;

    public MyListData(List<String> description, List<String> num, List<String> userScore) {
        this.description = description;
        this.num = num;
        this.userScore = userScore;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<String> getNum() {
        return num;
    }

    public void setNum(List<String> num) {
        this.num = num;
    }

    public List<String> getUserScore() {
        return userScore;
    }

    public void setUserScore(List<String> userScore) {
        this.userScore = userScore;
    }
}