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
    /**
     * Counts the number of cheeses in the table.
     *
     * @return The number of cheeses.
     */
    @Query("SELECT COUNT(*) FROM " + Person.TABLE_NAME)
    int count();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPerson(@NonNull Person person);

    @Query("SELECT * FROM " + Person.TABLE_NAME)
    List<Person> getAllPersons();

    @Query("SELECT * FROM " + Person.TABLE_NAME)
    List<ProjectionPersonName> getAllName();
}
