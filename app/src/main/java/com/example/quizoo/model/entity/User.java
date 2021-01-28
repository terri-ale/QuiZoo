package com.example.quizoo.model.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "avatar")
    private int avatar;

    @Nullable
    @ColumnInfo(name = "numResponses")
    private int numResponses;

    @Nullable
    @ColumnInfo(name = "numResponsesCorrect")
    private int numResponsesCorrect;


    public User(@NonNull String name, int avatar, int numResponses, int numResponsesCorrect) {
        this.name = name;
        this.avatar = avatar;
        this.numResponses = numResponses;
        this.numResponsesCorrect = numResponsesCorrect;
    }
    @Ignore
    public User() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public int getNumResponses() {
        return numResponses;
    }

    public void setNumResponses(int numResponses) {
        this.numResponses = numResponses;
    }

    public int getNumResponsesCorrect() {
        return numResponsesCorrect;
    }

    public void setNumResponsesCorrect(int numResponsesCorrect) {
        this.numResponsesCorrect = numResponsesCorrect;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", avatar=" + avatar +
                ", numResponses=" + numResponses +
                ", numResponsesCorrect=" + numResponsesCorrect +
                '}';
    }
}
