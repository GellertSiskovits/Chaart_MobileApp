package com.example.gellert.mobileapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewEntryActivity extends Activity {

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;
    private EditText inputdesc;
    private EditText inputamount;
    private Spinner categories_spinner;
    private DatePicker dp_datepicker;

    private static String url = "http://192.168.1.7/AndroidApp/Data/createdata.php";

    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        spinner = findViewById(R.id.categories_spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.category_names,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        inputdesc=findViewById(R.id.inputdesc);
        inputamount=findViewById(R.id.inputamount);
        categories_spinner=spinner;
        dp_datepicker = findViewById(R.id.dp_datepicker);

        Button additem= findViewById(R.id.addItem);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getBaseContext(),adapterView.getItemIdAtPosition(i) + " Selected",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CreateNewProduct().execute();
            }
        });

    }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewEntryActivity.this);
            pDialog.setMessage("Creating Product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            String description = inputdesc.getText().toString();
            String amount = inputamount.getText().toString();
            String category = categories_spinner.getSelectedItem().toString();
            String date =dp_datepicker.getYear()+"-"+dp_datepicker.getMonth()+"-"+dp_datepicker.getDayOfMonth();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("amount", amount));
            params.add(new BasicNameValuePair("category", category));
            params.add(new BasicNameValuePair("description", description));
            params.add(new BasicNameValuePair("date", date));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), data_dispaly.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
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
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}
