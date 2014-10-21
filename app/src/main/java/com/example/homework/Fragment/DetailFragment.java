package com.example.homework.Fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homework.Constants;
import com.example.homework.DataBase;
import com.example.homework.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DetailFragment extends Fragment {

    ImageView image;

    TextView question;

    Button btnYes;
    Button btnNo;

    SharedPreferences getID;
    SharedPreferences getBtnCaseId;

    DataBase dbHelper, dataBaseHelper;
    SQLiteDatabase db, database;
    ContentValues cv;
    Cursor cursor1, cursor2;

    ArrayList<HashMap<String, String>> caseList;
    ArrayList<HashMap<String, String>> answerYesList;
    ArrayList<HashMap<String, String>> answerNoList;
    HashMap<String, String> caseMap;
    HashMap<String, String> answerMap;

    TestAsyncTask testAsyncTask;

    Intent intent;

    int temp;
    int btnCaseId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        image = (ImageView) view.findViewById(R.id.image);

        question = (TextView) view.findViewById(R.id.tv_question);

        btnYes = (Button) view.findViewById(R.id.btn_yes);
        btnNo = (Button) view.findViewById(R.id.btn_no);

        getID = getActivity().getPreferences(Context.MODE_PRIVATE);
        temp = getID.getInt(Constants.GET_ID, 0);

        getBtnCaseId = getActivity().getPreferences(Context.MODE_PRIVATE);
        btnCaseId = getBtnCaseId.getInt(Constants.GET_BUTTON_ID, 0);

        Log.d("caseId", btnCaseId + "");

        dbHelper = new DataBase(getActivity());
        db = dbHelper.getWritableDatabase();
        cursor1 = db.query(Constants.TABLE_CASE, null, null, null, null, null, null);

        if (cursor1.moveToFirst()) {

        } else {
            for (int i = 1; i < 10; i++) {

                testAsyncTask = new TestAsyncTask();
                testAsyncTask.execute("http://expert-system.internal.shinyshark.com/cases/" + i);
                try {
                    Boolean b = testAsyncTask.get();
                    Log.d("case", "case");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        }

        cursor1.close();

        Log.d("yes", temp + "");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (btnCaseId == 0) {

            db = dbHelper.getWritableDatabase();
            cursor2 = db.query(Constants.TABLE_CASE, null, null, null, null, null, null);

            cursor2.moveToPosition(temp - 1);
            int textIndex = cursor2.getColumnIndex(Constants.COLUMN_CASE_TEXT);
            int imageIndex = cursor2.getColumnIndex(Constants.COLUMN_CASE_IMAGE);
            int firstBtnIndex = cursor2.getColumnIndex(Constants.COLUMN_FIRST_ANSWER_TEXT);
            int secondBtnIndex = cursor2.getColumnIndex(Constants.COLUMN_SECOND_ANSWER_TEXT);
            int fistBtnCaseIdIndex = cursor2.getColumnIndex(Constants.COLUMN_FIRST_ANSWER_CASE_ID);
            int secondBtnCaseIdIndex = cursor2.getColumnIndex(Constants.COLUMN_SECOND_ANSWER_CASE_ID);

            String text = cursor2.getString(textIndex);
            String imageURL = cursor2.getString(imageIndex);
            String firstBtn = cursor2.getString(firstBtnIndex);
            String secondBtn = cursor2.getString(secondBtnIndex);
            final int firstBtnCaseId = cursor2.getInt(fistBtnCaseIdIndex);
            final int secondBtnCaseId = cursor2.getInt(secondBtnCaseIdIndex);

            cursor2.close();
            dbHelper.close();

            new DownloadImageTask(image).execute(imageURL);
            btnYes.setText(firstBtn);
            btnNo.setText(secondBtn);
            question.setText(text);


            View.OnClickListener onClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.btn_yes:
                            intent = new Intent(Constants.START_AFTER_CLICK_FIRST_BTN);
                            intent.putExtra("firstBtn", firstBtnCaseId);
                            getActivity().sendBroadcast(intent);
                            break;
                        case R.id.btn_no:
                            intent = new Intent(Constants.START_AFTER_CLICK_SECOND_BTN);
                            intent.putExtra("secondBtn", secondBtnCaseId);
                            getActivity().sendBroadcast(intent);
                            break;
                    }
                }
            };

            btnYes.setOnClickListener(onClickListener);
            btnNo.setOnClickListener(onClickListener);

        } else {

            db = dbHelper.getWritableDatabase();
            cursor2 = db.query(Constants.TABLE_CASE, null, null, null, null, null, null);

            cursor2.moveToPosition(btnCaseId - 1);
            int textIndex = cursor2.getColumnIndex(Constants.COLUMN_CASE_TEXT);
            int imageIndex = cursor2.getColumnIndex(Constants.COLUMN_CASE_IMAGE);
            int firstBtnIndex = cursor2.getColumnIndex(Constants.COLUMN_FIRST_ANSWER_TEXT);
            int secondBtnIndex = cursor2.getColumnIndex(Constants.COLUMN_SECOND_ANSWER_TEXT);
            int fistBtnCaseIdIndex = cursor2.getColumnIndex(Constants.COLUMN_FIRST_ANSWER_CASE_ID);
            int secondBtnCaseIdIndex = cursor2.getColumnIndex(Constants.COLUMN_SECOND_ANSWER_CASE_ID);

            String text = cursor2.getString(textIndex);
            String imageURL = cursor2.getString(imageIndex);
            String firstBtn = cursor2.getString(firstBtnIndex);
            String secondBtn = cursor2.getString(secondBtnIndex);
            final int firstBtnCaseId = cursor2.getInt(fistBtnCaseIdIndex);
            final int secondBtnCaseId = cursor2.getInt(secondBtnCaseIdIndex);

            cursor2.close();
            dbHelper.close();

            new DownloadImageTask(image).execute(imageURL);
            btnYes.setText(firstBtn);
            btnNo.setText(secondBtn);
            question.setText(text);


            View.OnClickListener onClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.btn_yes:
                            intent = new Intent(Constants.START_AFTER_CLICK_FIRST_BTN);
                            intent.putExtra("firstBtn", firstBtnCaseId);
                            getActivity().sendBroadcast(intent);
                            break;
                        case R.id.btn_no:
                            intent = new Intent(Constants.START_AFTER_CLICK_SECOND_BTN);
                            intent.putExtra("secondBtn", secondBtnCaseId);
                            getActivity().sendBroadcast(intent);
                            break;
                    }
                }
            };

            btnYes.setOnClickListener(onClickListener);
            btnNo.setOnClickListener(onClickListener);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public class TestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpGet httppost = new HttpGet(params[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject tempObject = new JSONObject(data);
                    JSONObject caseObject = tempObject.getJSONObject("case");
                    JSONArray answers = new JSONArray();

                    if (caseObject.has("answers")) {

                        answers = caseObject.getJSONArray("answers");
                        answerYesList = new ArrayList<HashMap<String, String>>();
                        answerNoList = new ArrayList<HashMap<String, String>>();

                    }
                    String text = caseObject.getString("text");
                    String imageURL = caseObject.getString("image");
                    String id = caseObject.getString("id");

                    caseList = new ArrayList<HashMap<String, String>>();
                    caseMap = new HashMap<String, String>();


                    caseMap.put("text", text);
                    caseMap.put("image", imageURL);
                    caseMap.put("id", id);

                    caseList.add(caseMap);

                    if (answers.length() == 0) {

                        cv = new ContentValues();

                        cv.put(Constants.COLUMN_CASE_TEXT, caseList.get(0).get("text"));
                        cv.put(Constants.COLUMN_CASE_IMAGE, caseList.get(0).get("image"));
                        cv.put(Constants.TABLE_CASE_COLUMN_ID, caseList.get(0).get("id"));

                        cv.put(Constants.COLUMN_FIRST_ANSWER_TEXT, 0);
                        cv.put(Constants.COLUMN_FIRST_ANSWER_ID, 0);
                        cv.put(Constants.COLUMN_FIRST_ANSWER_CASE_ID, 0);

                        cv.put(Constants.COLUMN_SECOND_ANSWER_TEXT, 0);
                        cv.put(Constants.COLUMN_SECOND_ANSWER_ID, 0);
                        cv.put(Constants.COLUMN_SECOND_ANSWER_CASE_ID, 0);

                        db.insert(Constants.TABLE_CASE, null, cv);
                        return true;

                    } else {

                        JSONObject answerYesObj = answers.getJSONObject(0);

                        answerMap = new HashMap<String, String>();

                        answerMap.put("text", answerYesObj.getString("text"));
                        answerMap.put("id", answerYesObj.getString("id"));
                        answerMap.put("caseId", answerYesObj.getString("caseId"));
                        answerYesList.add(answerMap);

                        JSONObject answerNoObj = answers.getJSONObject(1);

                        answerMap = new HashMap<String, String>();

                        answerMap.put("text", answerNoObj.getString("text"));
                        answerMap.put("id", answerNoObj.getString("id"));
                        answerMap.put("caseId", answerNoObj.getString("caseId"));
                        answerNoList.add(answerMap);

                        cv = new ContentValues();

                        cv.put(Constants.COLUMN_CASE_TEXT, caseList.get(0).get("text"));
                        cv.put(Constants.COLUMN_CASE_IMAGE, caseList.get(0).get("image"));
                        cv.put(Constants.TABLE_CASE_COLUMN_ID, caseList.get(0).get("id"));

                        cv.put(Constants.COLUMN_FIRST_ANSWER_TEXT, answerYesList.get(0).get("text"));
                        cv.put(Constants.COLUMN_FIRST_ANSWER_ID, answerYesList.get(0).get("id"));
                        cv.put(Constants.COLUMN_FIRST_ANSWER_CASE_ID, answerYesList.get(0).get("caseId"));

                        cv.put(Constants.COLUMN_SECOND_ANSWER_TEXT, answerNoList.get(0).get("text"));
                        cv.put(Constants.COLUMN_SECOND_ANSWER_ID, answerNoList.get(0).get("id"));
                        cv.put(Constants.COLUMN_SECOND_ANSWER_CASE_ID, answerNoList.get(0).get("caseId"));

                        db.insert(Constants.TABLE_CASE, null, cv);

                        return true;
                    }
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


    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urlDisplay = params[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return icon;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }
}
