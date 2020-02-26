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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.Utilities;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.Switch;
import com.loopz.blackfolks.views.SwitchesActivity;

import java.util.ArrayList;


public class AdapterRoomSwitchSelection extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterRoomSelection.OnViewHolderClickListener {
    ArrayList<Room> roomArrayList;
    OnViewHolderClickListener onViewHolderClickListener;
    static Context context;
    private int height = 0;
    private int width = 0;
    boolean isFromHome = false;
    Home home;
    Activity activity;

    public AdapterRoomSwitchSelection(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
    }

    public AdapterRoomSwitchSelection(ArrayList<Room> roomArrayList, OnViewHolderClickListener onViewHolderClickListener, boolean isFromHome) {
        this.roomArrayList = roomArrayList;
        this.onViewHolderClickListener = onViewHolderClickListener;
        this.isFromHome = isFromHome;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    //Self Constructed interface to allow subscription to events by implementors
    public interface OnViewHolderClickListener {
        void onRoomSelectionChange(String parentId,Room room, boolean isSelected);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_rooms_switch_selection, parent, false);
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
        //holder.cbTick.setText(title);
        getSwitches(room,holder);
        holder.room = room;
    }

    private void getSwitches(final Room room, final ViewHolderSender holder) {
        FirebaseConstants.getSwitchesReference(home.getId(), room.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        Switch aSwitch=snapshot.toObject(Switch.class);
                        Room switchR=new Room(snapshot.getId(),aSwitch.getName());
                        holder.switchArrayList.add(switchR);
                    }
                    /*if (switchArrayList.size() == 0) {
                        nothingLayout.setVisibility(View.VISIBLE);
                    } else {
                        nothingLayout.setVisibility(View.GONE);
                    }*/
/*                    if (switchArrayList.size() >= 4) {
                        fab.hide();
                    } else {
                        fab.show();
                    }*/
                    AdapterRoomSelection adapterRoomSelection=new AdapterRoomSelection(holder.switchArrayList,AdapterRoomSwitchSelection.this);
                    holder.switchList.setAdapter(adapterRoomSelection);
                    adapterRoomSelection.setParentId(room.getId());
                } else {
                }
            }
        });
    }

    @Override
    public void onRoomSelectionChange(String parentId,Room switchT, boolean isSelected) {
        onViewHolderClickListener.onRoomSelectionChange(parentId,switchT,isSelected);
    }

    @Override
    public int getItemCount() {
        return roomArrayList.size();
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {
        protected TextView tvTitle;
        protected AdapterRoomSwitchSelection.OnViewHolderClickListener onViewHolderClickListener;
        protected Room room;
        protected CheckBox cbTick;
        protected RecyclerView switchList;
        protected ArrayList<Room> switchArrayList=new ArrayList<>();

        public ViewHolderSender(final View itemView, final AdapterRoomSwitchSelection.OnViewHolderClickListener onViewHolderClickListener) {
            super(itemView);
            this.onViewHolderClickListener = onViewHolderClickListener;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            switchList = itemView.findViewById(R.id.switchList);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            switchList.setLayoutManager(layoutManager);
            switchList.setHasFixedSize(false);
            //cbTick = itemView.findViewById(R.id.cbTick);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onViewHolderClickListener.onRoomSelectionChange(room,cbTick.isSelected());
//                }
//            });
            /*cbTick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onViewHolderClickListener.onRoomSelectionChange(room,isChecked);
                }
            });*/
        }
    }


    private void setScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = context.getResources().getDisplayMetrics();
        height = displayMetrics.heightPixels;
        this.width = displayMetrics.widthPixels;
    }
}
