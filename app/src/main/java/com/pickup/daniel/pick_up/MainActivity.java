package com.pickup.daniel.pick_up;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener {
    public static final String EXTRA_GAME = "com.example.daniel.GAME";

    GameAdapter gameAdapter;
    ArrayList<Game> games = new ArrayList<>();
    Date dateFromDatePicker = new Date();
    Date dateSelected = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This will be used to restore the activity when going from one to another
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This makes it so the keyboard doesn't push up the buttons
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Game selector spinner
        Spinner gameSpinner = (Spinner) findViewById(R.id.gamesSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> gameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.games_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        gameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        gameSpinner.setAdapter(gameSpinnerAdapter);

        // Number of players selector spinner
        Spinner numPlayersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> numPlayersSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.num_players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        numPlayersSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        numPlayersSpinner.setAdapter(numPlayersSpinnerAdapter);

        // Adapter for the list view
        gameAdapter = new GameAdapter(this, android.R.layout.simple_selectable_list_item, games);
        final ListView listView = (ListView) findViewById(R.id.gamesListView);
        // Connect the list to the ArrayAdapter
        listView.setAdapter(gameAdapter);

        // Initially populate the list with random games
        if (!sharedPref.contains("gamesSize")) {
            Log.d("MainActivity", "The games list was empty. Generating new games...");
            // Instantiate our AsyncTask class for generating a list of games and populating the  list
            PopulateListTask populateListTask = new PopulateListTask(gameAdapter, MainActivity.this, games);
            populateListTask.execute();
        }
        // Maintain the intially created games when switching between activities
        else
        {
            Log.d("MainActivity", "This is restoring the games");

            int numGames = sharedPref.getInt("gamesSize", 0);

            Gson gson = new Gson();

            // Obtain all of the saved json games to restore from the shared preferences
            for(int i = 0; i < numGames; i++) {
                String game = sharedPref.getString("Game" + Integer.toString(i), "");
                Game restoredGame = gson.fromJson(game, Game.class);
                games.add(restoredGame);
            }

            OrganizeListTask organizeListTask = new OrganizeListTask(gameAdapter, MainActivity.this, games);
            organizeListTask.execute();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                // Todo: Game type here?
                Game selectedFromList =(Game) (listView.getItemAtPosition(myItemInt));
                Log.d("MainActivity", selectedFromList.getListString());

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

//    protected void onSaveInstanceState(Bundle bundle) {
//        Log.d("MainActivity", "This is in onSaveInstanceState");
//
//        // Get an instance of the Shared Preferences
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        // Create an editor with which we can add to the preferences
//        SharedPreferences.Editor editor = sharedPref.edit();
//
//        // We need to know the size of the list
//        editor.putInt("gamesSize", games.size());
//
//        // Save all the games in the shared preferences
//        Gson gson = new Gson();
//        for(int i = 0; i < games.size(); i++) {
//            String savedGame = gson.toJson(games.get(i));
//            editor.putString("Game" + Integer.toString(i), savedGame);
//        }
//
//        editor.commit();
//    }

    // Doesn't get called
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        Log.d("MainActivity", "This is in onRestoreInstanceState");
//
//        super.onRestoreInstanceState(savedInstanceState);
//        List<String> gamesToRestore;
//        Gson gson = new Gson();
//
//        // Obtain all of the saved json games to restore
//        gamesToRestore = savedInstanceState.getStringArrayList("games");
//        for(String game: gamesToRestore) {
//            Game restoredGame = gson.fromJson(game, Game.class);
//            games.add(restoredGame);
//        }
//
//        OrganizeListTask organizeListTask = new OrganizeListTask(arrayAdapter, MainActivity.this, games);
//        organizeListTask.execute();
//    }



    public void onCreateGame(View view) {
        // Start the details activity
        Intent intent = new Intent(MainActivity.this, CreateGame.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(Date date) {
        // This method will be called with the date from the `DatePicker`.
        dateFromDatePicker = date;
        // Todo: This is getting called on the NEXT time you click ok, not the first time.
    }

    public void onDateButton(View view) {
        DatePickerFragment fragment = DatePickerFragment.newInstance(this);
        fragment.show(getFragmentManager(), "datePicker");
        dateSelected = dateFromDatePicker;
        Log.d("MainActivity", "THIS IS THE DATE: " + dateSelected.toString());
        final Button dateButton = (Button) findViewById(R.id.dateButton);

        // Todo: Trying to set the text of the button doesn't seem to work very well. Set a text field with the selected date?
//        dateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dateButton.setText((CharSequence) dateSelected);
//            }
//        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
