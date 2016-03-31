package com.example.tomipc.foodzye.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.TabHost;

import com.example.tomipc.foodzye.R;

public class FoodFragment extends Fragment {

    private TabHost mTabHost;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_food, container, false);

        mTabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        mTabHost.setup();

        TabHost.TabSpec spec = mTabHost.newTabSpec("tag");
        spec.setIndicator("Android");
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        mTabHost.addTab(spec);
        spec = mTabHost.newTabSpec("tag1");
        spec.setIndicator("java");
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        mTabHost.addTab(spec);
        spec = mTabHost.newTabSpec("tag2");
        spec.setIndicator("Favourate");
        spec.setContent(new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {
                // TODO Auto-generated method stub
                return (new AnalogClock(getActivity()));
            }
        });
        mTabHost.addTab(spec);


        return rootView;
    }
}