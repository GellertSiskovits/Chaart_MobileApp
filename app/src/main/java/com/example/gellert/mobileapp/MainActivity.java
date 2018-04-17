package com.example.gellert.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener {

    EditText eUser,ePass;
    Button eButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eUser = (EditText) findViewById(R.id.eUser);
        ePass = (EditText) findViewById(R.id.ePass);
        eButton = (Button) findViewById(R.id.eButton);

        eButton.setOnClickListener(this);

//        PostResponseAsyncTask task = new PostResponseAsyncTask(this);
//        task.execute("");
    }



    @Override
    public void processFinish(String result) {
        if(result.equals("SUCCESS")) {
            // Toast.makeText(this,result,Toast.LENGTH_LONG).show();
            Toast.makeText(this, "LOGIN SUCCESSFULL", Toast.LENGTH_LONG).show();
            Intent in = new Intent(this,SecondActivity.class);
            startActivity(in);
        }else{

            Toast.makeText(this, "LOGIN FAILd", Toast.LENGTH_LONG).show();
        }
       //  Toast.makeText(this,result,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        HashMap postData = new HashMap();
        postData.put("btnSubmit","Login");
        postData.put("mobile","android");
        postData.put("txtUsername",eUser.getText().toString());
        postData.put("txtPassoword",ePass.getText().toString());

        PostResponseAsyncTask task = new PostResponseAsyncTask(this,postData);
        task.execute("http://192.168.1.8/AndroidApp/Client/login.php");
    }
}
