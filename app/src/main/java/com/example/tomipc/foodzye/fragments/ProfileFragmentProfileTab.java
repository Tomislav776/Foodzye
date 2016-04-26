package com.example.tomipc.foodzye.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomipc.foodzye.ProfileActivity;
import com.example.tomipc.foodzye.R;

public class ProfileFragmentProfileTab extends Fragment {
    public static ProfileFragmentProfileTab newInstance() {
        return new ProfileFragmentProfileTab();
    }

    int user_role;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        ProfileActivity activity = (ProfileActivity) getActivity();
        user_role = activity.getUserRole();
        if(user_role == 1){
            view = inflater.inflate(R.layout.fragments_profile_user, null);
        }else{
            view = inflater.inflate(R.layout.fragments_profile_food_service_provider, null);
        }

        return view;
    }
}
