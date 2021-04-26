package com.hello.hello_matrix_flutter.src.directory;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DirectoryInstance {
    static String _tag = "DirectoryInstance";
    private static DirectoryInstance INSTANCE = null;

    // other instance variables can be here
    List<UserProfile> directoryList = new ArrayList<>();

    private DirectoryInstance() {
    }

    public static DirectoryInstance getInstance() {
        if (INSTANCE == null) {
            Log.i(_tag, "new instance created");
            INSTANCE = new DirectoryInstance();
        }
        return (INSTANCE);
    }

    // other instance methods can follow
    public List<UserProfile> getDirectoryList() {
        if (directoryList.isEmpty()) {
            UserProfileDao userProfileDao = new DirectoryController().initDB().userProfileDao();
            this.directoryList = userProfileDao.getAll();
        }
        return directoryList;
    }

    public void setDirectoryList(List<UserProfile> directoryList) {
        this.directoryList = directoryList;
    }
}
