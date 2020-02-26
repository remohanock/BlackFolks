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
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.SceneMode;
import com.loopz.blackfolks.model.SceneMode;

import java.util.ArrayList;


public class AdapterSceneMode extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SceneMode> sceneModeArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    static Home home;
    Activity activity;

    public AdapterSceneMode(ArrayList<SceneMode> sceneModeArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.sceneModeArrayList = sceneModeArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterSceneMode(ArrayList<SceneMode> sceneModeArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromHome) {
        this.sceneModeArrayList = sceneModeArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromHome = isFromHome;
    }

    public void setHome(Home home2) {
        home = home2;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onSceneModeViewHolderClick(SceneMode sceneMode);
        void onSceneModeSwitchClick(SceneMode sceneMode);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_scene_modes, parent, false);
        context = parent.getContext();
        activity = (Activity) context;
        ViewHolderSender viewHolderSender = new ViewHolderSender(itemView, onViewHolderClickListener);
        setScreenDimensions();
        return viewHolderSender;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolderSender holder = (ViewHolderSender) viewHolder;
        final SceneMode sceneMode = sceneModeArrayList.get(position);
        Log.e("mode",sceneMode.toString());
        String title = sceneMode.getName();
        holder.tvTitle.setText(title);
            if (!sceneMode.isOn()) {
                holder.switch1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_off));
            } else {
                holder.switch1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_on));
            }
        holder.sceneMode = sceneMode;
    }


    @Override
    public int getItemCount() {
        return sceneModeArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle, tvLetter;
        protected AdapterSceneMode.OnViewHolderClickListener onViewHolderClickListener;
        protected SceneMode sceneMode;
        protected ImageView switch1, switch2, switch3, switch4;
        View layout;

        public ViewHolderSender(final View itemView, final AdapterSceneMode.OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLetter = itemView.findViewById(R.id.tvLetter);
            switch1 = itemView.findViewById(R.id.switch1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onSceneModeViewHolderClick(sceneMode);
                }
            });
            switch1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderClickListener.onSceneModeSwitchClick(sceneMode);
                    if(sceneMode.isOn()){
                        switch1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_off));
                        sceneMode.setOn(false);
                        turnSwitches(sceneMode.getRoomSwitch(),false);
                    }else {
                        switch1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_switch_on));
                        sceneMode.setOn(true);
                        turnSwitches(sceneMode.getRoomSwitch(),true);
                    }
                    FirebaseConstants.getSceneModeReference().document(sceneMode.getId()).set(sceneMode);
                }
            });
        }

        private void turnSwitches(ArrayList<String> roomSwitches, final boolean isOn) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            for (String switchR:roomSwitches) {
                final String[] switchArr=switchR.split(",");
                final DatabaseReference buttonStateReference = firebaseDatabase.getReference().child(home.getId()).child(switchArr[0]).child("buttonState");
                buttonStateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        StringBuilder buttonState = null;
                        try {
                            buttonState = new StringBuilder(dataSnapshot.getValue().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            buttonState = new StringBuilder("0000");
                        }
                        if (isOn) {
                            buttonState.setCharAt(Integer.parseInt(switchArr[1].replace("S","")) - 1, '1');
                        } else {
                            buttonState.setCharAt(Integer.parseInt(switchArr[1].replace("S","")) - 1, '0');
                        }
                        buttonStateReference.setValue(buttonState.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }


    private void setScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = context.getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
    }
}
