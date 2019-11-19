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
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.UserHome;

import java.util.ArrayList;


public class AdapterHome extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<UserHome> homeArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    Activity activity;
    Home primeHome;

    public AdapterHome(ArrayList<UserHome> homeArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.homeArrayList = homeArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterHome(ArrayList<UserHome> homeArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromHome) {
        this.homeArrayList = homeArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromHome = isFromHome;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onHomeViewHolderClick(UserHome home);

        void onHomeViewHolderLongClick(UserHome home);
    }

    public void setPrimeHome(Home primeHome) {
        this.primeHome = primeHome;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_homes, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderSender holder = (ViewHolderSender) viewHolder;
        UserHome home = homeArrayList.get(position);
        String title = home.getHomeId();
        holder.tvTitle.setText(title);
        holder.tvRole.setText(home.getPriority());
        holder.tvLetter.setText(title.substring(0,1));
        if(home.getHomeId().equals(primeHome.getId())){
            holder.tvPrimary.setVisibility(View.VISIBLE);
        }else {
            holder.tvPrimary.setVisibility(View.GONE);
        }
        holder.home=home;
    }

    @Override
    public int getItemCount() {
        return homeArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle, tvLetter, tvPrimary, tvRole, tvDaysCount, tvCount;
        protected AdapterHome.OnViewHolderClickListener onViewHolderClickListener;
        protected UserHome home;
        protected ImageView ivImage;
        View layout;

        public ViewHolderSender(final View itemView, final AdapterHome.OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            tvPrimary = itemView.findViewById(R.id.tvPrimary);
            tvRole = itemView.findViewById(R.id.tvRole);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onHomeViewHolderClick(home);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onViewHolderClickListener.onHomeViewHolderLongClick(home);
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
