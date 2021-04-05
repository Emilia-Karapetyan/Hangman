package com.example.hangman;

import java.util.ArrayList;

public class Word {

    private int id;
    private String words;
    private int categoryId;
    private String hintText;
    private String hintImg;
    private String hintAudio;

    public Word(int id, String words, int categoryId, String hintText, String hintImg, String hintAudio) {
        this.id = id;
        this.words = words;
        this.categoryId = categoryId;
        this.hintText = hintText;
        this.hintImg = hintImg;
        this.hintAudio = hintAudio;
    }

    public Word() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getHintImg() {
        return hintImg;
    }

    public void setHintImg(String hintImg) {
        this.hintImg = hintImg;
    }

    public String getHintAudio() {
        return hintAudio;
    }

    public void setHintAudio(String hintAudio) {
        this.hintAudio = hintAudio;
    }


    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", words='" + words + '\'' +
                ", categoryId=" + categoryId +
                ", hintText='" + hintText + '\'' +
                ", hintImg='" + hintImg + '\'' +
                ", hintAudio='" + hintAudio + '\'' +
                '}';
    }
}
