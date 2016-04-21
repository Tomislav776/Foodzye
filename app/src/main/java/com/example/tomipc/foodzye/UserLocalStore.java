package com.example.tomipc.foodzye;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tomipc.foodzye.model.User;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putInt("id", user.id);
        userLocalDatabaseEditor.putString("slug", user.slug);
        userLocalDatabaseEditor.putString("username", user.username);
        userLocalDatabaseEditor.putString("email", user.email);
        userLocalDatabaseEditor.putString("role", user.role);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        int id = userLocalDatabase.getInt("id", 0);
        String username = userLocalDatabase.getString("username", "");
        String slug = userLocalDatabase.getString("slug", "");
        String email = userLocalDatabase.getString("email", "");
        String role = userLocalDatabase.getString("role", "");

        User user = new User(id, username, slug, email, role);
        return user;
    }

}
