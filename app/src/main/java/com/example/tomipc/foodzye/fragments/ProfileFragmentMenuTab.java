package com.example.tomipc.foodzye.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tomipc.foodzye.Database;
import com.example.tomipc.foodzye.DividerItemDecoration;
import com.example.tomipc.foodzye.FoodActivity;
import com.example.tomipc.foodzye.ProfileActivity;
import com.example.tomipc.foodzye.R;
import com.example.tomipc.foodzye.adapter.MenuAdapter;
import com.example.tomipc.foodzye.model.Menu;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragmentMenuTab extends Fragment {
    private List<Menu> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;
    Menu menu;
    int user_id;

    Database baza;
    Context c;

    public static ProfileFragmentMenuTab newInstance() {
        return new ProfileFragmentMenuTab();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_profile_menu, null);

        ProfileActivity activity = (ProfileActivity) getActivity();
        user_id = activity.getUserId();

        baza = new Database(c);

        recyclerView = (RecyclerView) view.findViewById(R.id.MenuRecyclerView);

        mAdapter = new MenuAdapter(menuList, c);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(c);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(c, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareMenuData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Menu menu = menuList.get(position);
                Toast.makeText(c, menu.getName() + " is selected!", Toast.LENGTH_SHORT).show();


                Intent foodActivity = new Intent(getActivity(), FoodActivity.class);
                foodActivity.putExtra("Menu", menu);

                startActivity(foodActivity);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        c = context;
    }

    private void prepareMenuData() {
        ArrayList<Menu> arrayOfFood;

        baza = new Database(c);
        arrayOfFood = baza.readMenu("getMenu/"+Integer.toString(user_id));

        for(Menu value: arrayOfFood) {
            menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getRate(), value.getPrice(), value.getFood_id(), value.getNameFood(), value.getUser_id());
            menuList.add(menu);
        }

        mAdapter.notifyDataSetChanged();

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ProfileFragmentMenuTab.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ProfileFragmentMenuTab.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
