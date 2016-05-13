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
        userLocalDatabaseEditor.putInt("id", user.getId());
        userLocalDatabaseEditor.putString("slug", user.getSlug());
        userLocalDatabaseEditor.putString("username", user.getUsername());
        userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putInt("role", user.getRole());
        userLocalDatabaseEditor.putInt("premium", user.getPremium());
        userLocalDatabaseEditor.putInt("type", user.getType());
        userLocalDatabaseEditor.putString("premium_until", user.getPremium_until());
        userLocalDatabaseEditor.putString("location", user.getLocation());
        userLocalDatabaseEditor.putString("phone", user.getPhone());
        userLocalDatabaseEditor.putString("work_time", user.getWork_time());
        userLocalDatabaseEditor.putString("user_picture", user.getPicture());
        userLocalDatabaseEditor.putString("description", user.getDescription());
        userLocalDatabaseEditor.putFloat("rate_total", (float)user.getRate());
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
        int role = userLocalDatabase.getInt("role", 1);
        int premium = userLocalDatabase.getInt("premium", 0);
        int type = userLocalDatabase.getInt("type", 1);
        String premium_until = userLocalDatabase.getString("premium_until", "0");


        String location = userLocalDatabase.getString("location", "");
        String phone = userLocalDatabase.getString("phone", "");
        String work_time = userLocalDatabase.getString("work_time", "");
        String user_picture = userLocalDatabase.getString("user_picture", "");
        String description = userLocalDatabase.getString("description", "");
        float rate = userLocalDatabase.getFloat("rate_total", 0);

        User user = new User(id, username, slug, email, role, location, phone, work_time, user_picture, description, rate, premium, premium_until, type);
        return user;
    }

}
