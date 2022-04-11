package com.example.democipherroom.db.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.democipherroom.db.entry.Person;
import com.example.democipherroom.db.entry.ProjectionPersonName;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPerson(@NonNull Person person);

    @Query("SELECT * FROM TB_person")
    List<Person> getAllPersons();

    @Query("SELECT * FROM TB_person")
    List<ProjectionPersonName> getAllName();
}
