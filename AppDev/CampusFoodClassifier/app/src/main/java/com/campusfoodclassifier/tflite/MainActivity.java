package com.campusfoodclassifier.tflite;

import android.Manifest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.libraries.places.api.Places;
//
//import com.google.android.gms.tasks.Task;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.Place.Field;
//import com.google.android.libraries.places.api.model.PlaceLikelihood;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
//import com.google.android.libraries.places.api.net.PlacesClient;
//
//import android.Manifest.permission;
//
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    //change the following parameters
    private static final String MODEL_PATH = "transfer_learning_vgg16.tflite";//changed
    //private static final String MODEL_PATH = "FinalFullyTrainedWithAugStep10RmsProp.tflite";//changed
    //private static final String MODEL_PATH ="SeparateValidationSetNoAugSGDStep10epoch25.tflite";
    //private static final String MODEL_PATH ="FinalFullyTrainedNoAugStep10RmsProp.tflite";
    private static final boolean QUANT = false;//changed
    //private static final boolean QUANT = true;//changed

    private static final String LABEL_PATH = "Mylabels.txt";//changed
    private static final int INPUT_SIZE = 150;//changed

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera, btnHistory;
    private ImageView imageViewResult;
    private CameraView cameraView;

    private TextView textViewNutrition;
    private TextView textBigResult;
    public Button btnSave;
    private Button btnLocation;

    private float totalSpending;
    private float totalSugar;
    private float totalCalories;



    public boolean flip = true;

    Nutrition apple =  new Nutrition("Apple", 106.8f, 0.27f, 10);
    Nutrition banana = new Nutrition("Banana", 61.57f, 0.2f, 12);
    Nutrition coffeeMedium = new Nutrition("Coffee Medium", 61.57f, 14.7f);
    Nutrition coffeeSmall = new Nutrition("Coffee Small", 61.57f, 2.50f, 10.6f);
    Nutrition coffeeUOB = new Nutrition("Coffee UOB", 61.57f, 2, 10.6f);
    Nutrition crispsBlue = new Nutrition("Crisps Sea Salt", 61.57f, 1, 0.2f);
    Nutrition crispsGreen = new Nutrition("Crisps Salt&Vinegar", 61.57f, 1, 0.4f);
    Nutrition orange = new Nutrition("Orange", 61.57f, 0.3f, 9);
    Nutrition juice = new Nutrition("Juice", 61.57f, 0.30f, 53);


    String predictionString = "";


    public BayesTable bayesTable;

    public static final String SHARED_PREFS = "SharedPrefs";
    public static final Set<String> HISTORY = new HashSet<>();;
    public static final String TEXT = "text";
    public static final String SPENDINGKEY = "SpendingKey";
    public static final String SUGARKEY = "SugarKey";
    public static final String CALORIESKEY = "CaloriesKey";

    public static int locationIndex = -1;
    public static boolean tableUpdated = false;






    private List<Nutrition> items = new ArrayList();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        System.out.println("Created!!!!!!!!!!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bayesTable = new BayesTable();


        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        textViewNutrition = findViewById(R.id.textViewNutrition);
        textBigResult = findViewById(R.id.textBigResult);
        textBigResult.setTextColor(Color.parseColor("#FF5722"));
        btnHistory = findViewById(R.id.btnHistory);
        btnSave = findViewById(R.id.btnSave);



        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnDetectObject = findViewById(R.id.btnDetectObject);
        btnLocation = findViewById(R.id.btnLocation);

        items.add(apple);
        items.add(banana);
        items.add(coffeeMedium);
        items.add(coffeeSmall);
        items.add(coffeeUOB);
        items.add(crispsBlue);
        items.add(crispsGreen);
        items.add(orange);
        items.add(juice);





        cameraView.addCameraKitListener(new CameraKitEventListener() {

            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                System.out.println("Width: "+w+" Height: "+h);

                //bitmap =Bitmap.createBitmap(bitmap, 0, 210 ,1080, 1080);
                bitmap =Bitmap.createBitmap(bitmap, 0, 583 ,1080, 1080);

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                //textViewResult.setText(results.toString());
                predictionString = results.get(0).getTitle();
                textBigResult.setText(predictionString);
                textBigResult.setTextColor(Color.parseColor("#FF5722"));

                ArrayList<String> stringResults = new ArrayList();
                for (int i =0; i < results.size(); i++) {
                    float confPer = results.get(i).getConfidence();
                    String conf = String.format(" %.1f%%", confPer * 100.0f);
                    String name = results.get(i).getTitle();
                    String result = name+" "+conf;
                    stringResults.add(result+"\n");
                }


                textViewResult.setText(stringResults.toString().replace(", ", "")  //remove the commas
                        .replace("[", "")  //remove the right bracket
                        .replace("]", "")  //remove the left bracket
                        .trim());
                int id = Integer.parseInt(results.get(0).getID());
                Nutrition item = items.get(id);
                textViewNutrition.setText("Price = £"+item.getPrice()+"\n" +
                        "Sugar = "+item.getSugar()+"\nCalories = "+item.getCalories());

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {


            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, History.class));
                overridePendingTransition(R.anim.slide_in_right, 0);



            }
        });


        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
                overridePendingTransition(R.anim.slide_in_right, 0);

            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!predictionString.equals("")) {
                    saveNutrition(predictionString);
                    toastMessage("Saved "+predictionString);
                    textBigResult.setText(predictionString+" ✓");
                    textBigResult.setTextColor(Color.parseColor("#BDC6BC"));//#3FC82C


                    //convert string to index

                    int index;
                    switch (predictionString) {
                        case "Apple":
                            index = 0;
                            break;
                        case "Banana":
                            index = 1;
                            break;
                        case "CoffeeMedium":
                            index = 2;
                            break;
                        case "CoffeeSmall":
                            index = 3;
                            break;
                        case "CoffeeUOB":
                            index = 4;
                            break;
                        case "CrispsBlue":
                            index = 5;
                            break;
                        case "CrispsGreen":
                            index = 6;
                            break;
                        case "Juice":
                            index = 7;
                            break;
                        case "Orange":
                            index = 8;
                            break;
                        default:
                            System.out.println("ERROR!!");
                            index = -1;
                    }

                    if (locationIndex > -1) {
                        bayesTable.updateTable(index, locationIndex);
                        tableUpdated = true;
                    }


                    //bayesTable.updateTable(predictionIndex, location);

                    System.out.println("Saved!!!!!!!!!!!");


                }
            }
        });


        initTensorFlowAndLoadModel();
    }

    @Override
    protected void onResume() {
        System.out.println("Resume!!!!!!!!!!!");
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        System.out.println("Pause!!!!!!!!!!!");
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroyed!!!!!!!!!!!");
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });

    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }

    public void saveNutrition(String item) {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, item);
        editor.apply();

        totalSpending = sharedPreferences.getFloat(SPENDINGKEY, 0);
        totalSugar = sharedPreferences.getFloat(SUGARKEY, 0);
        totalCalories = sharedPreferences.getFloat(CALORIESKEY, 0);

        Nutrition itemObj;
        switch (item) {
            case "Apple":
                itemObj = apple;
                break;
            case "Banana":
                itemObj = banana;
                break;
            case "CoffeeMedium":
                itemObj = coffeeMedium;
                break;
            case "CoffeeSmall":
                itemObj = coffeeSmall;
                break;
            case "CoffeeUOB":
                itemObj = coffeeUOB;
                break;
            case "CrispsBlue":
                itemObj = crispsBlue;
                break;
            case "CrispsGreen":
                itemObj = crispsGreen;
                break;
            case "Juice":
                itemObj = juice;
                break;
            case "Orange":
                itemObj = orange;
                break;
            default:
                System.out.println("ERROR!!");
                itemObj = apple;
        }

        float newSpendingValue = totalSpending + (float)itemObj.getPrice();
        editor.putFloat(SPENDINGKEY, newSpendingValue);

        float newSugarValue = totalSugar + itemObj.getSugar();
        editor.putFloat(SUGARKEY, newSugarValue);

        float newCaloriesValue = totalCalories + itemObj.getCalories();
        editor.putFloat(CALORIESKEY, newCaloriesValue);

        editor.apply();

        System.out.println("Saved!!!!!!!!!!!");

    }

    private void toastMessage(String m) {
        Toast toast = Toast.makeText(this, m, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 350);
        toast.show();
    }


//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void getPlace() {
//
//
//        String apiKey = "AIzaSyDaNxB-sCZFVR2711hNQZRl9vSFVwtdLHs";
//
//        // Initialize the SDK
//        Places.initialize(getApplicationContext(), apiKey);
//
//// Create a new Places client instance
//        PlacesClient placesClient = Places.createClient(this);
//
//
//
//
//        // Use fields to define the data types to return.
//        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);
//
//// Use the builder to create a FindCurrentPlaceRequest.
//        FindCurrentPlaceRequest request =
//                FindCurrentPlaceRequest.newInstance(placeFields);
//
//// Call findCurrentPlace and handle the response (first check that the user has granted permission).
//        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
//                    if (task.isSuccessful()) {
//                        FindCurrentPlaceResponse response = task.getResult();
//                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                            Log.i(TAG, String.format("Place '%s' has likelihood: %f",
//                                    placeLikelihood.getPlace().getName(),
//                                    placeLikelihood.getLikelihood()));
//                        }
//                    } else {
//                        Exception exception = task.getException();
//                        if (exception instanceof ApiException) {
//                            ApiException apiException = (ApiException) exception;
//                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                        }
//                    }
//                }
//            });
//        } else {
//            // A local method to request required permissions;
//            // See https://developer.android.com/training/permissions/requesting
//            //getLocationPermission();
//
////            ActivityCompat.requestPermissions(this,
////                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                    99);
//
//
//            requestPermissions(new String[] {ACCESS_FINE_LOCATION}, 1);
//
//        }
//
//
//
//        System.out.println(placeFields.get(0));
//
//    }


}
