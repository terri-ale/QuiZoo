package com.example.quizoo.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizoo.model.entity.User;

import java.util.List;

public interface UserDao {

    @Update
    int update(User user);

    @Insert
    long insert(User user);

    @Query("select * from users")
    LiveData<List<User>> getAll();


}
