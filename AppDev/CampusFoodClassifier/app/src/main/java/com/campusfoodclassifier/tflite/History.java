package com.campusfoodclassifier.tflite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import java.util.HashSet;
import java.util.Set;

public class History extends AppCompatActivity {

    public TextView historyText;
    public Button btnBack;
    public TextView spendingText;
    public TextView sugarText;
    public TextView caloriesText;


    public static final String SHARED_PREFS = "SharedPrefs";
    public static final Set<String> HISTORY = new HashSet<>();
    public static final String TEXT = "text";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);




        Slidr.attach(this);

        historyText = findViewById(R.id.historyText);
        btnBack = findViewById(R.id.btnBack);


        spendingText = findViewById(R.id.Spending);
        sugarText = findViewById(R.id.Sugar);
        caloriesText = findViewById(R.id.Calories);

        spendingText.setTextColor(Color.parseColor("#BDC6BC"));
        sugarText.setTextColor(Color.parseColor("#BDC6BC"));
        caloriesText.setTextColor(Color.parseColor("#BDC6BC"));

        String[] totals = loadTotals();
        //String spendingTotal = Float.toString(sharedPreferences.getFloat(MainActivity.SPENDINGKEY, 0));
        spendingText.setText("Spending:         £"+totals[0]);

        //String sugarTotal = Float.toString(sharedPreferences.getFloat(MainActivity.SUGARKEY, 0));
        sugarText.setText("Sugar:                  "+totals[1]+"g");

        //String caloriesTotal = Float.toString(sharedPreferences.getFloat(MainActivity.CALORIESKEY, 0));
        caloriesText.setText("Calories:             "+totals[2]+" cals");




        historyText.setText("Your last item was "+loadLastItem());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }


    protected String loadLastItem() {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(MainActivity.TEXT, "...well nothing yet!");

    }

    protected String[] loadTotals() {
        String[] totals = new String[3];
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        float priceAsFloat = sharedPreferences.getFloat(MainActivity.SPENDINGKEY, 0);
        double doubleFormat = Math.round(priceAsFloat * 100.0) / 100.0;
        totals[0] = Double.toString(doubleFormat);
        totals[1] = Float.toString(sharedPreferences.getFloat(MainActivity.SUGARKEY, 0));
        totals[2] = Integer.toString( Math.round(sharedPreferences.getFloat(MainActivity.CALORIESKEY, 0)));

        return totals;

    }





}
