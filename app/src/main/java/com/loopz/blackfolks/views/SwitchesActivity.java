package com.loopz.blackfolks.views;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.Utilities;
import com.loopz.blackfolks.adapter.AdapterSwitches;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.Switch;

import java.util.ArrayList;


public class SwitchesActivity extends AppCompatActivity implements AdapterSwitches.OnViewHolderClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference switchReference, buttonStateReference;
    RecyclerView recyclerView;
    ArrayList<Switch> switchArrayList = new ArrayList<>();
    AdapterSwitches adapterSwitches;
    AlertDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    Home home;
    Room room;
    String buttonState = "0000";
    FloatingActionButton fab;
    NothingLayout nothingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switches);
        fab = findViewById(R.id.fab);

        getSupportActionBar().setTitle("Switches");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        home = (Home) getIntent().getSerializableExtra("home");
        room = (Room) getIntent().getSerializableExtra("room");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //switchReference = firebaseDatabase.getReference(ROOMS).child(home.getId()).child(room.getId()).child(SWITCHES);
        buttonStateReference = firebaseDatabase.getReference().child(home.getId()).child(room.getId()).child("buttonState");
        //switchReference = firebaseDatabase.getReference(ROOMS).child(firebaseAuth.getUid()).child(home.getId()).child(SWITCHES);
        //switchReference = firebaseDatabase.getReference(SWITCHES).child(firebaseAuth.getUid()).child(room.getId());
        nothingLayout = findViewById(R.id.nothingLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapterSwitches = new AdapterSwitches(switchArrayList, SwitchesActivity.this);
        recyclerView.setAdapter(adapterSwitches);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoomDialog();
            }
        });
        getSwitches();
        getButtonState();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwitches();
            }
        });
    }

    private void getButtonState() {
        buttonStateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    buttonState = dataSnapshot.getValue().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    buttonState = "0000";
                }
                changeSwitchState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeSwitchState() {
        for (int i = 0; i < switchArrayList.size(); i++) {
            Switch aSwitch = switchArrayList.get(i);
            aSwitch.setSwitchOn(Utilities.getBoolean(buttonState.charAt(i)));
            switchArrayList.set(i, aSwitch);
        }
        adapterSwitches.notifyDataSetChanged();
    }

    private void getSwitches() {
        switchArrayList.removeAll(switchArrayList);
        swipeRefreshLayout.setRefreshing(true);
        FirebaseConstants.getSwitchesReference(home.getId(), room.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        Switch aSwitch = snapshot.toObject(Switch.class);
                        aSwitch.setSwitchOn(Utilities.getBoolean(buttonState.charAt(switchArrayList.size())));
                        switchArrayList.add(aSwitch);
                    }
                    adapterSwitches.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    if (switchArrayList.size() == 0) {
                        nothingLayout.setVisibility(View.VISIBLE);
                    } else {
                        nothingLayout.setVisibility(View.GONE);
                    }
                    if (switchArrayList.size() >= 4) {
                        fab.hide();
                    } else {
                        fab.show();
                    }
                } else {
                    Toast.makeText(SwitchesActivity.this, "Error getting switches", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSwitchViewHolderClick(Switch switchObj) {

    }

    @Override
    public void onToggleSwitchClick(final Switch switchObj, final boolean isChecked) {
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
                if (isChecked) {
                    buttonState.setCharAt(switchObj.getId() - 1, '1');
                } else {
                    buttonState.setCharAt(switchObj.getId() - 1, '0');
                }
                buttonStateReference.setValue(buttonState.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSwitchViewHolderLongClick(Switch switchObj) {
        showNameChangeDialog(switchObj);
    }

    private void showNameChangeDialog(final Switch aSwitch) {
        Log.e("room", aSwitch.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_switch_dialog, null);
        builder.setView(view);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Change Switch Name");
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        et_new_name.setText(aSwitch.getName());
        et_new_name.setSelection(0, et_new_name.getText().length());
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aSwitch.setName(et_new_name.getText().toString());
                saveRoom(aSwitch);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void addRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_switch_dialog, null);
        builder.setView(view);
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                saveRoom(new Switch((switchArrayList.size() + 1), et_new_name.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveRoom(final Switch aSwitch) {
        /*switchReference.child("S" + aSwitch.getId()).setValue(aSwitch).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //getSwitches();
            }
        });*/
        FirebaseConstants.getSwitchesReference(home.getId(), room.getId()).document("S" + aSwitch.getId()).set(aSwitch).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    getSwitches();
                } else {
                    Toast.makeText(SwitchesActivity.this, "Failed adding new switch", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
