package com.example.gellert.mobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends Activity {

    EditText amount;
    EditText desciprion;
    Spinner category;
    DatePicker date;

    Button update;
    Button delete;

    String pid;

    // single product url
    private static final String url_product_detials = "http://192.168.1.7/AndroidApp/Data/onerecord.php";

    // url to update product
    private static final String url_update_product = "http://192.168.1.7/AndroidApp/Data/editdata.php";

    // url to delete product
    private static final String url_delete_product = "http://192.168.1.7/AndroidApp/Data/deleterecord.php";


    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_AMOUNT = "amount";
    private static final String TAG_DATE = "date";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CATEGORY = "category";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        ArrayAdapter<CharSequence> adapter;
        category = findViewById(R.id.edit_categories_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.category_names,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);




        update = findViewById(R.id.editbtn);
        delete = findViewById(R.id.deletebtn);

        Intent i = getIntent();

        pid = i.getStringExtra("pid");
        Log.d("PID:", pid);
        new GetProductDetails().execute();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateProduct().execute();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 new DeleteProduct().execute();
            }
        });
    }


    //-----------------------

    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         */

        JSONObject product;

        protected String doInBackground(String... params1) {

            // updating UI from Background Thread

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                // Note that product details url will use GET request
                Log.d("PRODUCT ID", pid.toString());
                JSONObject json = jsonParser.makeHttpRequest(
                        url_product_detials, "GET", params);

                // check your log for json response
                Log.d("Single Product Details", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully received product details
                    JSONArray productObj = json
                            .getJSONArray(TAG_PRODUCT); // JSON Array

                    // get first product object from JSON Array
                    product = productObj.getJSONObject(0);

                    // product with this pid found
                    // Edit Text


                } else {
                    // product with pid not found
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
            // dismiss the dialog once got all details
            runOnUiThread(new Runnable() {
                public void run() {
                    amount = (EditText) findViewById(R.id.edit_inputamount);
                    desciprion = (EditText) findViewById(R.id.edit_inputdesc);
                    category = (Spinner) findViewById(R.id.edit_categories_spinner);
                    date = (DatePicker) findViewById(R.id.edit_dp_datepicker);
                    // display product data in EditText
                    try {
                        amount.setText(product.getString(TAG_AMOUNT));


                        int year = Integer.parseInt(
                                product.getString(TAG_DATE).substring(0, 4)
                        );

                        int month = Integer.parseInt(
                                product.getString(TAG_DATE).substring(5, 7)
                        );

                        int day = Integer.parseInt(
                                product.getString(TAG_DATE).substring(8, 10)
                        );
                        Log.d("DATE:", year + "-" + month + "-" + day);
                        date.init(year, month, day, null);

                        desciprion.setText(product.getString(TAG_DESCRIPTION));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pDialog.dismiss();
                }

            });
        }
    }

        //-------------------------

        class UpdateProduct extends AsyncTask<String, String, String> {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(EditProductActivity.this);
                pDialog.setMessage("Saving product ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            /**
             * Saving product
             */
            protected String doInBackground(String... args) {

                // getting updated data from EditTexts
                String amount1 = amount.getText().toString();
                String category1 = category.getSelectedItem().toString();
                String description1 = desciprion.getText().toString();
                String date1 = date.getYear() + "-" + date.getMonth() + "-" + date.getDayOfMonth();

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_PID, pid));
                params.add(new BasicNameValuePair(TAG_AMOUNT, amount1));
                params.add(new BasicNameValuePair(TAG_CATEGORY, category1));
                params.add(new BasicNameValuePair(TAG_DESCRIPTION, description1));
                params.add(new BasicNameValuePair(TAG_DATE, date1));

                // sending modified data through http request
                // Notice that update product url accepts POST method
                JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                        "POST", params);

                // check json success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully updated
                        Intent i = getIntent();
                        // send result code 100 to notify about product update
                        setResult(100, i);
                        finish();
                    } else {
                        // failed to update product
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            //----------------------------
        }

    class DeleteProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Deleting Product...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         * */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_product, "POST", params);

                // check your log for json response
                Log.d("Delete Product", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // product successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about product deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
    }
