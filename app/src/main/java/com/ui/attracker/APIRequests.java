package com.ui.attracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ui.attracker.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class APIRequests {

    static FirebaseDatabase database = null;

    public static User user = null;


    public static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(false);
        }
    }


    public static void login(final String username, final Context context) {
        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User newUser = null;

                if (dataSnapshot.getChildrenCount() > 0)
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        User child = Objects.requireNonNull(childSnapshot.getValue(User.class));
                        if (child.getUsername().equals(username))
                            newUser = child;
                    }

                if (newUser == null) {
                    DatabaseReference dataRef = database.getReference("users").push();
                    newUser = new User(username, dataRef.getKey());
                    dataRef.setValue(newUser);
                }

                user = newUser;
                user.updateEvents(context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void addEvent(String name, Context context) {
        if (user == null)
            return;

        user.addEvent(name, context);

        Map<String, Object> map = new HashMap<>();
        map.put("events", user.getEvents());
        database.getReference("users").child(user.getKey()).updateChildren(map);

        database.getReference("users").child(user.getKey()).child("attendees").child(name).push().setValue(user.getUsername());
    }

    public static void addAttendee(String eventName, String userKey, final SuccessfullyScannedActivity activity) {
        if (user == null) {
            activity.setMessage("Error");
            return;
        }

        final DatabaseReference myRef = database.getReference("users").child(userKey).child("attendees").child(eventName);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren())
                        if (childSnapshot.getValue().equals(user.getUsername())) {
                            activity.setMessage("You are already registered");
                            return;
                        }
                    myRef.push().setValue(user.getUsername());
                    activity.setMessage("You are successfully added");

                } else
                    activity.setMessage("Failed to add you");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void retrieveAttendees(String eventName, final ArrayAdapter<String> adapter) {
        database.getReference("users").child(user.getKey()).child("attendees").child(eventName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.add(snapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
