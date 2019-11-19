package com.loopz.blackfolks.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loopz.blackfolks.R;
import com.loopz.blackfolks.model.Switch;

import java.util.ArrayList;


public class AdapterSwitches extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Switch> switchArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    Activity activity;

    public AdapterSwitches(ArrayList<Switch> switchArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.switchArrayList = switchArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterSwitches(ArrayList<Switch> switchArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromHome) {
        this.switchArrayList = switchArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromHome = isFromHome;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onSwitchViewHolderClick(Switch switchObj);

        void onToggleSwitchClick(Switch switchObj, boolean isChecked);

        void onSwitchViewHolderLongClick(Switch switchObj);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_switch, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderSender holder = (ViewHolderSender) viewHolder;
        final Switch switchObj = switchArrayList.get(position);
        String title = switchObj.getName();
        holder.tvTitle.setText(title);
        holder.btSwitch.setChecked(switchObj.isSwitchOn());
        holder.btSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onViewHolderClickListener.onToggleSwitchClick(switchObj, isChecked);
            }
        });

        holder.switchObj = switchObj;
    }

    @Override
    public int getItemCount() {
        return switchArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle, tvLetter, tvRating, tvCategory, tvDaysCount, tvCount;
        protected OnViewHolderClickListener onViewHolderClickListener;
        protected Switch switchObj;
        protected android.widget.Switch btSwitch;
        View layout;

        public ViewHolderSender(final View itemView, final OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvName);
            btSwitch = itemView.findViewById(R.id.btSwitch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onSwitchViewHolderClick(switchObj);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onViewHolderClickListener.onSwitchViewHolderLongClick(switchObj);
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
