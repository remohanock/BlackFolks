package com.loopz.blackfolks.constants;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConstants {

    public static String USERS = "users";
    public static String HOMES = "homes";
    public static String PRIMARY_HOME = "primaryHome";
    public static String USER_HOMES = "userHomes";
    public static String ROOMS = "rooms";
    public static String SWITCHES = "switches";

    public static CollectionReference getHomeReference(){
        return FirebaseFirestore.getInstance().collection(HOMES);
    }
    public static CollectionReference getPrimaryHomeReference(){
        return FirebaseFirestore.getInstance().collection(PRIMARY_HOME);
    }

    public static CollectionReference getUserHomeReference(){
        return FirebaseFirestore.getInstance().collection(USER_HOMES);
    }

    public static CollectionReference getRoomsReference(String homeId){
        return FirebaseFirestore.getInstance().collection(ROOMS);
    }

    public static CollectionReference getSwitchesReference(String homeId, String roomId){
        return FirebaseFirestore.getInstance().collection(SWITCHES).document(homeId).collection(roomId);
    }
}
