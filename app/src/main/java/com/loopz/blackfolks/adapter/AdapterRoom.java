package com.loopz.blackfolks.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;

import java.util.ArrayList;


public class AdapterRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Room> roomArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    Home home;
    Activity activity;

    public AdapterRoom(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterRoom(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromHome) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromHome = isFromHome;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onRoomViewHolderClick(Room room);

        void onRoomViewHolderLongClick(Room room);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_rooms, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderSender holder = (ViewHolderSender) viewHolder;
        final Room room = roomArrayList.get(position);
        String title = room.getName();
        holder.tvTitle.setText(title);
        holder.tvLetter.setText(title.substring(0, 1));
        for (int i = 0; i < 4; i++) {
            Resources res = context.getResources();
            int id = res.getIdentifier("switch" + (i + 1), "id", context.getPackageName());
            ImageView aSwitch = holder.itemView.findViewById(id);
            if (room.getButtonState().charAt(i) == '0') {
                aSwitch.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_off));
            } else {
                aSwitch.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_on));
            }
        }
        if (home != null) {
            DatabaseReference buttonStateReference = FirebaseDatabase.getInstance().getReference().child(home.getId()).child(room.getId()).child("buttonState");
            buttonStateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    room.setButtonState(dataSnapshot.getValue().toString());
                    for (int i = 0; i < 4; i++) {
                        Resources res = context.getResources();
                        int id = res.getIdentifier("switch" + (i + 1), "id", context.getPackageName());
                        ImageView aSwitch = holder.itemView.findViewById(id);
                        if ((room.getButtonState().charAt(i)+"").equals("0")) {
                            aSwitch.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_off));
                        } else {
                            Log.e(room.getId()+" state "+i,room.getButtonState().charAt(i) +"");
                            aSwitch.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_on));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        holder.room = room;
    }


    @Override
    public int getItemCount() {
        return roomArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle, tvLetter;
        protected AdapterRoom.OnViewHolderClickListener onViewHolderClickListener;
        protected Room room;
        protected ImageView switch1, switch2, switch3, switch4;
        View layout;

        public ViewHolderSender(final View itemView, final AdapterRoom.OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            switch1 = itemView.findViewById(R.id.switch1);
            switch2 = itemView.findViewById(R.id.switch2);
            switch3 = itemView.findViewById(R.id.switch3);
            switch4 = itemView.findViewById(R.id.switch4);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onRoomViewHolderClick(room);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onViewHolderClickListener.onRoomViewHolderLongClick(room);
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
