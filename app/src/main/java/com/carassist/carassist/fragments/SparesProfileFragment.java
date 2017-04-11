package com.carassist.carassist.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.carassist.carassist.R;
import com.carassist.carassist.activities.AddSparesActivity;
import com.carassist.carassist.adapters.SparesProfileArrayAdapter;
import com.carassist.carassist.data.Spares;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2/28/2016.
 */
public class SparesProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ArrayList<Spares> items = new ArrayList<>();

    SparesProfileArrayAdapter sparesProfileArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spares_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        sparesProfileArrayAdapter = new SparesProfileArrayAdapter(getActivity(),items);

        DatabaseReference sparesReference = databaseReference.child("spares");
        Query queryRef = sparesReference.orderByChild("uniqueId").equalTo(mAuth.getCurrentUser().getUid());
        queryRef.keepSynced(true);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Spares spares = snapshot.getValue(Spares.class);
                    items.add(spares);
                }

                sparesProfileArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.fragment_spares_profile_empty_state);
        ListView listView = (ListView)rootView.findViewById(R.id.fragment_spares_profile_list_view);

        listView.setAdapter(sparesProfileArrayAdapter);

        linearLayout.setVisibility(View.GONE);

        //add footer to the listView
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footerView = layoutInflater.inflate(R.layout.listview_footer,null);

        listView.addFooterView(footerView);

        //listen to listView clicks/events
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final DatabaseReference deleteSparesReference = databaseReference.child("spares").child(items.get(position).getPushId());

                ImageView delete = (ImageView)view.findViewById(R.id.spares_delete);
                ImageView edit = (ImageView)view.findViewById(R.id.spares_edit);
                CircleImageView circleImageView = (CircleImageView)view.findViewById(R.id.spares_image);

                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayPictureDialog(items.get(position));
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Spares spares = items.get(position);

                        Intent intent = new Intent(getActivity(), AddSparesActivity.class);
                        intent.putExtra("mode","edit");
                        intent.putExtra("name",spares.getName());
                        intent.putExtra("description",spares.getDescription());
                        intent.putExtra("location",spares.getLocation());
                        intent.putExtra("price",spares.getPrice());
                        intent.putExtra("pushId",spares.getPushId());

                        startActivity(intent);

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
                                    Snackbar.make(v,"item has been deleted",Snackbar.LENGTH_SHORT).show();
                                    sparesProfileArrayAdapter.notifyDataSetChanged();

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

}
