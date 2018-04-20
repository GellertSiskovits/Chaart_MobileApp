package com.example.gellert.mobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chart extends AppCompatActivity {

    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();

    JSONArray products = null;

    private static String url_all_products = "http://192.168.1.7/AndroidApp/Data/getAllProducts.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "ExpenseId";
    private static final String TAG_NAME = "expense_date";
    private static final String TAG_SUM = "expense_amount";
    private static final String TAG_NOTE = "expense_note";
    private static final String TAG_CAT = "expense_category";


    BarChart barChart;
    BarDataSet dataSet;
    BarData data;

    ArrayList<BarEntry> entries;
    ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_dispaly);

        barChart = new BarChart(this);
        setContentView(barChart);


        entries = new ArrayList<BarEntry>();
        labels = new ArrayList<String>();
        //load data -> Background Thread

        new LoadAllProducts().execute();

    }




    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(chart.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String category = c.getString(TAG_CAT);
                        String amount = c.getString(TAG_SUM) ;
                        String comment = c.getString(TAG_NOTE);
                        // creating new HashMap
                        ArrayList<BarEntry> entries1 = new ArrayList<>();
                        ArrayList<String> labels1 = new ArrayList<>();
                        // adding each child node to HashMap key => value
//                        map.put(TAG_PID, id);
//                        map.put(TAG_NAME, name);
//                        map.put(TAG_CAT, category);
//                        map.put(TAG_SUM, amount);
//                        map.put(TAG_NOTE, comment);
                        entries1.add(new BarEntry(4f, Integer.parseInt(amount)));
                        labels1.add(name);
                        // adding HashList to ArrayList
                        entries = (entries1);
                        labels = labels1;
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    Intent i = new Intent(getApplicationContext(),
                            data_new.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
//                   /* *//**
//                     * Updating parsed JSON data into ListView
//                     * *//*
                    dataSet = new BarDataSet(entries, "#");
                    data = new BarData(labels, dataSet);
                    // updating listview
                    System.out.print("HEYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYyy");


                }
            });

        }

    }
}