package com.campusfoodclassifier.tflite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;

import java.util.HashSet;
import java.util.Set;

public class BayesTable {



    public static final String SHARED_PREFS = "SharedPrefs";
    public static final Set<String> HISTORY = new HashSet<>();
    public static final String TEXT = "text";


    static int[][] frequencyTable = new int[10][4];
    static float[][] posteriorTable= new float[9][3];

    BayesTable() {

        //initialise the table
        for (int i = 0; i<9; i++) {
            for (int j = 0; j<3; j++) {
                frequencyTable[i][j] = 1;
            }
        }

        //column of totals
        for (int i = 0; i < 9; i++) {
            frequencyTable[i][3] = 3;
        }

        //row of totals
        for (int i = 0; i < 3; i++) {
            frequencyTable[9][i] = 9;
        }


        frequencyTable[9][3] = 18;

    }




    public void updateTable(int food, int location) {

        //first increment cell
        frequencyTable[food][location]++;

        //recalculate totals
        frequencyTable[9][location]++;
        frequencyTable[food][3]++;
        frequencyTable[9][3]++;



        updatePosteriorTable(location);


    }

    public String getPrediction(int location) {
        float max = posteriorTable[0][location];
        System.out.println("0----"+posteriorTable[0][location]);
        int maxIndex = 0;
        for (int i=1; i < 9; i++) {
            if (posteriorTable[i][location] > max){
                maxIndex = i;
                max = posteriorTable[i][location];
                System.out.println(i+"----"+posteriorTable[i][location]);
            }
        }

        LocationActivity.predictionIndex = maxIndex;

        String predictionString = "";
        switch (maxIndex) {
            case 0:
                predictionString = "Apple";
                break;
            case 1:
                predictionString = "Banana";
                break;
            case 2:
                predictionString = "coffeeMedium";
                break;
            case 3:
                predictionString = "coffeeSmall";
                break;
            case 4:
                predictionString = "coffeeUOB";
                break;
            case 5:
                predictionString = "Crisps Blue";
                break;
            case 6:
                predictionString = "Crisps Green";
                break;
            case 7:
                predictionString = "Orange";
                break;
            case 8:
                predictionString = "Juice";
                break;
            default:
                System.out.println("ERROR!!");
                predictionString = "Unsure";

        }

        max = (float )(double)Math.round(max *100);

        predictionString = predictionString+" "+max+"%";

        return predictionString;




    }


    protected void updatePosteriorTable(int location) {

        //for each item of food (or each row)
        for (int i = 0; i < 9; i++) {

            //1
            float a = frequencyTable[i][location];
            float b = frequencyTable[i][3];
            float PofLocationGivenFood = a / b;

            //2
            float c =frequencyTable[i][3];
            float d = frequencyTable[9][3];
            float PofFood = c/d;

            //3
            float e = frequencyTable[9][location];
            float f = frequencyTable[9][3];
            float PofLocation = e/f;

            //4
            float PofFoodGivenLocation = (PofLocationGivenFood*PofFood)/PofLocation;
            posteriorTable[i][location] = PofFoodGivenLocation;
        }



    }




}
