package com.example.homework.Fragment;



import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.homework.Constants;
import com.example.homework.DataBase;
import com.example.homework.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ListFragment extends Fragment {

    ListView scenarios;

    private DataBase dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;
    int text;
    int caseID;

    ArrayList<String> textList;
    ArrayList<Integer> caseIDList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_fragment, container, false);
        scenarios = (ListView) v.findViewById(R.id.scenarios);

        textList = new ArrayList<String>();
        caseIDList = new ArrayList<Integer>();

        dbHelper = new DataBase(getActivity());
        database = dbHelper.getReadableDatabase();
        cursor = database.query(Constants.TABLE_SCENARIOS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            caseID = cursor.getColumnIndex(Constants.COLUMN_CASE_ID);
            text = cursor.getColumnIndex(Constants.COLUMN_TEXT);
            do {
                caseIDList.add(cursor.getInt(caseID));
                textList.add(cursor.getString(text));
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }

        dbHelper.close();
        database.close();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, textList);
        scenarios.setAdapter(arrayAdapter);

        scenarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case (0):
                        intent = new Intent(Constants.START_FIRST_SCENARIO);
                        intent.putExtra("first", caseIDList.get(0));
                        getActivity().sendBroadcast(intent);
                        break;
                    case (1):
                        intent = new Intent(Constants.START_SECOND_SCENARIO);
                        intent.putExtra("second", caseIDList.get(1));
                        getActivity().sendBroadcast(intent);
                        break;
                }
            }
        });

        return v;
    }


}
