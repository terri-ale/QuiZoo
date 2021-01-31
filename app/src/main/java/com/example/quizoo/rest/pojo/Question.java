package com.example.quizoo.rest.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private long id;

    @SerializedName("card_id")
    @Expose
    private long cardId;

    @SerializedName("question_text")
    @Expose
    private String text;

    @SerializedName("right_answer")
    @Expose
    private String rightAnswer;


    @SerializedName("wrong_answer1")
    @Expose
    private String wrongAnswer1;


    @SerializedName("wrong_answer2")
    @Expose
    private String wrongAnswer2;


    @SerializedName("wrong_answer3")
    @Expose
    private String wrongAnswer3;


    public Question(){ }

    public Question(long cardId, String text, String rightAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
        this.cardId = cardId;
        this.text = text;
        this.rightAnswer = rightAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }

    public Question(long cardId, String text, String rightAnswer, String wrongAnswer1) {
        this.cardId = cardId;
        this.text = text;
        this.rightAnswer = rightAnswer;
        this.wrongAnswer1 = wrongAnswer1;
    }

    public boolean checkAnswer(String answer){
        return answer.equalsIgnoreCase(this.rightAnswer);
    }

    public List<String> getShuffledAnswers(){
        List<String> answers = new ArrayList<>();
        answers.add(rightAnswer);
        answers.add(wrongAnswer1);
        if(wrongAnswer2 != null) { answers.add(wrongAnswer2); }
        if(wrongAnswer3 != null) { answers.add(wrongAnswer3); }
        Collections.shuffle(answers);
        return answers;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }


    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", cardId=" + cardId +
                ", text='" + text + '\'' +
                ", rightAnswer='" + rightAnswer + '\'' +
                ", wrongAnswer1='" + wrongAnswer1 + '\'' +
                ", wrongAnswer2='" + wrongAnswer2 + '\'' +
                ", wrongAnswer3='" + wrongAnswer3 + '\'' +
                '}';
    }
}
