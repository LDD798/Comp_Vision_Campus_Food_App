package com.campusfoodclassifier.tflite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;

import java.util.HashSet;
import java.util.Set;

public class LocationActivity extends AppCompatActivity {

    public TextView predictionResult;
    public Button btnCosta;
    public Button btnCS;
    public Button btnHome;
    public Button btnResult;

    String location = "";

    public static int predictionIndex = -1;


    public BayesTable bayesTable;


    Nutrition apple =  new Nutrition("Apple", 106.8f, 0.27f, 10);
    Nutrition banana = new Nutrition("Banana", 61.57f, 0.2f, 12);
    Nutrition coffeeMedium = new Nutrition("Coffee Medium", 61.57f, 14.7f);
    Nutrition coffeeSmall = new Nutrition("Coffee Small", 61.57f, 2.50f, 10.6f);
    Nutrition coffeeUOB = new Nutrition("Coffee UOB", 61.57f, 2, 10.6f);
    Nutrition crispsBlue = new Nutrition("Crisps Sea Salt", 61.57f, 1, 0.2f);
    Nutrition crispsGreen = new Nutrition("Crisps Salt&Vinegar", 61.57f, 1, 0.4f);
    Nutrition orange = new Nutrition("Orange", 61.57f, 0.3f, 9);
    Nutrition juice = new Nutrition("Juice", 61.57f, 0.30f, 53);

    private float totalSpending;
    private float totalSugar;
    private float totalCalories;

    public static final String SHARED_PREFS = "SharedPrefs";
    public static final Set<String> HISTORY = new HashSet<>();;
    public static final String TEXT = "text";
    public static final String SPENDINGKEY = "SpendingKey";
    public static final String SUGARKEY = "SugarKey";
    public static final String CALORIESKEY = "CaloriesKey";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        //if (tableExists) {
            //get table
        //else
        bayesTable = new BayesTable();



        Slidr.attach(this);

        predictionResult = findViewById(R.id.btnResult);
        btnCosta = findViewById(R.id.btnCosta);
        btnCS = findViewById(R.id.btnCS);
        btnHome = findViewById(R.id.btnHome);

        updatePredictionText();

        switch (MainActivity.locationIndex) {
            case 0:
                btnCosta.setTextColor(Color.parseColor("#FF5722"));
                break;
            case 1:
                btnCS.setTextColor(Color.parseColor("#FF5722"));
                break;
            case 2:
                btnHome.setTextColor(Color.parseColor("#FF5722"));
                break;
        }


        btnCosta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Costa";
                MainActivity.locationIndex = 0;
                updatePredictionText();

                btnCosta.setTextColor(Color.parseColor("#FF5722"));
                btnCS.setTextColor(Color.parseColor("#000000"));
                btnHome.setTextColor(Color.parseColor("#000000"));

            }
        });

        btnCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "CS";
                MainActivity.locationIndex = 1;
                updatePredictionText();

                btnCosta.setTextColor(Color.parseColor("#000000"));
                btnCS.setTextColor(Color.parseColor("#FF5722"));
                btnHome.setTextColor(Color.parseColor("#000000"));

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = "Home";
                MainActivity.locationIndex = 2;
                updatePredictionText();


                btnCosta.setTextColor(Color.parseColor("#000000"));
                btnCS.setTextColor(Color.parseColor("#000000"));
                btnHome.setTextColor(Color.parseColor("#FF5722"));
            }
        });

        predictionResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (predictionIndex > -1) {
                    //save the item
                    saveNutrition(predictionIndex);
                    toastMessage("Saved ");
                    predictionResult.setText(predictionResult.getText()+" ✓");
                    predictionResult.setTextColor(Color.parseColor("#BDC6BC"));//#3FC82C
                }


                btnCosta.setTextColor(Color.parseColor("#000000"));
                btnCS.setTextColor(Color.parseColor("#000000"));
                btnHome.setTextColor(Color.parseColor("#FF5722"));
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }

    private void updatePredictionText() {
        if (MainActivity.locationIndex > -1 && MainActivity.tableUpdated) {
            String prediction = bayesTable.getPrediction(MainActivity.locationIndex);

            predictionResult.setText(prediction);
        }
        else{
            predictionResult.setText("No Data ");
        }
    }



    private void toastMessage(String m) {
        Toast toast = Toast.makeText(this, m, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 350);
        toast.show();
    }


    public void saveNutrition(int item) {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        totalSpending = sharedPreferences.getFloat(SPENDINGKEY, 0);
        totalSugar = sharedPreferences.getFloat(SUGARKEY, 0);
        totalCalories = sharedPreferences.getFloat(CALORIESKEY, 0);
        String itemString = "";
        Nutrition itemObj;
        switch (item) {
            case 0:
                itemObj = apple;
                itemString = "Apple";
                break;
            case 1:
                itemObj = banana;
                itemString = "Banana";
                break;
            case 2:
                itemObj = coffeeMedium;
                itemString = "CoffeeMedium";
                break;
            case 3:
                itemObj = coffeeSmall;
                itemString ="CoffeeSmall";
                break;
            case 4:
                itemObj = coffeeUOB;
                itemString = "CoffeeUOB";
                break;
            case 5:
                itemObj = crispsBlue;
                itemString = "CrispsBlue";
                break;
            case 6:
                itemObj = crispsGreen;
                itemString = "CrispsGreen";
                break;
            case 7:
                itemObj = juice;
                itemString = "Juice";
                break;
            case 8:
                itemObj = orange;
                itemString = "Orange";
                break;
            default:
                System.out.println("ERROR!!");
                itemObj = apple;
        }

        editor.putString(TEXT, itemString);
        editor.apply();

        float newSpendingValue = totalSpending + (float)itemObj.getPrice();
        editor.putFloat(SPENDINGKEY, newSpendingValue);

        float newSugarValue = totalSugar + itemObj.getSugar();
        editor.putFloat(SUGARKEY, newSugarValue);

        float newCaloriesValue = totalCalories + itemObj.getCalories();
        editor.putFloat(CALORIESKEY, newCaloriesValue);

        editor.apply();

        System.out.println("Saved!!!!!!!!!!!");

    }



}
