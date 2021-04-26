package com.hello.hello_matrix_flutter.src.directory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kotlin.jvm.JvmSuppressWildcards;

@Dao
public interface UserProfileDao {
    @Query("SELECT * FROM user_profile ORDER BY first_name ASC")
    List<UserProfile> getAll();

    @Query("SELECT * FROM user_profile WHERE hello_id IN (:helloIds) ORDER BY first_name ASC")
    List<UserProfile> loadAllByHelloIds(String[] helloIds);

    @Query("SELECT * FROM user_profile WHERE hello_id = :helloId LIMIT 1 ")
    UserProfile loadByHelloId(String[] helloId);

    //query should be like %abc%
    @Query("SELECT * FROM user_profile WHERE (first_name LIKE :query OR last_name LIKE :query OR email LIKE :query) ORDER BY first_name ASC")
    List<UserProfile> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    void insertAll(List<UserProfile> userProfiles);

    @Delete
    void delete(UserProfile userProfile);

    @Query("DELETE FROM user_profile")
    public void deleteAll();
}
