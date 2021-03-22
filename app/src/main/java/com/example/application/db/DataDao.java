package com.example.application.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.application.model.User;


import java.util.List;

@Dao
public interface DataDao
{
    @Query("SELECT * FROM avtartable")
    List<User> getAll();

    @Insert
    void insert(User data);

    @Query("DELETE FROM avtartable")
    void deleteAll();

}
