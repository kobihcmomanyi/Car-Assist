package com.carassist.carassist.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.GarageArrayAdapter;
import com.carassist.carassist.adapters.GarageProfileArrayAdapter;
import com.carassist.carassist.data.AuthCredentials;
import com.carassist.carassist.data.Garage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2/27/2016.
 */
public class GarageOwnerFragment extends Fragment {

    ListView listView;
    ArrayList<Garage> items;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    public GarageArrayAdapter garageArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_garage,container,false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        items = new ArrayList<>();

        garageArrayAdapter = new GarageArrayAdapter(getActivity(),items);

        listView = (ListView)rootView.findViewById(R.id.fragment_garage_list_view);

        databaseReference.child("garage").keepSynced(true);
        databaseReference.child("garage").addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Garage garage = postSnapshot.getValue(Garage.class);
                    items.add(garage);
                }

                garageArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(databaseError != null){
                    Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setAdapter(garageArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {

                ImageView call = (ImageView) view.findViewById(R.id.garage_call);
                ImageView locate = (ImageView) view.findViewById(R.id.garage_locate);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = items.get(position).getPhoneNumber();

                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                        startActivity(intent);
                    }
                });

                locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String location = items.get(position).getLocation().replace(" ","+");
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+location);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
            }
        });


        return rootView;
    }

    public void listByName(String name){
        ArrayList<Garage> filteredItems = new ArrayList<>();

        for(Garage garage:items){
            if(garage.getName().equalsIgnoreCase(name) || garage.getLocation().equalsIgnoreCase(name)){
                filteredItems.add(garage);
            }
        }
        items.clear();
        items.addAll(filteredItems);
        garageArrayAdapter.notifyDataSetChanged();

    }

}
