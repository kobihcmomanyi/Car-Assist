package com.carassist.carassist.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.carassist.carassist.R;
import com.carassist.carassist.data.Sales;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 4/9/2017.
 */

public class AnalyticsActivity extends AppCompatActivity {

    //create the bar chart object

    BarChart barChart;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ArrayList<BarEntry> salesEntries = new ArrayList<>();
    int janSales = 10,febSales = 20,marSales = 0,aprSales = 0,maySales = 0,junSales = 0,
            julSales = 0,augSales = 40,sepSales = 60,octSales = 10,novSales = 0,decSales = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Analytics");

        //create an instance of the firebase db

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //create an instance of the bar chart

        barChart = (BarChart)findViewById(R.id.analytics_bar_graph);

        /*databaseReference.child("sales").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salesEntries.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Sales sales = postSnapshot.getValue(Sales.class);
                    int month = sales.getMonth();
                    int month = 0;
                    switch(month){
                        case 0:
                            janSales++;
                            break;
                        case 1:
                            febSales++;
                            break;
                        case 2:
                            marSales++;
                            break;
                        case 3:
                            aprSales++;
                            break;
                        case 4:
                            maySales++;
                            break;
                        case 5:
                            junSales++;
                            break;
                        case 6:
                            julSales++;
                            break;
                        case 7:
                            augSales++;
                            break;
                        case 8:
                            sepSales++;
                            break;
                        case 9:
                            octSales++;
                            break;
                        case 10:
                            novSales++;
                            break;
                        case 11:
                            decSales++;
                            break;
                        default:
                            break;
                    }
                }
                salesEntries.add(new BarEntry(0,janSales));
                salesEntries.add(new BarEntry(1,febSales));
                salesEntries.add(new BarEntry(2,marSales));
                salesEntries.add(new BarEntry(3,aprSales));
                salesEntries.add(new BarEntry(4,maySales));
                salesEntries.add(new BarEntry(5,junSales));
                salesEntries.add(new BarEntry(6,julSales));
                salesEntries.add(new BarEntry(7,augSales));
                salesEntries.add(new BarEntry(8,sepSales));
                salesEntries.add(new BarEntry(9,octSales));
                salesEntries.add(new BarEntry(10,novSales));
                salesEntries.add(new BarEntry(11,decSales));

                barChart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"An error occurred while retrieving data",Toast.LENGTH_SHORT).show();
            }
        });*/

        //create the individuals bars and populate them with data from Jan to Dec

        salesEntries.add(new BarEntry(0,janSales));
        salesEntries.add(new BarEntry(1,febSales));
        salesEntries.add(new BarEntry(2,marSales));
        salesEntries.add(new BarEntry(3,aprSales));
        salesEntries.add(new BarEntry(4,maySales));
        salesEntries.add(new BarEntry(5,junSales));
        salesEntries.add(new BarEntry(6,julSales));
        salesEntries.add(new BarEntry(7,augSales));
        salesEntries.add(new BarEntry(8,sepSales));
        salesEntries.add(new BarEntry(9,octSales));
        salesEntries.add(new BarEntry(10,novSales));
        salesEntries.add(new BarEntry(11,decSales));

        BarDataSet barDataSet = new BarDataSet(salesEntries,"No. of items sold");
        barDataSet.setDrawValues(false);

        //populate the x-axis of the bar chart with months

        final ArrayList<String> monthsLabels = new ArrayList<>();
        monthsLabels.add("Jan");
        monthsLabels.add("Feb");
        monthsLabels.add("March");
        monthsLabels.add("Apr");
        monthsLabels.add("May");
        monthsLabels.add("Jun");
        monthsLabels.add("Jul");
        monthsLabels.add("Aug");
        monthsLabels.add("Sep");
        monthsLabels.add("Oct");
        monthsLabels.add("Nov");
        monthsLabels.add("Dec");

        XAxis milkYieldXAxis = barChart.getXAxis();
        milkYieldXAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date = "";

                switch((int)value){
                    case 0:
                        date = monthsLabels.get(0);
                        break;
                    case 1:
                        date = monthsLabels.get(1);
                        break;
                    case 2:
                        date = monthsLabels.get(2);
                        break;
                    case 3:
                        date = monthsLabels.get(3);
                        break;
                    case 4:
                        date = monthsLabels.get(4);
                        break;
                    case 5:
                        date = monthsLabels.get(5);
                        break;
                    case 6:
                        date = monthsLabels.get(6);
                        break;
                    case 7:
                        date = monthsLabels.get(7);
                        break;
                    case 8:
                        date = monthsLabels.get(8);
                        break;
                    case 9:
                        date = monthsLabels.get(9);
                        break;
                    case 10:
                        date = monthsLabels.get(10);
                        break;
                    case 11:
                        date = monthsLabels.get(11);
                        break;
                    default:
                        break;
                }

                return date;
            }

        });

        BarData barData = new BarData(barDataSet);

        //set bar chart properties

        barChart.getDescription().setText("Monthly Sales");
        barChart.setData(barData);
        barChart.animateXY(3000,3000);
        barChart.invalidate();

    }
}
