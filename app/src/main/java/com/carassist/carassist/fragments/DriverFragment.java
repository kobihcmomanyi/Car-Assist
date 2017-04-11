package com.carassist.carassist.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.CarListArrayAdapter;
import com.carassist.carassist.adapters.DriverProfileArrayAdapter;
import com.carassist.carassist.data.Cars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2/27/2016.
 */
public class DriverFragment extends Fragment {

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ArrayList<Cars> items = new ArrayList<>();

    CheckBox tyres,toolKit,engineOil,water,windscreenWiper,screenWash,windscreen,light,powerSteering,bodyWork;
    Button mButton;

    CarListArrayAdapter carListArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver,container,false);

        tyres = (CheckBox)rootView.findViewById(R.id.tyres);
        toolKit = (CheckBox)rootView.findViewById(R.id.toolkit);
        engineOil = (CheckBox)rootView.findViewById(R.id.engine_oil);
        water = (CheckBox)rootView.findViewById(R.id.water);
        windscreenWiper = (CheckBox)rootView.findViewById(R.id.windscreen_wipers);
        screenWash = (CheckBox)rootView.findViewById(R.id.screenwash);
        windscreen = (CheckBox)rootView.findViewById(R.id.windscreen);
        light = (CheckBox)rootView.findViewById(R.id.light);
        powerSteering = (CheckBox)rootView.findViewById(R.id.power_steering);
        bodyWork = (CheckBox)rootView.findViewById(R.id.body_work);

        mButton = (Button)rootView.findViewById(R.id.maintenance_advice_button);

        //get an instance of the firebase authentication service and database service
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        carListArrayAdapter = new CarListArrayAdapter(getActivity(),items);

        //create the database query
        com.google.firebase.database.Query queryRef = databaseReference.child("cars").orderByChild("uniqueId").equalTo(mAuth.getCurrentUser().getUid());
        queryRef.keepSynced(true);

        GridView gridView = (GridView)rootView.findViewById(R.id.cars_list);
        final TextView mileage = (TextView)rootView.findViewById(R.id.mileage);

        //listen to any changes made to the db as a result of CRUD operation
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //remove all items in the ArrayList
                items.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Cars cars = snapshot.getValue(Cars.class);
                    items.add(cars);

                }

                carListArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gridView.setAdapter(carListArrayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                CheckBox checkBox = (CheckBox) view.findViewById(R.id.car_list_checkbox);
                if(checkBox.isChecked()){
                    checkBox.setChecked(true);
                    mileage.setText(items.get(position).getMileage().toUpperCase());
                }else{
                    checkBox.setChecked(false);
                    mileage.setText("");
                }
            }
        });

        tyres.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        toolKit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tyres.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        engineOil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    tyres.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        water.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    tyres.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        windscreenWiper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    tyres.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        screenWash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    tyres.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        windscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    tyres.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    tyres.setChecked(false);
                    powerSteering.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        powerSteering.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    tyres.setChecked(false);
                    bodyWork.setChecked(false);

                }
            }
        });

        bodyWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toolKit.setChecked(false);
                    engineOil.setChecked(false);
                    water.setChecked(false);
                    windscreenWiper.setChecked(false);
                    screenWash.setChecked(false);
                    windscreen.setChecked(false);
                    light.setChecked(false);
                    powerSteering.setChecked(false);
                    tyres.setChecked(false);

                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tyres.isChecked()){
                    showMessageDialog("Correct tyre pressure reduces the risk of losing control " +
                            "of your vehicle. It also protects your tyres from premature wear and" +
                            " irreversible damage to the internal construction. Change your tyres " +
                            "before your tread depth is worn to 1.6mm.");
                }
                else if(toolKit.isChecked()){
                    showMessageDialog("Confirm that you have a basic toolkit including a first aid kit" +
                            " in your vehicle at all times");
                }
                else if(engineOil.isChecked()){
                    showMessageDialog("Always use the engine oil recommended by the manufacturer. Confirm" +
                            " that oil levels are accurate and there are no leaks");
                }
                else if(water.isChecked()){
                    showMessageDialog("Check that coolant levels are accurate and no leaks are present.");
                }
                else if(windscreen.isChecked()){
                    showMessageDialog("Replace windscreen if any cracks are visible");
                }
                else if(light.isChecked()){
                    showMessageDialog("Always check for warning signs of lamp failure");
                }
                else if(powerSteering.isChecked()){
                    showMessageDialog("Check power steering fluid levels at every minor service. Confirm" +
                            " that no leaks are present");
                }
                else if(bodyWork.isChecked()){
                    showMessageDialog("Check for physical damage of bodywork especially the undercarriage." +
                            " Also check for presence of oil or brake fluid leaks in the event of physical damage. ");
                }
                else if(windscreenWiper.isChecked()){
                    showMessageDialog("Change wiper blades after every 4 months");
                }
            }
        });

        return rootView;
    }

    //create an alert dialog to show the messages about car maintenance
    public void showMessageDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        builder.setMessage(message);
        builder.setTitle("Maintenance");

        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

}
