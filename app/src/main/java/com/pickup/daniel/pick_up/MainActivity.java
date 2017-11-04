package com.pickup.daniel.pick_up;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_GAME = "com.example.daniel.GAME";

    ArrayAdapter<String> arrayAdapter;
    List<Game> games = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This makes it so the keyboard doesn't push up the buttons
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Game selector spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.games_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Adapter for the list view
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item);
        final ListView listView = (ListView) findViewById(R.id.gamesListView);
        // Connect the list to the ArrayAdapter
        listView.setAdapter(arrayAdapter);

        // Initially populate the list with random games
        if (games.size() == 0) {
            // Instantiate our AsyncTask class for generating a list of games and populating the  list
            PopulateListTask populateListTask = new PopulateListTask(arrayAdapter, MainActivity.this, games);
            populateListTask.execute();
        }
        // Maintain the intially created games when switching between activities
        else
        {
            // OrganizeListTask?
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                String selectedFromList =(String) (listView.getItemAtPosition(myItemInt));
                Log.d("MainActivity", selectedFromList);


                // Convert the game object to JSON so I can pass it to the details activity
                Gson gson = new Gson();
                String jsonGame = gson.toJson(games.get(myItemInt));

                // Start the details activity
                Intent intent = new Intent(MainActivity.this, DisplayDetailsActivity.class);
                intent.putExtra(EXTRA_GAME, jsonGame);
                startActivity(intent);
            }
        });


    }

    public void onCreateGame(View view) {
        // Start the details activity
        Intent intent = new Intent(MainActivity.this, CreateGame.class);
        startActivity(intent);
    }

//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }
}
