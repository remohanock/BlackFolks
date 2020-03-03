package com.loopz.blackfolks.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterRoomSwitchSelection;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.SceneMode;

import java.util.ArrayList;

public class SceneModeActivity extends AppCompatActivity implements AdapterRoomSwitchSelection.OnViewHolderClickListener {
    EditText etName;

    Button btn_change_name;

    Spinner spPriority;
    Home home;
    RecyclerView roomsList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomsReference;
    ArrayList<Room> roomArrayList = new ArrayList<>();
    ArrayList<String> roomList = new ArrayList<>();
    AdapterRoomSwitchSelection adapterRoom;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_mode);
        setTitle("Scene Mode");
        home = (Home) getIntent().getSerializableExtra("home");
        if (getSupportActionBar() !=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        etName = findViewById(R.id.etUserId);
        btn_change_name = findViewById(R.id.btn_change_name);
        spPriority = findViewById(R.id.spPriority);
        roomsList = findViewById(R.id.roomsList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        roomsList.setLayoutManager(layoutManager);
        roomsList.setHasFixedSize(false);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSceneMode(new SceneMode(etName.getText().toString(), home.getId(), FirebaseAuth.getInstance().getUid(), roomList));

            }
        });
        getRooms();
    }

    private void getRooms() {
        adapterRoom = new AdapterRoomSwitchSelection(roomArrayList, SceneModeActivity.this);
        adapterRoom.setHome(home);
        roomsList.setAdapter(adapterRoom);
        roomsReference = firebaseDatabase.getReference().child(home.getId());
        roomsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    roomArrayList.add(room);
                }
                adapterRoom.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addSceneMode(SceneMode sceneMode) {
        progressDialog.show();
        FirebaseConstants.getSceneModeReference().add(sceneMode).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Scene mode created successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRoomSelectionChange(String parentId, Room room, boolean isSelected) {
        Log.e("isSelected", isSelected + "");
        Log.e("before list", roomList.toString());
        if (isSelected) {
            roomList.add(parentId + "," + room.getId());
        } else {
            roomList.remove(parentId + "," + room.getId());
        }
        Log.e("after list", roomList.toString());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
