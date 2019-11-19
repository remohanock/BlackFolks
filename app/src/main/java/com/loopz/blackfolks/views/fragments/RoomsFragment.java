package com.loopz.blackfolks.views.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterRoom;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.constants.Roles;
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.Switch;
import com.loopz.blackfolks.model.UserHome;
import com.loopz.blackfolks.views.MainActivity;
import com.loopz.blackfolks.views.SwitchesActivity;

import java.util.ArrayList;

public class RoomsFragment extends Fragment implements AdapterRoom.OnViewHolderClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomsReference;
    RecyclerView recyclerView;
    ArrayList<Room> roomArrayList = new ArrayList<>();
    AdapterRoom adapterRoom;
    AlertDialog dialog;
    Home home;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab;
    NothingLayout nothingLayout;

    private OnFragmentInteractionListener mListener;

    public RoomsFragment() {
        // Required empty public constructor
    }

    public static RoomsFragment newInstance(String param1, String param2) {
        RoomsFragment fragment = new RoomsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        nothingLayout = view.findViewById(R.id.nothingLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);
        fab = view.findViewById(R.id.fab);
        fab.hide();
        adapterRoom = new AdapterRoom(roomArrayList, RoomsFragment.this);
        recyclerView.setAdapter(adapterRoom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoomDialog();
            }
        });
        getHomes();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRooms();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getRooms() {
        //home= ((MainActivity) getActivity()).getHome();
        //Log.e("home",home.toString());
        adapterRoom.setHome(home);
        fab.show();
        roomsReference = firebaseDatabase.getReference().child(home.getId());
        roomArrayList.removeAll(roomArrayList);
        swipeRefreshLayout.setRefreshing(true);
        roomsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    roomArrayList.add(room);
                }
                adapterRoom.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                if (roomArrayList.size() == 0) {
                    nothingLayout.setVisibility(View.VISIBLE);
                } else {
                    nothingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRoomViewHolderClick(Room room) {
        Intent intent = new Intent(getActivity(), SwitchesActivity.class);
        intent.putExtra("room", room);
        intent.putExtra("home", home);
        startActivity(intent);
    }

    @Override
    public void onRoomViewHolderLongClick(Room room) {
        showNameChangeDialog(room);
    }

    private void showNameChangeDialog(final Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_room_dialog, null);
        builder.setView(view);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Change Room Name");
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        et_new_name.setText(room.getName());
        et_new_name.setSelection(0, et_new_name.getText().length());
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                room.setName(et_new_name.getText().toString());
                saveRoom(room);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void addRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_room_dialog, null);
        builder.setView(view);
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoom(new Room("R" + (roomArrayList.size() + 1), et_new_name.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveRoom(Room room) {
        roomsReference.child(room.getId()).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getRooms();
            }
        });
    }

    private void getHomes() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseConstants.getPrimaryHomeReference().document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        home = task.getResult().toObject(Home.class);
                        ((MainActivity) getActivity()).setHome(home);
                        getRooms();
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        showAddHomeDialog();
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showAddHomeDialog();
                }
            }
        });
    }

    private void showAddHomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_home_dialog, null);
        builder.setView(view);
        final EditText etHomeId = view.findViewById(R.id.etHomeId);
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHome(new Home(etHomeId.getText().toString().trim(), et_new_name.getText().toString(), Roles.ADMIN, null));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveHome(final Home home2) {
        FirebaseConstants.getHomeReference().document(home2.getId()).set(home2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //getHomes();
                if (task.isSuccessful()) {
                    home = home2;
                    ((MainActivity) getActivity()).setHome(home);
                    initRooms();
                    saveRoomConfig();
                    setHomePrimary(home);
                }
            }
        });
    }

    private void saveRoomConfig() {
        FirebaseConstants.getUserHomeReference().add(new UserHome(home.getId(), firebaseAuth.getUid(), Roles.OWNER, null));
    }

    private void setHomePrimary(final Home home2) {
        FirebaseConstants.getPrimaryHomeReference().document(firebaseAuth.getUid()).set(home).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //getHomes();
                if (task.isSuccessful()) {
                }
            }
        });
    }

    private void initRooms() {
        roomsReference = firebaseDatabase.getReference().child(home.getId());
        for (int i = 1; i < 3; i++) {
            final Room room = new Room("R" + i, "Room " + i);
            roomsReference.child(room.getId()).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    for (int j = 1; j < 3; j++) {
                        Switch aSwitch = new Switch(j, "Switch " + j);
                        FirebaseConstants.getSwitchesReference(home.getId(), room.getId()).document("S" + aSwitch.getId()).set(aSwitch).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                } else {
                                    Toast.makeText(getActivity(), "Failed adding new switch", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        getRooms();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (home != null) {
            getRooms();
            getActivity().setTitle("Rooms");
        }
    }
}
