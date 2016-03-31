package com.example.tomipc.foodzye.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.loginActivity;

import butterknife.Bind;

public class LoginFragment extends Fragment  {

    Activity activity;

    @Bind(R.id.btn_login)
    Button _loginButton;
    public LoginFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = getView();

        Intent intent = new Intent(getActivity(), loginActivity.class);
        startActivity(intent);


        return rootView;
    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.activity = activity;
    }
}