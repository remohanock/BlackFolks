package com.loopz.blackfolks.views.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopz.blackfolks.R;
import com.loopz.blackfolks.adapter.AdapterHome;
import com.loopz.blackfolks.adapter.AdapterUser;
import com.loopz.blackfolks.constants.FirebaseConstants;
import com.loopz.blackfolks.customViews.NothingLayout;
import com.loopz.blackfolks.model.Home;
import com.loopz.blackfolks.model.UserHome;
import com.loopz.blackfolks.views.MainActivity;

import java.util.ArrayList;

public class HomesFragment extends Fragment implements AdapterHome.OnViewHolderClickListener {
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
    RecyclerView homeListRecyclerview;
    AdapterHome adapterUser;
    ArrayList<UserHome> homesArrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    NothingLayout nothingLayout;
    FloatingActionButton fab;
    ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public HomesFragment() {
        // Required empty public constructor
    }

    public static HomesFragment newInstance(String param1, String param2) {
        HomesFragment fragment = new HomesFragment();
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
        progressDialog=new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        homeListRecyclerview = view.findViewById(R.id.userListRecyclerview);
        homeListRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListRecyclerview.setHasFixedSize(false);
        adapterUser = new AdapterHome(homesArrayList, HomesFragment.this);
        adapterUser.setPrimeHome(((MainActivity) getActivity()).getHome());
        homeListRecyclerview.setAdapter(adapterUser);
        nothingLayout = view.findViewById(R.id.nothingLayout);
        fab = view.findViewById(R.id.fab);
        fab.hide();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
            }
        });
        getUsers();
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
    public void onHomeViewHolderClick(final UserHome home) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Primary Home")
                .setMessage("Are you sure you want to set this as primary home?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setHomePrimary(home);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setHomePrimary(final UserHome home2) {
        progressDialog.show();
        FirebaseConstants.getHomeReference().document(home2.getHomeId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final Home home = task.getResult().toObject(Home.class);
                home.setPrivilege(home2.getPriority());
                home.setRoomIds(home2.getRoomIds());
                Log.e("home", home.toString());
                FirebaseConstants.getPrimaryHomeReference().document(firebaseAuth.getUid()).set(home).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //getHomes();
                        if (task.isSuccessful()) {/*
                            ((MainActivity) getActivity()).setHome(home);*/
                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                            getActivity().finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onHomeViewHolderLongClick(UserHome home) {

    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseConstants.getUserHomeReference().whereEqualTo("userId", firebaseAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                swipeRefreshLayout.setRefreshing(false);
                if (task.isSuccessful()) {
                    homesArrayList.removeAll(homesArrayList);
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Log.e("homes", snapshot.toString());
                        UserHome userHome = snapshot.toObject(UserHome.class);
                        homesArrayList.add(userHome);
                    }
                    adapterUser.notifyDataSetChanged();

                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("User List");
    }
}
