package com.example.democipherroom.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.democipherroom.db.dao.PersonDao;
import com.example.democipherroom.db.entry.Person;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import timber.log.Timber;

@Database(entities = {Person.class}, version = 1)
public abstract class DemoDB extends RoomDatabase {
    private static final String PASSPHRASE = "123456";
    private static final String DB_NAME = "myDemoDB";
    private static DemoDB mDemoDB;

    public abstract PersonDao getPersonDao();

    /**
     * Call this method to create an database or open the existed database.
     *
     * @param context a context.
     */
    public static synchronized void init(@NonNull Context context) {
        if (mDemoDB == null) {
            final byte[] passphrase = SQLiteDatabase.getBytes(PASSPHRASE.toCharArray());
            final SupportFactory factory = new SupportFactory(passphrase);
            mDemoDB = Room.databaseBuilder(context.getApplicationContext(), DemoDB.class, DB_NAME)
                    .openHelperFactory(factory)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            Timber.d("onCreate");
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            Timber.d("onOpen");
                        }

                        @Override
                        public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                            Timber.d("onDestructiveMigration");
                        }
                    })
                    .build();
        }
    }

    /**
     * Get the initialized database instance.
     *
     * @return the database instance
     */
    public static synchronized DemoDB getInstance() {
        if (mDemoDB == null) {
            throw new NullPointerException("database must be initialized first by calling " +
                    "RoomDatabase.init() ");
        }

        return mDemoDB;
    }
}
