package com.example.homework;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.homework.Fragment.DetailFragment;
import com.example.homework.Fragment.ListFragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity {

    FragmentManager fragmentManager;

    FragmentTransaction fragmentTransactionForList;
    FragmentTransaction fragmentTransactionForDetails;
    FragmentTransaction fragmentTransactionForButton;
    FragmentTransaction fragmentTransactionAfterFirstQuestion;
    FragmentTransaction fragmentTransactionAfterThirdQuestion;
    FragmentTransaction fragmentTransactionAfterFifthQuestion;
    FragmentTransaction fragmentTransactionAfterEighthQuestion;
    FragmentTransaction fragmentTransactionAfterNinthQuestion;


    FrameLayout container;
    ListFragment listFragment;
    DetailFragment detailFragment;
    DetailFragment detailFragmentAfterFirstQuestion, detailFragmentAfterThirdQuestion, detailFragmentAfterChoice,
            detailFragmentAfterFifthQuestion, detailFragmentAfterEighthQuestion, detailFragmentAfterNinthQuestion;

    ArrayList<HashMap<String, String>> scenariosList;
    HashMap<String, String> temp;

    DataBase dbHelper;
    SQLiteDatabase db;
    ContentValues cv;
    Cursor cursor;

    BroadcastReceiver broadcastReceiverForFirstScenario;
    BroadcastReceiver broadcastReceiverForSecondScenario;
    BroadcastReceiver broadcastReceiverForFirstButton;
    BroadcastReceiver broadcastReceiverForSecondButton;

    ScenariosAsyncTask scenariosAsyncTask;

    SharedPreferences rowsID;
    SharedPreferences buttonCaseID;

    int rowsNumber;
    int btnCaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        container = (FrameLayout) findViewById(R.id.container);

        scenariosList = new ArrayList<HashMap<String, String>>();

        fragmentManager = getFragmentManager();

        fragmentTransactionForList = fragmentManager.beginTransaction();
        fragmentTransactionForDetails = fragmentManager.beginTransaction();
        fragmentTransactionForButton = fragmentManager.beginTransaction();
        fragmentTransactionAfterFirstQuestion = fragmentManager.beginTransaction();
        fragmentTransactionAfterThirdQuestion = fragmentManager.beginTransaction();
        fragmentTransactionAfterFifthQuestion = fragmentManager.beginTransaction();
        fragmentTransactionAfterEighthQuestion = fragmentManager.beginTransaction();
        fragmentTransactionAfterNinthQuestion = fragmentManager.beginTransaction();

        listFragment = new ListFragment();
        detailFragment = new DetailFragment();
        detailFragmentAfterChoice = new DetailFragment();
        detailFragmentAfterFirstQuestion = new DetailFragment();
        detailFragmentAfterThirdQuestion = new DetailFragment();
        detailFragmentAfterFifthQuestion = new DetailFragment();
        detailFragmentAfterEighthQuestion = new DetailFragment();
        detailFragmentAfterNinthQuestion = new DetailFragment();

        dbHelper = new DataBase(this);
        db = dbHelper.getWritableDatabase();
        cursor = db.query(Constants.TABLE_SCENARIOS, null, null, null, null, null, null);


            if (isOnline()) {

                if (cursor.moveToFirst()) {
                    cursor.close();

                    fragmentTransactionForList.add(R.id.container, listFragment);
                    fragmentTransactionForList.commit();

                } else {
                    scenariosAsyncTask = new ScenariosAsyncTask();
                    scenariosAsyncTask.execute("http://expert-system.internal.shinyshark.com/scenarios");

                    try {
                        scenariosAsyncTask.get();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    fragmentTransactionForList.add(R.id.container, listFragment);
                    fragmentTransactionForList.commit();

                }
            } else {

                Toast toast = Toast.makeText(MainActivity.this, "Disable Internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiverForFirstScenario = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                fragmentTransactionForDetails.replace(R.id.container, detailFragment);
                fragmentTransactionForDetails.commit();
                rowsNumber = intent.getIntExtra("first", 0);

                rowsID = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = rowsID.edit();
                editor.putInt(Constants.GET_ID, rowsNumber);
                editor.commit();

            }
        };

        broadcastReceiverForSecondScenario = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                fragmentTransactionForDetails.replace(R.id.container, detailFragment);
                fragmentTransactionForDetails.commit();

                rowsNumber = intent.getIntExtra("second", 0);

                rowsID = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = rowsID.edit();
                editor.putInt(Constants.GET_ID, rowsNumber);
                editor.commit();

            }
        };

        broadcastReceiverForFirstButton = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                btnCaseId = intent.getIntExtra("firstBtn", 0);

                switch (btnCaseId) {
                    case 3:
                        fragmentTransactionAfterFirstQuestion.replace(R.id.container, detailFragmentAfterFirstQuestion);
                        fragmentTransactionAfterFirstQuestion.commit();
                        break;
                    case 5:
                        fragmentTransactionAfterThirdQuestion.replace(R.id.container, detailFragmentAfterThirdQuestion);
                        fragmentTransactionAfterThirdQuestion.commit();
                        break;
                    case 7:
                        fragmentTransactionAfterFifthQuestion.replace(R.id.container, detailFragmentAfterFifthQuestion);
                        fragmentTransactionAfterFifthQuestion.commit();
                        break;
                    case 8:
                        fragmentTransactionAfterEighthQuestion.replace(R.id.container, detailFragmentAfterEighthQuestion);
                        fragmentTransactionAfterEighthQuestion.commit();
                        break;
                    case 9:
                        fragmentTransactionAfterNinthQuestion.replace(R.id.container, detailFragmentAfterNinthQuestion);
                        fragmentTransactionAfterNinthQuestion.commit();
                }

                buttonCaseID = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = buttonCaseID.edit();
                editor.putInt(Constants.GET_BUTTON_ID, btnCaseId);
                editor.commit();

            }
        };

        broadcastReceiverForSecondButton = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fragmentTransactionForButton.replace(R.id.container, detailFragmentAfterChoice);
                btnCaseId = intent.getIntExtra("secondBtn", 0);

                buttonCaseID = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = buttonCaseID.edit();
                editor.putInt(Constants.GET_BUTTON_ID, btnCaseId);
                editor.commit();

            }
        };

        registerReceiver(broadcastReceiverForFirstScenario, new IntentFilter(Constants.START_FIRST_SCENARIO));
        registerReceiver(broadcastReceiverForSecondScenario, new IntentFilter(Constants.START_SECOND_SCENARIO));
        registerReceiver(broadcastReceiverForFirstButton, new IntentFilter(Constants.START_AFTER_CLICK_FIRST_BTN));
        registerReceiver(broadcastReceiverForSecondButton, new IntentFilter(Constants.START_AFTER_CLICK_SECOND_BTN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverForFirstScenario);
        unregisterReceiver(broadcastReceiverForSecondScenario);
        unregisterReceiver(broadcastReceiverForFirstButton);
        unregisterReceiver(broadcastReceiverForSecondButton);
    }

    public boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        if (netInfo == null) {
            return false;
        }

        for (NetworkInfo ni : netInfo) {

            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {

                    return true;
                }

            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {

                    return true;
                }
        }
        return false;
    }


    public class ScenariosAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                HttpResponse response = client.execute(post);

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject mainObject = new JSONObject(data);
                    JSONArray scenarios = mainObject.getJSONArray("scenarios");

                    for (int i = 0; i < scenarios.length(); i++) {
                        JSONObject scenarioInfo = scenarios.getJSONObject(i);

                        temp = new HashMap<String, String>();

                        temp.put("text", scenarioInfo.getString("text"));
                        temp.put("id", scenarioInfo.getString("id"));
                        temp.put("caseId", scenarioInfo.getString("caseId"));

                        scenariosList.add(temp);

                    }
                    cv = new ContentValues();

                    for (int i = 0; i < scenariosList.size(); i++) {
                        cv.put(Constants.COLUMN_CASE_ID, scenariosList.get(i).get("caseId"));
                        cv.put(Constants.COLUMN_ID, scenariosList.get(i).get("id"));
                        cv.put(Constants.COLUMN_TEXT, scenariosList.get(i).get("text"));

                        db.insert(Constants.TABLE_SCENARIOS, null, cv);
                    }
                    dbHelper.close();
                    db.close();

                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }
}
