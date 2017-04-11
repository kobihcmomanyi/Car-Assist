package com.carassist.carassist.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.adapters.HomeViewPagerFragmentAdapter;
import com.carassist.carassist.data.AuthCredentials;
import com.carassist.carassist.data.Garage;
import com.carassist.carassist.fragments.DriverFragment;
import com.carassist.carassist.fragments.GarageOwnerFragment;
import com.carassist.carassist.fragments.SparesFragment;
import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;

public class HomeActivity extends AppCompatActivity{

    MorphyToolbar morphyToolbar;
    ViewPager viewPager;
    ResideMenu resideMenu;
    ResideMenuItem myProfile,driverProfile,garageProfile,sparesProfile,analytics,logOut;
    TabLayout tabLayout;
    MaterialSearchView materialSearchView;
    HomeViewPagerFragmentAdapter viewPagerAdapter;

    String path;
    String databaseName;
    String password;
    WaspDb db;
    WaspHash userDetails;

    GarageOwnerFragment garageOwnerFragment;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        path = getFilesDir().getPath();
        databaseName = "Car Assist DB";
        password = "qwertyuiop";
        db = WaspFactory.openOrCreateDatabase(path,databaseName,password);
        userDetails = db.openOrCreateHash("userDetails");

        garageOwnerFragment = new GarageOwnerFragment();
        tabLayout = (TabLayout)findViewById(R.id.home_tab);
        viewPager = (ViewPager)findViewById(R.id.home_view_pager);
        materialSearchView = (MaterialSearchView)findViewById(R.id.activity_home_search_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        morphyToolbar = MorphyToolbar.builder(this,toolbar)
                .withToolbarAsSupportActionBar()
                .withTitle("")
                .withSubtitle("")
                .build();

        setSupportActionBar(toolbar);

        viewPagerAdapter = new HomeViewPagerFragmentAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (morphyToolbar.isCollapsed()) {
                //  morphyToolbar.expand();
                //} else {
                //  morphyToolbar.collapse();
                //}
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Car Assist");

        //ResideMenu
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.color.colorPrimary);
        resideMenu.attachToActivity(this);
        resideMenu.addIgnoredView(viewPager);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        //create menuItems
        final String titles[] = {"Driver","Garage","Spares","Logout","My Profile","Analytics"};
        int icons[] = {R.drawable.ic_driver,R.drawable.ic_garage,R.drawable.ic_spares,R.drawable.ic_logout,R.mipmap.ic_pofilepic,R.drawable.ic_analytics};

        //ResideMenuItem

        myProfile = new ResideMenuItem(this,icons[4],titles[4]);
        driverProfile = new ResideMenuItem(this,icons[0],titles[0]);
        garageProfile = new ResideMenuItem(this,icons[1],titles[1]);
        sparesProfile = new ResideMenuItem(this,icons[2],titles[2]);
        logOut = new ResideMenuItem(this,icons[3],titles[3]);
        analytics = new ResideMenuItem(this,icons[5],titles[5]);

        resideMenu.addMenuItem(myProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(driverProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(garageProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(sparesProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(analytics, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(logOut, ResideMenu.DIRECTION_LEFT);

        driverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("PROFILECATEGORY", "driver");
                startActivity(intent);
            }
        });
        garageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                intent.putExtra("PROFILECATEGORY", "garage");
                startActivity(intent);
            }
        });
        sparesProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("PROFILECATEGORY", "spares");
                startActivity(intent);
            }
        });
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("PROFILECATEGORY", "myProfile");
                startActivity(intent);
            }
        });
        analytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AnalyticsActivity.class);
                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        //search event listeners
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                switch(viewPager.getCurrentItem()){
                    case 0:
                        DriverFragment driverFragment = (DriverFragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        break;
                    case 1:
                        GarageOwnerFragment garageOwnerFragment = (GarageOwnerFragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        garageOwnerFragment.listByName(query);
                        break;
                    case 2:
                        SparesFragment sparesFragment = (SparesFragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        sparesFragment.listByName(query);
                        break;
                    default:
                        break;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        //retrieves user details from the firebase database.
        databaseReference.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                AuthCredentials authCredentials = dataSnapshot.getValue(AuthCredentials.class);
                userDetails.put("name", authCredentials.getName());
                userDetails.put("userName", authCredentials.getUsername());
                userDetails.put("email", authCredentials.getEmail());
                userDetails.put("phoneNumber", authCredentials.getPhoneNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //inflates the right hand side corner button with the menu items.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //inflate the menu items

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.analytics_items, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        materialSearchView.setMenuItem(menuItem);

        return super.onCreateOptionsMenu(menu);
    }

    //handle right hand button menu item clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            //opens the menu

            case android.R.id.home:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                return true;
            case R.id.sort_by_price_high_to_low:

                //sort the items according to price from highest to lowest

                switch(viewPager.getCurrentItem()){
                    case 2:
                        SparesFragment sparesFragment = (SparesFragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        sparesFragment.sortFromHighToLow();
                        break;
                    default:
                        break;
                }
                return true;
            case R.id.sort_by_price_low_to_high:

                //sort the items according to price from lowest to highest

                switch(viewPager.getCurrentItem()){
                    case 2:
                        SparesFragment sparesFragment = (SparesFragment)viewPager.getAdapter().instantiateItem(viewPager,viewPager.getCurrentItem());
                        sparesFragment.sortFromLowToHigh();
                        break;
                    default:
                        break;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
