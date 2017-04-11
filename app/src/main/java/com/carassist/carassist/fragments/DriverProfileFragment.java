package com.carassist.carassist.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.DriverProfileArrayAdapter;
import com.carassist.carassist.data.Cars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * Created by user on 2/28/2016.
 */
public class DriverProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ArrayList<Cars> items = new ArrayList<>();

    DriverProfileArrayAdapter driverProfileArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        //creates an instance of the firebase auth service and database
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        driverProfileArrayAdapter = new DriverProfileArrayAdapter(getActivity(),items);

        com.google.firebase.database.Query queryRef = databaseReference.child("cars").orderByChild("uniqueId").equalTo(mAuth.getCurrentUser().getUid());
        queryRef.keepSynced(true);

        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.fragment_driver_profile_empty_state);
        ListView listView = (ListView)rootView.findViewById(R.id.fragment_driver_profile_list_view);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Cars cars = snapshot.getValue(Cars.class);
                    items.add(cars);

                }

                driverProfileArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setAdapter(driverProfileArrayAdapter);

        linearLayout.setVisibility(View.GONE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final DatabaseReference deleteSparesReference = databaseReference.child("cars").child(items.get(position).getPushId());

                ImageView delete = (ImageView) view.findViewById(R.id.driver_delete);
                ImageView edit = (ImageView) view.findViewById(R.id.driver_edit);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cars cars = items.get(position);
                        carDialog(cars);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        deleteSparesReference.setValue(null, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    //item deleted successfully
                                    Snackbar.make(v, "item has been deleted", Snackbar.LENGTH_SHORT).show();
                                    driverProfileArrayAdapter.notifyDataSetChanged();
                                } else {
                                    //an error occurred
                                    Snackbar.make(v, "an error occurred, try again", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });

        return rootView;
    }

    public void carDialog(final Cars cars){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_car_layout,null);

        dialog.setView(dialogView);
        dialog.setTitle("ADD CAR");

        //initialize dialog views
        final MaterialEditText carModel = (MaterialEditText) dialogView.findViewById(R.id.add_car_car_model);
        final MaterialEditText bodyType = (MaterialEditText) dialogView.findViewById(R.id.add_car_body_type);
        final MaterialEditText bodyPaint = (MaterialEditText) dialogView.findViewById(R.id.add_car_body_paint);
        final MaterialEditText mileage = (MaterialEditText) dialogView.findViewById(R.id.add_car_mileage);
        final MaterialEditText description = (MaterialEditText) dialogView.findViewById(R.id.add_car_other_description);

        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.add_car_body_type_radio_button);
        final RadioButton radioButton = (RadioButton) dialogView.findViewById(radioGroup.getCheckedRadioButtonId());

        final View colorBox = dialogView.findViewById(R.id.add_car_body_paint_color_box);
        final SeekBar redSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_red);
        final SeekBar greenSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_green);
        final SeekBar blueSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_blue);

        carModel.setText(cars.getCarModel());
        mileage.setText(cars.getMileage());
        description.setText(cars.getDescription());

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                colorBox.setBackgroundColor(Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                colorBox.setBackgroundColor(Color.rgb(redSeekBar.getProgress(), progress, blueSeekBar.getProgress()));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                colorBox.setBackgroundColor(Color.rgb(progress, greenSeekBar.getProgress(), blueSeekBar.getProgress()));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //convert rgb color to hexadecimal
                String colorHex = String.format("#%02x%02x%02x", redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());

                bodyType.setText(radioButton.getText().toString());
                bodyPaint.setText(colorHex);

                DatabaseReference carReference = databaseReference.child("cars").child(cars.getPushId());

                Cars cars = new Cars();
                cars.setCarModel(carModel.getText().toString());
                cars.setBodyType(bodyType.getText().toString());
                cars.setBodyPaint(bodyPaint.getText().toString());
                cars.setDescription(description.getText().toString());
                cars.setMileage(mileage.getText().toString());
                cars.setUniqueId(mAuth.getCurrentUser().getUid());
                cars.setPushId(carReference.getKey());

                carReference.setValue(cars, new DatabaseReference.CompletionListener() {
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
