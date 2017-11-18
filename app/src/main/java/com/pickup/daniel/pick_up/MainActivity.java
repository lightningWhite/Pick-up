package com.pickup.daniel.pick_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, TimePickerFragment.TimePickerFragmentListener {
    public static final String EXTRA_GAME = "com.example.daniel.GAME";
    final String GAMES_FILE = "savedGames";
    final String GAME_KEY = "gameKey";
    final String TAG = "MainActivity";

    GameAdapter _gameAdapter;
    ArrayList<Game> _gamesMasterList = new ArrayList<>(); // Master games list
    ArrayList<Game> _gamesDisplayList = new ArrayList<>(); // Filtered display games list
    ArrayList<Game> _tempFilterList = new ArrayList<>(); // Temp list used for filtering and sorting
    String dateFromDatePicker;
    int hourOfDayFromTimePicker;
    int minuteFromTimePicker;

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

        // Load the SharedPreferences file containing the _gamesMasterList that were saved for activity switches
        SharedPreferences contactPref = this.getSharedPreferences(GAMES_FILE, MODE_PRIVATE);
        Gson gson = new Gson();

        // Get the stored json list of _gamesMasterList. This populates the list. Note: This line prevents it from populating the list in the PopulateListTask
        _gamesMasterList = gson.fromJson(contactPref.getString(GAME_KEY, null),
                new TypeToken<ArrayList<Game>>() {
                }.getType());

        // If there are no saved games in the shared preferences, allocate a new ArrayList
        if (_gamesMasterList == null) {
            _gamesMasterList = new ArrayList<>();
        }

        // Create the games adapter so we can populate the gamesListView
        _gameAdapter = new GameAdapter(this, android.R.layout.simple_selectable_list_item, _gamesDisplayList);
        final ListView gamesListView = (ListView) findViewById(R.id.gamesListView);
        gamesListView.setAdapter(_gameAdapter);

        // Initially populate the list with random games if none exist in the preferences to load
        if (_gamesMasterList.isEmpty()) {
            Log.d("MainActivity", "The _gamesMasterList list was empty. Generating new _gamesMasterList...");
            // Instantiate our AsyncTask class for generating a list of _gamesMasterList and populating the  list
            PopulateListTask populateListTask = new PopulateListTask(_gameAdapter,MainActivity.this, _gamesMasterList, _gamesDisplayList);
            populateListTask.execute();
        } else {
            // Start out with the _gamesDisplayList filled with all the items in the master games list
            _gamesDisplayList.addAll(_gamesMasterList);
            Log.d(TAG, "Number of items in the _gamesDisplayList = " + Integer.toString(_gamesDisplayList.size()));
            //_gameAdapter.updateGamesList(_gamesMasterList);
        }

        /* Listener for when a game is selected to show the details of the game */
        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Game selectedFromList =(Game) (gamesListView.getItemAtPosition(myItemInt));
                Log.d("MainActivity", selectedFromList.getListString());

                // Convert the game object to JSON so I can pass it to the details activity
                Gson gson = new Gson();
                String jsonGame = gson.toJson(_gamesMasterList.get(myItemInt));

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
        String json = gson.toJson(_gamesMasterList);
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
        dateFromDatePicker = date.toString();
        Log.d("MainActivity", "THE ON DATE SET METHOD IS GETTING NOTIFIED!!!!!! " + dateFromDatePicker.toString());
        TextView selectedDate = (TextView) findViewById(R.id.selectedDate);

        String simpleDate = "";

        // Place each character of the variable into a string until end of block or end of
        // string
        int i = 0;
        int spaceCount = 0;
        while (i < dateFromDatePicker.length() && spaceCount < 3) {
            simpleDate += dateFromDatePicker.charAt(i);

            if (dateFromDatePicker.charAt(i) == ' ') {
                spaceCount++;
            }
            // Go to the next letter in the variable
            i++;
        }
        selectedDate.setText(simpleDate);
    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        // This method will be called with the date from the `DatePicker`.
        hourOfDayFromTimePicker = hourOfDay;
        minuteFromTimePicker = minute;

        String amPm = "";
        if (hourOfDayFromTimePicker > 11) {
            amPm = "PM";
            hourOfDayFromTimePicker %= 12; // convert from 24 hour

            if (hourOfDayFromTimePicker == 12) {
                hourOfDayFromTimePicker = 12;
            }
        }
        else {
            amPm = "AM";
        }

        if (hourOfDayFromTimePicker == 0) {
            hourOfDayFromTimePicker = 12;
        }

        String minuteForm = "";
        if (minuteFromTimePicker < 10) {
            minuteForm = "0" + Integer.toString(minute);
        }
        else {
            minuteForm = Integer.toString(minute);
        }

        String displayTime = Integer.toString(hourOfDayFromTimePicker) + ":" + minuteForm + amPm;

        Log.d("MainActivity", "THE ON TIME SET METHOD IS GETTING NOTIFIED!!!!!! " +
                displayTime);

        TextView selectedTime = (TextView) findViewById(R.id.selectedTime);
        selectedTime.setText(displayTime);

    }

    public void onDateButton(View view) {
        DatePickerFragment fragment = DatePickerFragment.newInstance(this);
        fragment.show(getFragmentManager(), "datePicker");
        // The date will be sent to the onDateSet listener
    }

    public void onTimeButton(View view) {
        TimePickerFragment fragment = TimePickerFragment.newInstance(this);
        fragment.show(getFragmentManager(), "datePicker");
        // The date will be sent to the onDateSet listener
    }

//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getFragmentManager(), "datePicker");
//    }
//
//    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getFragmentManager(), "timePicker");
//    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        //boolean checked = ((CheckBox) view).isChecked();
        Log.d(TAG, "THIS IS IN IN THE ON CHECK BOX CLICKED!!!");
        filterGamesDisplayList();
    }

//    public void onFilterGameType() {
//        // Toggle a boolean indicating the box is checked. Call filterGamesList()
//        _isGameChecked = !_isGameChecked;
//        filterGamesDisplayList();
//    }
//
//    public void onFilterDate() {
//        // Toggle a boolean indicating the box is checked. Call filterGamesList()
//        _isDateChecked = !_isDateChecked;
//        filterGamesDisplayList();
//
//    }
//
//    public void onFilterTime() {
//        // Toggle a boolean indicating the box is checked. Call filterGamesList()
//        _isTimeChecked = !_isTimeChecked;
//        filterGamesDisplayList();
//
//    }
//
//    public void onFilterNumPlayers() {
//        // Toggle a boolean indicating the box is checked. Call filterGamesList()
//        _isPlayersChecked = !_isPlayersChecked;
//        filterGamesDisplayList();
//
//    }

    public void filterGamesDisplayList() {
        // Check which boxes are checked, get values associated with those boxes,
        // Loop through the master games list and add only the ones that meet the selected values
        // If only the default values are selected,

        // Start with a fresh list
        _tempFilterList.clear();

        // Get the gamesSpinner
        Spinner gamesSpinner = (Spinner) findViewById(R.id.gamesSpinner);
        String selectedGameString = "";

        // Get the selected date
        TextView selectedDate = (TextView) findViewById(R.id.selectedDate);
        String selectedDateString = selectedDate.getText().toString();

        // Get the selected time
        TextView selectedTime = (TextView) findViewById(R.id.selectedTime);
        String selectedTimeString = selectedTime.getText().toString();

        Spinner playersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        int numPlayers = 0;

        boolean isGameChecked = ((CheckBox) findViewById(R.id.gameTypeCheckBox)).isChecked();
        boolean isDateChecked = ((CheckBox) findViewById(R.id.dateCheckBox)).isChecked();
        boolean isTimeChecked = ((CheckBox) findViewById(R.id.timeCheckBox)).isChecked();
        boolean isPlayersChecked = ((CheckBox) findViewById(R.id.numPlayersCheckBox)).isChecked();

        // Check if more than one check box is checked to know whether to sort or just filter
//        boolean doSort = true;
        int numChecks = 0;
        if (isGameChecked) {
            numChecks++;
            selectedGameString = gamesSpinner.getSelectedItem().toString();
        }
        if (isDateChecked) {
            numChecks++;
        }
        if (isTimeChecked) {
            numChecks++;
        }
        if (isPlayersChecked) {
            numChecks++;
            if (!playersSpinner.getSelectedItem().toString().contentEquals("Players")) {
                numPlayers = Integer.parseInt(playersSpinner.getSelectedItem().toString()); // TODO: BUG
            }
            else {
                numPlayers = 0;
            }
        }

//        if (numChecks > 1)
//            doSort = false;

        // Filter the list according to check boxes
        if (isGameChecked) {
            // Add all of the matching games to the tempList
            for (Game game : _gamesMasterList) {
                Log.d(TAG, game.getGameType());
                Log.d(TAG, "COMPARE TO: " );
                Log.d(TAG, selectedGameString);

                // If default value, add all games, and we'll sort them if it's the only checked one
                if (selectedGameString.equals("Game Selection")) {
                    _tempFilterList.add(game);
                }
                else if (game.getGameType().equals(selectedGameString)) {
                    _tempFilterList.add(game);
                }
            }
        }
        if (isDateChecked) {
            // Add all of the matching dates to the list
            for (Game game : _gamesMasterList) {
                Log.d(TAG, game.getDate());
                Log.d(TAG, "--COMPARE TO: " );
                Log.d(TAG, selectedDateString);

                if (selectedDateString.equals("Select a Date"))
                {
                    _tempFilterList.add(game);
                }
                else if (game.getDate().equals(selectedDateString)) {
                    _tempFilterList.add(game);
                }
            }
        }
        if (isTimeChecked) {
            for (Game game : _gamesMasterList) {
                Log.d(TAG, game.getTime());
                Log.d(TAG, "++COMPARE TO: " );
                Log.d(TAG, selectedTimeString);
                if (selectedTimeString.equals("Select a Time"))
                {
                    _tempFilterList.add(game);
                }
                else if (game.getTime().equals(selectedTimeString)) {
                    _tempFilterList.add(game);
                }
            }
        }
        if (isPlayersChecked) {
            for (Game game : _gamesMasterList) {
                Log.d(TAG, Integer.toString(game.getNumPlayers()));
                Log.d(TAG, "##COMPARE TO: " );
                Log.d(TAG, Integer.toString(numPlayers));
                if (numPlayers == 0) {
                    _tempFilterList.add(game);
                }
                if (game.getNumPlayers() == numPlayers) {
                    _tempFilterList.add(game);
                }
            }
        }

        // Sort the list if only one check box is checked Todo: and it's the default value
        if (numChecks == 1) {

            if (isGameChecked) {
                Collections.sort(_tempFilterList, Game.GameTypeComparator);
            } else if (isDateChecked) {
                Collections.sort(_tempFilterList, Game.DateComparator);

            } else if (isTimeChecked) {
                Collections.sort(_tempFilterList, Game.TimeComparator);

            } else if (isPlayersChecked) {
                Collections.sort(_tempFilterList, Game.NumPlayersComparator);

            }
        }
        else if (numChecks == 0) {
            // Fill it back up if no checks are present
            _tempFilterList.addAll(_gamesMasterList);
            //_tempFilterList = _gamesMasterList;
        }

        // Assign the display list to the filtered/sorted list and notify the adapter the set changed
        _gamesDisplayList.clear();
        _gamesDisplayList.addAll(_tempFilterList);
        Log.d(TAG, "THIS IS THE SIZE OF THE SORTED LIST: " + Integer.toString(_gamesDisplayList.size()));
        //_gameAdapter.updateGamesList(_gamesDisplayList);
        _gameAdapter.notifyDataSetChanged();
    }
}
