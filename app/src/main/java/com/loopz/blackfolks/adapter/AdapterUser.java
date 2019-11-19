package com.loopz.blackfolks.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loopz.blackfolks.R;
import com.loopz.blackfolks.model.UserHome;

import java.util.ArrayList;


public class AdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<UserHome> userArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromUser = false;
    Activity activity;

    public AdapterUser(ArrayList<UserHome> userArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.userArrayList = userArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterUser(ArrayList<UserHome> userArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromUser) {
        this.userArrayList = userArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromUser = isFromUser;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onUserViewHolderClick(UserHome user);

        void onUserViewHolderLongClick(UserHome user);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_users, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderSender holder = (ViewHolderSender) viewHolder;
        UserHome user = userArrayList.get(position);
        String title = user.getUserId();
        holder.tvTitle.setText(title);
        holder.tvRole.setText(user.getPriority());
        holder.tvLetter.setText(title.substring(0,1));

        holder.user=user;
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle, tvLetter, tvRole, tvCategory, tvDaysCount, tvCount;
        protected AdapterUser.OnViewHolderClickListener onViewHolderClickListener;
        protected UserHome user;
        protected ImageView ivImage;
        View layout;

        public ViewHolderSender(final View itemView, final AdapterUser.OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            tvRole = itemView.findViewById(R.id.tvRole);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onUserViewHolderClick(user);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onViewHolderClickListener.onUserViewHolderLongClick(user);
                    return true;
                }
            });
        }
    }


    private void setScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = context.getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
    }
}
