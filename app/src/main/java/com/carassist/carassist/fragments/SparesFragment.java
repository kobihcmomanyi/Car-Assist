package com.carassist.carassist.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.SparesArrayAdapter;
import com.carassist.carassist.adapters.SparesProfileArrayAdapter;
import com.carassist.carassist.data.Garage;
import com.carassist.carassist.data.Sales;
import com.carassist.carassist.data.Spares;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2/27/2016.
 */
public class SparesFragment extends Fragment {

    ListView listView;
    ArrayList<Spares> items = new ArrayList<>();

    SparesArrayAdapter sparesArrayAdapter;

    String phoneNumber = "";

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spares,container,false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        sparesArrayAdapter = new SparesArrayAdapter(getActivity(),items);

        listView = (ListView)rootView.findViewById(R.id.fragment_spares_list_view);

        databaseReference.child("spares").keepSynced(true);
        databaseReference.child("spares").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Spares spares = postSnapshot.getValue(Spares.class);
                    items.add(spares);
                }

                sparesArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError != null) {
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setAdapter(sparesArrayAdapter);

        //add footer to the listView
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footerView = layoutInflater.inflate(R.layout.listview_footer, null);

        listView.addFooterView(footerView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Calendar calendar = Calendar.getInstance();
                final int month = calendar.get(Calendar.MONTH);

                CircleImageView circleImageView = (CircleImageView)view.findViewById(R.id.spares_image);
                final TextView price = (TextView)view.findViewById(R.id.spares_product_price);
                final TextView name = (TextView)view.findViewById(R.id.spares_product_name);

                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayPictureDialog(items.get(position));
                    }
                });
                ImageView buy = (ImageView)view.findViewById(R.id.spares_buy);
                buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child("users").child(items.get(position).getUniqueId()).child("phoneNumber").
                                addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        phoneNumber = dataSnapshot.getValue(String.class);
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        Sales sales = new Sales();
                        sales.setItemName(name.getText().toString());
                        sales.setMonth(month);
                        sales.setPrice(price.getText().toString());

                        databaseReference.child("sales").child(mAuth.getCurrentUser().getUid()).setValue(sales);
                    }
                });
                ImageView locate = (ImageView)view.findViewById(R.id.spares_locate);
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

    public Bitmap decodeImage(String imageFile){
        Bitmap bitmap = null;
        byte[] imageAsBytes = Base64.decode(imageFile, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

        return bitmap;
    }

    public void displayPictureDialog(Spares spares){

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_picture_layout,null);
        ImageView imageView = (ImageView)dialogView.findViewById(R.id.dialog_image);

        imageView.setImageBitmap(decodeImage(spares.getPic()));

        dialog.setView(dialogView);
        dialog.setTitle("SPARES");

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    public void listByName(String name){
        ArrayList<Spares> filteredItems = new ArrayList<>();

        for(Spares spares:items){
            if(spares.getName().equalsIgnoreCase(name) || spares.getLocation().equalsIgnoreCase(name)){
                filteredItems.add(spares);
            }
        }
        items.clear();
        items.addAll(filteredItems);
        sparesArrayAdapter.notifyDataSetChanged();

    }

    public void sortFromLowToHigh(){
        ArrayList<Spares> filteredItems = new ArrayList<>();
        int minimumValue = 0;

        for(Spares spares:items) {
            if (Integer.parseInt(spares.getPrice()) < minimumValue) {
                filteredItems.add(0, spares);
            } else {
                filteredItems.add(spares);
            }
            minimumValue = Integer.parseInt(spares.getPrice());
        }

        minimumValue = 0;

        items.clear();
        items.addAll(filteredItems);
        sparesArrayAdapter.notifyDataSetChanged();
    }

    public void sortFromHighToLow(){
        ArrayList<Spares> filteredItems = new ArrayList<>();
        int maximumValue = 0;

        for(Spares spares:items){
            if(Integer.parseInt(spares.getPrice()) > maximumValue){
                filteredItems.add(0,spares);
            }else{
                filteredItems.add(spares);
            }
            maximumValue = Integer.parseInt(spares.getPrice());
        }

        maximumValue = 0;

        items.clear();
        items.addAll(filteredItems);
        sparesArrayAdapter.notifyDataSetChanged();
    }

}
