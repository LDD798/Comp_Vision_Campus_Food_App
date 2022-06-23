package com.campusfoodclassifier.tflite;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class Nutrition {

    private float cal = -1;
    private float sugar = -1;
    private float price = -1;
    private String name = "";


    public static final String SHARED_PREFS = "SharedPrefs";
    public static final Set<String> HISTORY = new HashSet<>();
    public static final String TEXT = "text";
    public static final String SUGARTOTAL = "sugarTotal";
    public static final String CALORIESTOTAL = "caloriesTotal";
    public static final String PRICETOTAL = "priceTotal";



    public Nutrition(String n, float c, float p, float s) {

        name = n;
        cal = c;
        price = p;
        sugar = s;

    }

    public Nutrition(String n, float c) {

        name = n;
        cal = c;
        sugar = 0;
        price = 0;

    }
    public Nutrition(String n, float c, float p) {

        name = n;
        cal = c;
        sugar = 0;
        price = p;

    }

    public float getCalories() {
        return cal;
    }
    public float getSugar() {
        return sugar;
    }
    public float getPrice() {
        return price;
    }

//    public void saveItem() {
//
//        //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        //add item name to history set of items
//        //editor.putStringSet();
//
//        //add getSugar to the sugarTotal Varaible
//        //add getCalories to the sugarTotal Varaible
//        //add getPrice to the sugarTotal Varaible
//
//        editor.putString(TEXT, name);
//        editor.apply();
//
//    }
}
