package com.example.quizoo.view.adapter;

import com.example.quizoo.rest.pojo.Card;

public interface OnCardClickListener {
    public void onShowQuestionsClick(Card card);
    public void onEditCardClick(Card card);
}
