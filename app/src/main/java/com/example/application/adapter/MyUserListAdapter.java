package com.example.application.adapter;


import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.model.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;


public class MyUserListAdapter extends RecyclerView.Adapter<MyUserListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<User> userArray = null;
    private ArrayList<User> userListOrigin;

    public MyUserListAdapter(WeakReference<Context> context, ArrayList<User> userArray) {
        this.context = context.get();
        this.userArray = userArray;
        this.userListOrigin = new ArrayList<User>();
        this.userListOrigin.addAll(userArray);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        User user = userArray.get(position);
        Glide.with(context)
                .load(user.getAvatarUrl())
                .circleCrop()
                .into(myViewHolder.userImageView);
        myViewHolder.usernameTextView.setText(user.getLogin());

    }

    @Override
    public int getItemCount() {
        return userArray.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView userImageView;
        AppCompatTextView usernameTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            userImageView = itemView.findViewById(R.id.userImageView);

        }
    }

    // Filter Class
    public void filters(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        userArray.clear();
        if (charText.length() == 0) {
            userArray.addAll(userListOrigin);
        } else {
            for (User wp : userListOrigin) {
                if (wp.getLogin().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userArray.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MyUserListAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MyUserListAdapter.ClickListener clickListener) {
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
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildLayoutPosition(child));
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
