package com.example.quizoo.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizoo.model.entity.User;

import java.util.List;
@Dao
public interface UserDao {

    @Update
    int update(User user);

    @Insert
    long insert(User user);

    @Query("select * from users")
    LiveData<List<User>> getAll();

    @Query("delete from users where id = :id")
    void delete(long id);

    @Query("select numResponsesCorrect from users where id = :id")
    LiveData<Integer> getUserLiveScore(long id);

    @Query("update users set numResponsesCorrect = numResponsesCorrect + 1 where id = :id")
    void sumUserScore(long id);


}
