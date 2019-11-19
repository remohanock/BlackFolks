package com.loopz.blackfolks.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;

import java.util.ArrayList;

import static com.loopz.blackfolks.constants.FirebaseConstants.ROOMS;


public class RoomsActivity extends AppCompatActivity implements AdapterRoom.OnViewHolderClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        getSupportActionBar().setTitle("Rooms");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        home = (Home) getIntent().getSerializableExtra("home");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        nothingLayout = findViewById(R.id.nothingLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        fab = findViewById(R.id.fab);
        fab.hide();
        adapterRoom = new AdapterRoom(roomArrayList, RoomsActivity.this);
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

    private void getRooms() {
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
        Intent intent = new Intent(this, SwitchesActivity.class);
        intent.putExtra("room", room);
        intent.putExtra("home", home);
        startActivity(intent);
    }

    @Override
    public void onRoomViewHolderLongClick(Room room) {
        showNameChangeDialog(room);
    }

    private void showNameChangeDialog(final Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_room_dialog, null);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_room_dialog, null);
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
        FirebaseConstants.getHomeReference().document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        home = task.getResult().toObject(Home.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_home_dialog, null);
        builder.setView(view);
        final EditText etRoomId = view.findViewById(R.id.etHomeId);
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHome(new Home(etRoomId.getText().toString().trim(), et_new_name.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveHome(final Home home2) {
        FirebaseConstants.getHomeReference().document(firebaseAuth.getUid()).set(home2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //getHomes();
                if(task.isSuccessful()) {
                    home = home2;
                    getRooms();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
