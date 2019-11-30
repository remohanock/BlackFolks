package com.loopz.blackfolks.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loopz.blackfolks.R;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.User;
import com.loopz.blackfolks.model.UserHome;

import java.util.ArrayList;


public class AdapterRoomSelection extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Room> roomArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    String parentId;
    Activity activity;
    UserHome userHome;

    public AdapterRoomSelection(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterRoomSelection(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener, UserHome userHome) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.userHome = userHome;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onRoomSelectionChange(String id, Room room, boolean isSelected);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_rooms_selection, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener, parentId);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderSender holder = (ViewHolderSender) viewHolder;
        final Room room = roomArrayList.get(position);
        try {
            String title = room.getName();
            //holder.tvTitle.setText(title);
            holder.cbTick.setText(title);
        if (userHome != null) {
            holder.cbTick.setChecked(userHome.getRoomIds().contains(room.getId()));
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.room = room;
    }


    @Override
    public int getItemCount() {
        return roomArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle;
        protected AdapterRoomSelection.OnViewHolderClickListener onViewHolderClickListener;
        protected Room room;
        protected CheckBox cbTick;

        public ViewHolderSender(final View itemView, final AdapterRoomSelection.OnViewHolderClickListener onViewHolderClickListener,
                                final String parentId) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            cbTick = itemView.findViewById(R.id.cbTick);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onViewHolderClickListener.onRoomSelectionChange(room,cbTick.isSelected());
//                }
//            });
            cbTick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onViewHolderClickListener.onRoomSelectionChange(parentId, room, isChecked);
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
