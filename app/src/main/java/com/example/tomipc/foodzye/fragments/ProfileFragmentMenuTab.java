package com.example.tomipc.foodzye.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomipc.foodzye.R;


public class ProfileFragmentMenuTab extends Fragment {
    public static ProfileFragmentMenuTab newInstance() {
        return new ProfileFragmentMenuTab();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_profile_menu,null);

        return view;
    }
}
