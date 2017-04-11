package com.carassist.carassist.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.data.AuthCredentials;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class MainActivity extends AppCompatActivity {

    CardView cardView,middleCardView,outerCardView;
    FloatingActionButton signUpFab;
    ImageView cancelButton;
    MaterialEditText signUpName,signUpUsername,signUpEmail,signUpPhoneNumber,signUpPassword,signUpRepeatPassword;
    MaterialEditText loginEmail,loginPassword;
    Button signUpButton,loginButton;
    ProgressDialog progressDialog;

    //creates an instance of AuthCredentials which will contain the user details entered during
    //the sign up process
    AuthCredentials authCredentials = new AuthCredentials();

    //creates an instance of the database
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user has signed in
                    //user authenticated
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //user has signed out

                }
            }
        };

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("loading...");
        progressDialog.setCancelable(false);

        cardView = (CardView)findViewById(R.id.card_view);
        middleCardView = (CardView)findViewById(R.id.middle_card_view);
        outerCardView = (CardView)findViewById(R.id.outer_card_view);

        signUpFab = (FloatingActionButton)findViewById(R.id.sign_up_fab);
        cancelButton = (ImageView)findViewById(R.id.cancel_image_view);

        signUpName = (MaterialEditText)findViewById(R.id.sign_up_name);
        signUpUsername = (MaterialEditText)findViewById(R.id.sign_up_username);
        signUpEmail = (MaterialEditText)findViewById(R.id.sign_up_email);
        signUpPhoneNumber = (MaterialEditText)findViewById(R.id.sign_up_phone_number);
        signUpPassword = (MaterialEditText)findViewById(R.id.sign_up_password);
        signUpRepeatPassword = (MaterialEditText)findViewById(R.id.sign_up_repeat_password);
        signUpButton = (Button)findViewById(R.id.sign_up_button);

        loginButton = (Button)findViewById(R.id.login_button);
        loginEmail = (MaterialEditText)findViewById(R.id.login_email);
        loginPassword = (MaterialEditText)findViewById(R.id.login_password);

        signUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outerCardView.setVisibility(View.VISIBLE);
                setViewMargins();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outerCardView.setVisibility(View.INVISIBLE);
                setViewMargins();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
        converts values from pixels to density pixels so that they can be used to resize the views
         */
    public int pxToDp(int pixels){
        int densityPixels = 0;

        densityPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,pixels,getResources().getDisplayMetrics());

        return densityPixels;
    }

    /*
     resizes the margins of the views
     */
    public void setViewMargins(){

        RevealFrameLayout.LayoutParams cardViewLayoutParams = new RevealFrameLayout.LayoutParams(RevealFrameLayout.LayoutParams.MATCH_PARENT, RevealFrameLayout.LayoutParams.MATCH_PARENT);
        RevealFrameLayout.LayoutParams middleCardViewLayoutParams = new RevealFrameLayout.LayoutParams(RevealFrameLayout.LayoutParams.MATCH_PARENT, RevealFrameLayout.LayoutParams.MATCH_PARENT);


        if(outerCardView.getVisibility() == View.INVISIBLE){

            cardViewLayoutParams.setMargins(pxToDp(5),pxToDp(0),pxToDp(5),pxToDp(0));
            middleCardViewLayoutParams.setMargins(pxToDp(0),pxToDp(5),pxToDp(0),pxToDp(0));

            cardView.setLayoutParams(cardViewLayoutParams);
            middleCardView.setLayoutParams(middleCardViewLayoutParams);

        }else if(outerCardView.getVisibility() == View.VISIBLE){

            cardViewLayoutParams.setMargins(pxToDp(10),pxToDp(0),pxToDp(10),pxToDp(0));
            middleCardViewLayoutParams.setMargins(pxToDp(5),pxToDp(5),pxToDp(5),pxToDp(0));

            cardView.setLayoutParams(cardViewLayoutParams);
            middleCardView.setLayoutParams(middleCardViewLayoutParams);

        }

    }

    public void signUp(){

        //checks whether user has entered all the details without leaving any field blank
        if(signUpEmail.getText().toString().equalsIgnoreCase("") || signUpName.getText().toString().equalsIgnoreCase("")
                || signUpPassword.getText().toString().equalsIgnoreCase("") || signUpRepeatPassword.getText().toString().equalsIgnoreCase("")
                || signUpPhoneNumber.getText().toString().equalsIgnoreCase("") || signUpUsername.getText().toString().equalsIgnoreCase("")){

            //prompt user to enter details
            Toast.makeText(getApplicationContext(),"enter details",Toast.LENGTH_SHORT).show();

        }else{

            //if all details are set, an account for the user is created
            progressDialog.show();
            if(signUpPassword.getText().toString().equals(signUpRepeatPassword.getText().toString())){

                authCredentials.setEmail(signUpEmail.getText().toString());
                authCredentials.setName(signUpName.getText().toString());
                authCredentials.setPassword(signUpPassword.getText().toString());
                authCredentials.setPhoneNumber(signUpPhoneNumber.getText().toString());
                authCredentials.setUsername(signUpUsername.getText().toString());

                mAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(), signUpPassword.getText().toString()).
                        addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    //account successfully created
                                    authCredentials.setUniqueId(task.getResult().getUser().getUid());
                                    databaseReference.child("users").child(task.getResult().getUser().getUid()).setValue(authCredentials, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                //an error occurred during sign up

                                            } else {
                                                //success

                                            }
                                        }
                                    });

                                } else {
                                    //account creation failed

                                }
                            }
                        });

            }else{
                //password do not match

            }

        }

    }

    public void logIn(){

        //checks if the user has entered details in all the fields
        if(loginEmail.getText().toString().equalsIgnoreCase("") || loginPassword.getText().toString().equalsIgnoreCase("")){
            //prompt user to enter details
            Toast.makeText(getApplicationContext(),"enter details",Toast.LENGTH_SHORT).show();

        }else{

            progressDialog.show();
            //authenticates the user
            mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString()).
                    addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                //user has logged in successfully
                                //if the user is authenticated he is directed to the 'HomePage'

                            } else {
                                //user has failed to log in

                            }
                        }
                    });

        }

    }

}
