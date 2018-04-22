package com.example.gellert.mobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


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

    HashMap<Integer,String> labelMap;

    LineChart barChart ;
    LineDataSet dataSet;
    LineData data;

    List<Entry> entries;
    ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        barChart = (LineChart) findViewById(R.id.linechart);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);


        entries = new ArrayList<Entry>();
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
                    labelMap = new HashMap<>();
                    // looping through All Products
                    int index = 0;
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
                        String name = c.getString(TAG_NAME);
                        String amount = c.getString(TAG_SUM) ;
                        // creating new HashMap
                        ArrayList<BarEntry> entries1 = new ArrayList<>();
                        ArrayList<String> labels1 = new ArrayList<>();
                        // adding each child node to HashMap key => value
//                        map.put(TAG_PID, id);
//                        map.put(TAG_NAME, name);
//                        map.put(TAG_CAT, category);
//                        map.put(TAG_SUM, amount);
//                        map.put(TAG_NOTE, comment);
                        entries.add(new Entry(Integer.parseInt(amount),index));
                        labelMap.put(index,name);
                        index++;
                        Log.d("amount: ",amount);
                        Log.d("date: ", name);
                        labels.add(name);
                        // adding HashList to ArrayList



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
            Log.d("FINAL ENTRIES:", entries.toString());
            Log.d("FINAL LABELS:", labels.toString());

            dataSet = new LineDataSet(entries, "Expenses");
            dataSet.setDrawCubic(true);
            dataSet.setDrawFilled(true);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            data = new LineData(labels,dataSet);




            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
//

                    Log.d("FINAL ENTRIES:", entries.toString());
                    Log.d("FINAL LABELS:", labels.toString());

                    barChart.setData(data);
                   // setContentView(barChart);
                    barChart.invalidate();
//                    final String[] strings = labels.toArray(new String[]);
//                    IAxisValueFormatter formatter = new IAxisValueFormatter() {
//                        @Override
//                        public String getFormattedValue(float value, AxisBase axis) {
//                            return strings[(int)value];
//                        }
//
//
//                    };
//                    XAxis xAxis = barChart.getXAxis();
//                    xAxis.setGranularity(1f);
//                    xAxis.setValueFormatter(formatter);
                    pDialog.dismiss();

                }
            });

        }

    }
}