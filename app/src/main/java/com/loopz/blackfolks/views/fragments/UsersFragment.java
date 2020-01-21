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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterUser;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.User;
import com.loopz.blackfolks.model.UserHome;
import com.loopz.blackfolks.views.MainActivity;
import com.loopz.blackfolks.views.SwitchesActivity;
import com.loopz.blackfolks.views.UserRoomAccessEditActivity;
import com.loopz.blackfolks.views.UserRoomAceessActivity;

import java.util.ArrayList;

import static com.loopz.blackfolks.constants.Roles.OWNER;

public class UsersFragment extends Fragment implements AdapterUser.OnViewHolderClickListener {
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
    RecyclerView userListRecyclerview;
    AdapterUser adapterUser;
    AlertDialog dialog;
    ArrayList<UserHome> userArrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    NothingLayout nothingLayout;
    FloatingActionButton fab;

    private OnFragmentInteractionListener mListener;

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fab = view.findViewById(R.id.fab);
        userListRecyclerview = view.findViewById(R.id.userListRecyclerview);
        userListRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        userListRecyclerview.setHasFixedSize(false);
        adapterUser = new AdapterUser(userArrayList, UsersFragment.this);
        userListRecyclerview.setAdapter(adapterUser);
        nothingLayout = view.findViewById(R.id.nothingLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
            }
        });
        //getUsers();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addUserDialog();
                Intent intent=new Intent(getActivity(), UserRoomAceessActivity.class);
                intent.putExtra("home",((MainActivity) getActivity()).getHome());
                startActivity(intent);
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

    @Override
    public void onUserViewHolderClick(UserHome user) {
        if(!user.getPriority().equals(OWNER)) {
            Intent intent = new Intent(getActivity(), UserRoomAccessEditActivity.class);
            intent.putExtra("userHome", user);
            intent.putExtra("home", ((MainActivity) getActivity()).getHome());
            startActivity(intent);
        }else {
            Toast.makeText(getActivity(), "Cant edit owner settings", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserViewHolderLongClick(UserHome user) {

    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseConstants.getUserHomeReference().whereEqualTo("homeId", ((MainActivity) getActivity()).getHome().getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                swipeRefreshLayout.setRefreshing(false);
                if (task.isSuccessful()) {
                    userArrayList.removeAll(userArrayList);
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        final UserHome userHome = snapshot.toObject(UserHome.class);
                        userHome.setId(snapshot.getId());
//                        Log.e("homes", snapshot.toString());
//                        userArrayList.add(userHome);
                        FirebaseConstants.getUserReference().document(userHome.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                try {
                                    userHome.setUser(task.getResult().toObject(User.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                userArrayList.add(userHome);
                                adapterUser.notifyDataSetChanged();
                            }
                        });
                    }

                }
            }
        });

    }

    private void addUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_user_dialog, null);
        builder.setView(view);
        final EditText etUserId = view.findViewById(R.id.etUserId);
        Button btn_change_name = view.findViewById(R.id.btn_change_name);
        final Spinner spPriority = view.findViewById(R.id.spPriority);
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(new UserHome(((MainActivity) getActivity()).getHome().getId(), etUserId.getText().toString(), spPriority.getSelectedItem().toString(), null));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void addUser(UserHome userHome) {
        FirebaseConstants.getUserHomeReference().add(userHome).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getActivity(), "User added successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("User List");
        getUsers();
    }
}
