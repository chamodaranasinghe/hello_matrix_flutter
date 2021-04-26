package com.hello.hello_matrix_flutter.src.directory;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = UserProfile.class, version = 1)
public abstract class DirectoryDatabase extends RoomDatabase {
    public abstract UserProfileDao userProfileDao();
}
