package com.carassist.carassist.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.data.Cars;
import com.carassist.carassist.data.Garage;
import com.carassist.carassist.data.Spares;
import com.carassist.carassist.fragments.DriverProfileFragment;
import com.carassist.carassist.fragments.GarageOwnerProfileFragment;
import com.carassist.carassist.fragments.MyProfileFragment;
import com.carassist.carassist.fragments.SparesProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    //create an instance of the firebase database

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    Spares spares = new Spares();
    String category = "";
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //creates an instance of the firebase auth and database

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //gets a message from the HomeActivity checks the message for appropriate action

            category = getIntent().getStringExtra("PROFILECATEGORY");

        switch(category){
            case "spares":
                fab.setVisibility(View.VISIBLE);

                //sets the title of the action bar

                getSupportActionBar().setTitle("Spares");
                setFragment(new SparesProfileFragment());
                break;
            case "garage":
                fab.setVisibility(View.VISIBLE);

                //set the title of the action bar

                getSupportActionBar().setTitle("Garage");
                setFragment(new GarageOwnerProfileFragment());
                break;
            case "driver":
                fab.setVisibility(View.VISIBLE);

                //set the title of the action bar

                getSupportActionBar().setTitle("Driver");
                setFragment(new DriverProfileFragment());
                break;
            case "myProfile":
                fab.setVisibility(View.GONE);

                //set the title of the action bar

                getSupportActionBar().setTitle("My Profile");
                setFragment(new MyProfileFragment());
                break;
            default:
                setFragment(null);
                break;
        }

        //listen to the click of the FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checks the selected category and navigates to the appropriate activity (screen)
                if (category.equalsIgnoreCase("spares")) {
                    //sparesDialog();
                    Intent intent = new Intent(getApplicationContext(),AddSparesActivity.class);

                    intent.putExtra("name","");
                    intent.putExtra("description","");
                    intent.putExtra("location","");
                    intent.putExtra("price","");
                    intent.putExtra("mode","");
                    intent.putExtra("pushId","");

                    startActivity(intent);

                } else if (category.equalsIgnoreCase("garage")) {
                    garageDialog();
                } else {
                    carDialog();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.profile_fragment,fragment);
        fragmentTransaction.commit();

    }

    //dialog to enter car details

    public void carDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_car_layout,null);
        dialog.setView(dialogView);
        dialog.setTitle("ADD CAR");

        final View colorBox = dialogView.findViewById(R.id.add_car_body_paint_color_box);
        final SeekBar redSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_red);
        final SeekBar greenSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_green);
        final SeekBar blueSeekBar = (SeekBar)dialogView.findViewById(R.id.add_car_body_paint_blue);

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                colorBox.setBackgroundColor(Color.rgb(redSeekBar.getProgress(),greenSeekBar.getProgress(),progress));

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

                colorBox.setBackgroundColor(Color.rgb(redSeekBar.getProgress(),progress,blueSeekBar.getProgress()));

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

                colorBox.setBackgroundColor(Color.rgb(progress,greenSeekBar.getProgress(),blueSeekBar.getProgress()));

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
                String colorHex = String.format("#%02x%02x%02x",redSeekBar.getProgress(),greenSeekBar.getProgress(),blueSeekBar.getProgress());

                //initialize dialog views
                MaterialEditText carModel = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_car_car_model);
                MaterialEditText bodyType = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_car_body_type);
                MaterialEditText bodyPaint = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_car_body_paint);
                MaterialEditText mileage = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_car_mileage);
                MaterialEditText description = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_car_other_description);

                RadioGroup radioGroup = (RadioGroup) ((AlertDialog)dialog).findViewById(R.id.add_car_body_type_radio_button);
                RadioButton radioButton = (RadioButton) ((AlertDialog)dialog).findViewById(radioGroup.getCheckedRadioButtonId());
                bodyType.setText(radioButton.getText());
                bodyPaint.setText(colorHex);

                DatabaseReference carReference = databaseReference.child("cars").push();

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
                            Toast.makeText(getApplicationContext(), "an error occurred,please try again", Toast.LENGTH_SHORT).show();

                        } else {
                            //data was saved successfully
                            Toast.makeText(getApplicationContext(), "data saved successfully", Toast.LENGTH_SHORT).show();
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

    public void garageDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.MyDialogTheme);
        dialog.setView(R.layout.add_garage_layout);
        dialog.setTitle("ADD GARAGE");
        dialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //initialize dialog views
                MaterialEditText name = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_garage_name);
                MaterialEditText description = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_garage_description);
                MaterialEditText location = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_garage_location);
                MaterialEditText phoneNumber = (MaterialEditText) ((AlertDialog) dialog).findViewById(R.id.add_garage_phoneNumber);

                DatabaseReference garageReference = databaseReference.child("garage").push();

                Garage garage = new Garage();
                garage.setName(name.getText().toString());
                garage.setLocation(location.getText().toString());
                garage.setDescription(description.getText().toString());
                garage.setPhoneNumber(phoneNumber.getText().toString());
                garage.setUniqueId(mAuth.getCurrentUser().getUid());
                garage.setPushId(garageReference.getKey());

                garageReference.setValue(garage, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //an error occurred
                            Toast.makeText(getApplicationContext(), "an error occurred,please try again", Toast.LENGTH_SHORT).show();

                        } else {
                            //data was saved successfully
                            Toast.makeText(getApplicationContext(), "data saved successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
