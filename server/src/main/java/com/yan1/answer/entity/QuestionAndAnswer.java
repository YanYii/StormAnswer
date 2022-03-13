package com.yan1.answer.entity;


import lombok.Data;

@Data
public class QuestionAndAnswer {
    private String question;
    private String answer;

    public QuestionAndAnswer() {
    }

    public QuestionAndAnswer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
