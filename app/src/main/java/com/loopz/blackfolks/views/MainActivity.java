package com.loopz.blackfolks.views;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.constants.Roles;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.Room;
import com.loopz.blackfolks.model.Switch;
import com.loopz.blackfolks.model.UserHome;
import com.loopz.blackfolks.views.fragments.RoomsFragment;
import com.loopz.blackfolks.views.fragments.UsersFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Home home;
    FirebaseAuth firebaseAuth;
    AlertDialog dialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        /*fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new RoomsFragment());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        fragmentManager.beginTransaction().replace(R.id.container, new RoomsFragment());
                        break;
                    case R.id.nav_users:
                        fragmentManager.beginTransaction().replace(R.id.container, new UsersFragment());
                        break;
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;
            }
        });*/
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        configure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void getHomes() {
        FirebaseConstants.getPrimaryHomeReference().document(firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        home = task.getResult().toObject(Home.class);
                        Log.e("home",home.toString());
                        configure();
                    } else {
                        showAddHomeDialog();
                    }
                } else {
                    showAddHomeDialog();
                }
            }
        });
    }

    private void showAddHomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_home_dialog, null);
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
                                    Toast.makeText(getApplicationContext(), "Failed adding new switch", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        configure();
    }

    public void configure(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_users,R.id.nav_home_change,R.id.logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }
}
