package com.example.tomipc.foodzye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.MenuAdapter;
import com.example.tomipc.foodzye.model.Menu;
import com.example.tomipc.foodzye.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChooseFoodForEditActivity extends Navigation {

    private Toolbar toolbar;

    private List<Menu> menuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;
    Menu menu;
    UserLocalStore userLocalStore;
    User user;
    int user_id;

    Database db;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_food_for_edit);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        toolbar = (Toolbar) findViewById(R.id.ChooseFoodToolbar);
        set(toolbar);

        user_id = user.getId();

        c = this;

        db = new Database(c);

        recyclerView = (RecyclerView) findViewById(R.id.UserMenuRecyclerView);

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


                Intent EditFood = new Intent(getApplicationContext(), EditFoodActivity.class);
                EditFood.putExtra("Menu", menu);

                startActivity(EditFood);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void prepareMenuData() {
        ArrayList<Menu> arrayOfFood;

        db = new Database(c);
        arrayOfFood = db.readMenu("getMenu/"+Integer.toString(user_id));

        for(Menu value: arrayOfFood) {
            //menu = new Menu(value.getId(), value.getName(), value.getDescription(), value.getCurrency(), value.getImage(), value.getRate(), value.getPrice(), value.getFood_id(), value.getNameFood(), value.getUser_id());
            menuList.add(value);
        }

        mAdapter.notifyDataSetChanged();

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChooseFoodForEditActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChooseFoodForEditActivity.ClickListener clickListener) {
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
