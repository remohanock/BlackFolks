package com.loopz.blackfolks.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterHome;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.Home;

import java.util.ArrayList;

import static com.loopz.blackfolks.constants.FirebaseConstants.HOMES;

public class HomesActivity extends AppCompatActivity /*implements AdapterHome.OnViewHolderClickListener */{


    FirebaseAuth firebaseAuth;
    DatabaseReference homesReference;
    RecyclerView recyclerView;
    ArrayList<Home> homeArrayList = new ArrayList<>();
    AdapterHome adapterHome;
    AlertDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    NothingLayout nothingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homes);

        getSupportActionBar().setTitle("Homes");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        nothingLayout = findViewById(R.id.nothingLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        FloatingActionButton fab = findViewById(R.id.fab);
        //adapterHome = new AdapterHome(homeArrayList, HomesActivity.this);
        recyclerView.setAdapter(adapterHome);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHomeDialog();
            }
        });
        getHomes();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomes();
            }
        });
    }

    private void getHomes() {
        homeArrayList.removeAll(homeArrayList);
        swipeRefreshLayout.setRefreshing(true);
        homesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Home home = snapshot.getValue(Home.class);
                    homeArrayList.add(home);
                }
                adapterHome.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                if (homeArrayList.size() == 0) {
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
/*
    @Override
    public void onHomeViewHolderClick(Home home) {
        Intent intent = new Intent(this, RoomsActivity.class);
        intent.putExtra("home", home);
        startActivity(intent);
    }

    @Override
    public void onHomeViewHolderLongClick(Home home) {
        showNameChangeDialog(home);
    }*/

    private void showNameChangeDialog(final Home home) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_home_dialog, null);
        builder.setView(view);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Change Home Name");
        final EditText etRoomId = view.findViewById(R.id.etHomeId);
        etRoomId.setVisibility(View.GONE);
        final EditText et_new_name = view.findViewById(R.id.et_new_name);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        et_new_name.setText(home.getName());
        et_new_name.setSelection(0, et_new_name.getText().length());
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home.setName(et_new_name.getText().toString());
                saveHome(home);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void addHomeDialog() {
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

    private void saveHome(Home home) {
        homesReference.child(home.getId()).setValue(home).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getHomes();
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
            //
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
