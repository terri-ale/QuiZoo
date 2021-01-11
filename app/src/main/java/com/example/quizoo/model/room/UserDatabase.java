package com.example.quizoo.model.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.quizoo.model.dao.UserDao;
import com.example.quizoo.model.entity.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserDao getUserDao();

    private volatile static UserDatabase INSTANCE;

    public static synchronized UserDatabase getDb(final Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, "dbuser.sqlite").build();
        }
        return INSTANCE;
    }


}
