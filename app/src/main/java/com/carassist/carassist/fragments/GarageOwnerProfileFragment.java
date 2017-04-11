package com.carassist.carassist.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.GarageProfileArrayAdapter;
import com.carassist.carassist.data.Garage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * Created by user on 2/28/2016.
 */
public class GarageOwnerProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ArrayList<Garage> items = new ArrayList<>();

    GarageProfileArrayAdapter garageProfileArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_garage_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        garageProfileArrayAdapter = new GarageProfileArrayAdapter(getActivity(),items);

        DatabaseReference garageReference = databaseReference.child("garage");
        Query queryRef = garageReference.orderByChild("uniqueId").equalTo(mAuth.getCurrentUser().getUid());
        queryRef.keepSynced(true);

        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.fragment_garage_profile_empty_state);
        ListView listView = (ListView)rootView.findViewById(R.id.fragment_garage_profile_list_view);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Garage garage = snapshot.getValue(Garage.class);
                    items.add(garage);
                }
                garageProfileArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //items.add(new Garage("name", "description", "location", "", ""));

        listView.setAdapter(garageProfileArrayAdapter);

        linearLayout.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final DatabaseReference deleteSparesReference = databaseReference.child("garage").child(items.get(position).getPushId());

                ImageView delete = (ImageView)view.findViewById(R.id.garage_delete);
                ImageView edit = (ImageView)view.findViewById(R.id.garage_edit);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Garage garage = items.get(position);
                        garageDialog(garage);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        deleteSparesReference.setValue(null, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError == null){
                                    //item deleted successfully
                                    //Snackbar.make(v, "item has been deleted", Snackbar.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(),"item has been deleted",Toast.LENGTH_SHORT).show();
                                    garageProfileArrayAdapter.notifyDataSetChanged();

                                }else{
                                    //an error occurred
                                    Snackbar.make(v,"an error occurred, try again",Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });

        return rootView;
    }

    public void garageDialog(final Garage garage){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.add_garage_layout,null);

        dialog.setView(dialogView);
        dialog.setTitle("ADD GARAGE");

        //initialize dialog views
        final MaterialEditText name = (MaterialEditText) dialogView.findViewById(R.id.add_garage_name);
        final MaterialEditText description = (MaterialEditText) dialogView.findViewById(R.id.add_garage_description);
        final MaterialEditText location = (MaterialEditText) dialogView.findViewById(R.id.add_garage_location);

        name.setText(garage.getName());
        description.setText(garage.getDescription());
        location.setText(garage.getLocation());

        dialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference garageReference = databaseReference.child("garage").child(garage.getPushId());

                Garage garage = new Garage();
                garage.setName(name.getText().toString());
                garage.setLocation(location.getText().toString());
                garage.setDescription(description.getText().toString());
                garage.setUniqueId(mAuth.getCurrentUser().getUid());
                garage.setPushId(garageReference.getKey());

                garageReference.setValue(garage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //an error occurred
                            Toast.makeText(getActivity(), "an error occurred,please try again", Toast.LENGTH_SHORT).show();

                        } else {
                            //data was saved successfully
                            Toast.makeText(getActivity(), "data saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

}
