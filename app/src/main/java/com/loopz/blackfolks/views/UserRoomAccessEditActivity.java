package com.loopz.blackfolks.views;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterRoomSelection;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.UserHome;

import java.util.ArrayList;

import static com.loopz.blackfolks.constants.Roles.ADMIN;
import static com.loopz.blackfolks.constants.Roles.GUEST;
import static com.loopz.blackfolks.constants.Roles.OWNER;
import static com.loopz.blackfolks.constants.Roles.USER;

public class UserRoomAccessEditActivity extends AppCompatActivity implements AdapterRoomSelection.OnViewHolderClickListener {

    EditText etUserId;

    Button btn_change_name;

    Spinner spPriority;
    Home home;
    RecyclerView roomsList;
    UserHome userHome;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomsReference;
    ArrayList<Room> roomArrayList = new ArrayList<>();
    ArrayList<String> roomList = new ArrayList<>();
    AdapterRoomSelection adapterRoom;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_room_acees);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog=new ProgressDialog(this);
        setTitle("Edit user access");
        home = (Home) getIntent().getSerializableExtra("home");
        userHome = (UserHome) getIntent().getSerializableExtra("userHome");
        firebaseDatabase = FirebaseDatabase.getInstance();
        etUserId = findViewById(R.id.etUserId);
        btn_change_name = findViewById(R.id.btn_change_name);
        spPriority = findViewById(R.id.spPriority);
        roomsList = findViewById(R.id.roomsList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        roomsList.setLayoutManager(layoutManager);
        roomsList.setHasFixedSize(false);
        btn_change_name.setText("Save");
        etUserId.setFocusable(false);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(new UserHome(userHome.getId(),home.getId(), userHome.getUserId(), spPriority.getSelectedItem().toString(), roomList));

            }
        });
        initializeValues();
        getRooms();
    }

    private void initializeValues() {
        Log.e("userHome",userHome.toString());
        try {
            etUserId.setText(userHome.getUser().getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(userHome.getPriority().equals(ADMIN))
        spPriority.setSelection(0);
        else if(userHome.getPriority().equals(USER))
            spPriority.setSelection(1);
        else if(userHome.getPriority().equals(GUEST))
            spPriority.setSelection(2);

        roomList.addAll(userHome.getRoomIds());
    }

    private void getRooms() {

        adapterRoom = new AdapterRoomSelection(roomArrayList, UserRoomAccessEditActivity.this,userHome);
        roomsList.setAdapter(adapterRoom);
        adapterRoom.setParentId("");
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

    private void addUser(UserHome userHome) {
        progressDialog.show();
        Log.e("userhome",userHome.toString());
        FirebaseConstants.getUserHomeReference().document(userHome.getId()).set(userHome).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "User access updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onRoomSelectionChange(String parentId,Room room, boolean isSelected) {
        Log.e("isSelected",isSelected+"");
        Log.e("before list",roomList.toString());
        if (isSelected) {
            roomList.add(room.getId());
        } else {
            roomList.remove(room.getId());
        }
        Log.e("after list",roomList.toString());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
