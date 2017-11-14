package com.pickup.daniel.pick_up;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener {
    public static final String EXTRA_GAME = "com.example.daniel.GAME";
    final String GAMES_FILE = "savedGames";
    final String GAME_KEY = "gameKey";

    GameAdapter _gameAdapter;
    ArrayList<Game> _gamesList = new ArrayList<>();
    Date dateFromDatePicker = new Date();
    Date dateSelected = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // This will be used to restore the activity when going from one to another
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This makes it so the keyboard doesn't push up the buttons
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /* Game selector spinner */
        Spinner gameSpinner = (Spinner) findViewById(R.id.gamesSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> gameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.games_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        gameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        gameSpinner.setAdapter(gameSpinnerAdapter);

        /* Number of players selector spinner */
        Spinner numPlayersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> numPlayersSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.num_players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        numPlayersSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        numPlayersSpinner.setAdapter(numPlayersSpinnerAdapter);

        // Load the SharedPreferences file containing the _gamesList that were saved for activity switches
        SharedPreferences contactPref = this.getSharedPreferences(GAMES_FILE, MODE_PRIVATE);
        Gson gson = new Gson();

        // Get the stored json list of _gamesList. This populates the list. Note: This line prevents it from populating the list in the PopulateListTask
        _gamesList = gson.fromJson(contactPref.getString(GAME_KEY, null),
                new TypeToken<ArrayList<Game>>() {
                }.getType());

        // If there are no saved games in the shared preferences, allocate a new ArrayList
        if (_gamesList == null) {
            _gamesList = new ArrayList<>();
        }

        // Create the games adapter so we can populate the gamesListView
        _gameAdapter = new GameAdapter(this, android.R.layout.simple_selectable_list_item, _gamesList);
        final ListView gamesListView = (ListView) findViewById(R.id.gamesListView);
        gamesListView.setAdapter(_gameAdapter);

        // Initially populate the list with random games if none exist in the preferences to load
        if (_gamesList.isEmpty()) {
            Log.d("MainActivity", "The _gamesList list was empty. Generating new _gamesList...");
            // Instantiate our AsyncTask class for generating a list of _gamesList and populating the  list
            PopulateListTask populateListTask = new PopulateListTask(_gameAdapter, MainActivity.this, _gamesList);
            populateListTask.execute();
        }

        /* Listener for when a game is selected to show the details of the game */
        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Game selectedFromList =(Game) (gamesListView.getItemAtPosition(myItemInt));
                Log.d("MainActivity", selectedFromList.getListString());

                // Convert the game object to JSON so I can pass it to the details activity
                Gson gson = new Gson();
                String jsonGame = gson.toJson(_gamesList.get(myItemInt));

                // Start the details activity
                Intent intent = new Intent(MainActivity.this, DisplayDetailsActivity.class);
                intent.putExtra(EXTRA_GAME, jsonGame);
                startActivity(intent);
            }
        });
    }

    /**
     * This method saves all of the games in the gamesList so they can be preserved when changing
     * between activities.
     */
    @Override
    public void onPause() {

        super.onPause();  // Always call the superclass method first
        SharedPreferences mPrefs = getSharedPreferences(GAMES_FILE, MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        // Convert the contacts list into a json string
        String json = gson.toJson(_gamesList);
        Log.d("MainActivity", json);

        prefsEditor.putString(GAME_KEY, json);
        prefsEditor.commit();
    }

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
