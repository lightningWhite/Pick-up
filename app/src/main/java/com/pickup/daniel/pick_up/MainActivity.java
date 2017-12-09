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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
    public static final String EXTRA_GAME_POS = "com.example.daniel.GAME_POS";
    final String GAMES_FILE = "savedGames";
    final String GAME_KEY = "gameKey";
    final String TAG = "MainActivity";

    GameAdapter _gameAdapter;
    ArrayList<Game> _gamesMasterList = new ArrayList<>(); // Master games list
    ArrayList<Game> _gamesDisplayList = new ArrayList<>(); // Filtered display games list
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

        // Comment out this chunk to generate new data
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
                //String jsonGame = gson.toJson(_gamesMasterList.get(myItemInt));
                String jsonGamePos = gson.toJson(selectedFromList.getPositionInMasterList()); // TODO: I should just pass the game that has its own master index

                // Start the details activity
                Intent intent = new Intent(MainActivity.this, DisplayDetailsActivity.class);
                //intent.putExtra(EXTRA_GAME, jsonGame);
                intent.putExtra(EXTRA_GAME_POS, jsonGamePos);
                startActivity(intent);
            }
        });

        // Re-run the search if the selection is changed and the checkbox is checked
        Spinner gamesSpinner = (Spinner) findViewById(R.id.gamesSpinner);
        gamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Only rerun the filter if the checkbox is checked
                if (((CheckBox) findViewById(R.id.gameTypeCheckBox)).isChecked()) {
                    filterGamesDisplayList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // Re-run the search if the selection is changed and the checkbox is checked
        Spinner playersSpinner = (Spinner) findViewById(R.id.numPlayersSpinner);
        playersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Only rerun the filter if the checkbox is checked
                if (((CheckBox) findViewById(R.id.numPlayersCheckBox)).isChecked()) {
                    filterGamesDisplayList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        // Display the number of games found meeting the search criteria above the games ListView
        TextView numGamesTxt = (TextView) findViewById(R.id.numGames);
        int numGames = _gamesDisplayList.size();
        String gameText = "";
        if (numGames == 1) {
            gameText = "Game";
        }
        else {
            gameText = "Games";
        }
        numGamesTxt.setText(Integer.toString(numGames) + " " + gameText);
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
//        TextView selectedDate = (TextView) findViewById(R.id.selectedDate);

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
//        selectedDate.setText(simpleDate);

        // Update the time shown on the button
        Button dateButton = (Button) findViewById(R.id.dateButton);
        CharSequence btnDate = simpleDate;
        dateButton.setText(btnDate);

        // Re-run the filter if the checkbox is selected
        if (((CheckBox) findViewById(R.id.timeCheckBox)).isChecked()) {
            filterGamesDisplayList();
        }


    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        // This method will be called with the time from the `TimePicker`.
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

//        TextView selectedTime = (TextView) findViewById(R.id.selectedTime);
//        selectedTime.setText(displayTime);

        // Update the time shown on the button
        Button timeButton = (Button) findViewById(R.id.timeButton);
        CharSequence btnTime = displayTime;
        timeButton.setText(btnTime);

        // Re-run the filter if the checkbox is checked
        if (((CheckBox) findViewById(R.id.timeCheckBox)).isChecked()) {
            filterGamesDisplayList();
        }


    }

    public void onDateButton(View view) {
        DatePickerFragment fragment = DatePickerFragment.newInstance(this);
        fragment.show(getFragmentManager(), "datePicker");
        //((CheckBox) findViewById(R.id.dateCheckBox)).setChecked(false);
        // The date will be sent to the onDateSet listener
    }

    public void onTimeButton(View view) {
        TimePickerFragment fragment = TimePickerFragment.newInstance(this);
        fragment.show(getFragmentManager(), "datePicker");
        //((CheckBox) findViewById(R.id.timeCheckBox)).setChecked(false);
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
        ArrayList<Game> tempFilterList = new ArrayList<>(); // Temp list used for filtering and sorting
        ArrayList<Game> multipleFilterList = new ArrayList<>(); // Temp list used for filtering with multiple filters

        // Get the gamesSpinner
        Spinner gamesSpinner = (Spinner) findViewById(R.id.gamesSpinner);
        String selectedGameString = "";

        // Get the selected date
//        TextView selectedDate = (TextView) findViewById(R.id.selectedDate);
//        String selectedDateString = selectedDate.getText().toString();

        // Get the selected date from the button
        Button dateButton = (Button) findViewById(R.id.dateButton);
        String selectedDateString = dateButton.getText().toString();
        Log.d(TAG, "THE DATE SELECTED FROM THE BUTTON IS " + selectedDateString);

        // Get the selected time
//        TextView selectedTime = (TextView) findViewById(R.id.selectedTime);
//        String selectedTimeString = selectedTime.getText().toString();
        Button timeButton = (Button) findViewById(R.id.timeButton);
        String selectedTimeString = timeButton.getText().toString();
        Log.d(TAG, "THE TIME SELECTED FROM THE BUTTON IS " + selectedTimeString);

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
                numPlayers = Integer.parseInt(playersSpinner.getSelectedItem().toString());
            }
            else {
                numPlayers = 0;
            }
        }

//        if (numChecks > 1)
//            doSort = false;

        // Used to do multiple filters
        boolean isFirstFilterDone = false;

        // Filter the list according to check boxes
        if (isGameChecked) {
            // Add from _gamesMasterList if only one is checked or if the first filter hasn't been done yet
            if (numChecks == 1 || !isFirstFilterDone) {
                isFirstFilterDone = true; // Indicate that the first filter has been performed
                // Add all of the matching games to the tempList
                for (Game game : _gamesMasterList) {
                    Log.d(TAG, game.getGameType());
                    Log.d(TAG, "COMPARE TO: ");
                    Log.d(TAG, selectedGameString);

                    // If default value, add all games, and we'll sort them if it's the only checked one
                    if (selectedGameString.equals("Game")) {
                        tempFilterList.add(game);
                    } else if (game.getGameType().equals(selectedGameString)) {
                        tempFilterList.add(game);
                    }
                }
            }
            else {
                // Pull from the tempFilterList the ones that fit the additional filter
                for (Game game : tempFilterList) {
                    Log.d(TAG, game.getGameType());
                    Log.d(TAG, "COMPARE TO: ");
                    Log.d(TAG, selectedGameString);

                    // If default value, add all games, and we'll sort them if it's the only checked one
                    if (selectedGameString.equals("Game")) {
                        multipleFilterList.add(game);
                    } else if (game.getGameType().equals(selectedGameString)) {
                        multipleFilterList.add(game);
                    }
                }
            }
        }

        if (isDateChecked) {
            if (numChecks == 1 || !isFirstFilterDone) {
                isFirstFilterDone = true; // Indicate that the first filter has been performed
                // Add all of the matching dates to the list
                for (Game game : _gamesMasterList) {
                    Log.d(TAG, game.getDate());
                    Log.d(TAG, "--COMPARE TO: ");
                    Log.d(TAG, selectedDateString);

                    if (selectedDateString.equals("Date")) {
                        tempFilterList.add(game);
                    } else if (game.getDate().equals(selectedDateString)) {
                        tempFilterList.add(game);
                    }
                }
            }
            else {
                for (Game game : tempFilterList) {
                    Log.d(TAG, game.getDate());
                    Log.d(TAG, "--COMPARE TO: ");
                    Log.d(TAG, selectedDateString);

                    if (selectedDateString.equals("Date")) {
                        multipleFilterList.add(game);
                    } else if (game.getDate().equals(selectedDateString)) {
                        multipleFilterList.add(game);
                    }
                }
            }
        }

        if (isTimeChecked) {
            if (numChecks == 1 || !isFirstFilterDone) {
                isFirstFilterDone = true; // Indicate that the first filter has been performed
                for (Game game : _gamesMasterList) {
                    Log.d(TAG, game.getTime());
                    Log.d(TAG, "++COMPARE TO: ");
                    Log.d(TAG, selectedTimeString);
                    if (selectedTimeString.equals("Time")) {
                        tempFilterList.add(game);
                    } else if (game.getTime().equals(selectedTimeString)) {
                        tempFilterList.add(game);
                    }
                }
            }
            else {
                for (Game game : tempFilterList) {
                    Log.d(TAG, game.getTime());
                    Log.d(TAG, "++COMPARE TO: ");
                    Log.d(TAG, selectedTimeString);
                    if (selectedTimeString.equals("Time")) {
                        multipleFilterList.add(game);
                    } else if (game.getTime().equals(selectedTimeString)) {
                        multipleFilterList.add(game);
                    }
                }
            }
        }

        if (isPlayersChecked) {
            if (numChecks == 1 || !isFirstFilterDone) {
                isFirstFilterDone = true; // Indicate that the first filter has been performed
                for (Game game : _gamesMasterList) {
                    Log.d(TAG, Integer.toString(game.getNumPlayers()));
                    Log.d(TAG, "##COMPARE TO: ");
                    Log.d(TAG, Integer.toString(numPlayers));
                    if (numPlayers == 0) {
                        tempFilterList.add(game);
                    }
                    if (game.getNumPlayers() == numPlayers) {
                        tempFilterList.add(game);
                    }
                }
            }
            else {
                for (Game game : tempFilterList) {
                    Log.d(TAG, Integer.toString(game.getNumPlayers()));
                    Log.d(TAG, "##COMPARE TO: ");
                    Log.d(TAG, Integer.toString(numPlayers));
                    if (numPlayers == 0) {
                        multipleFilterList.add(game);
                    }
                    if (game.getNumPlayers() == numPlayers) {
                        multipleFilterList.add(game);
                    }
                }
            }
        }

        // Sort the list if only one check box is checked
        if (numChecks == 1) {

            if (isGameChecked) {
                Collections.sort(tempFilterList, Game.GameTypeComparator);
            } else if (isDateChecked) {
                Collections.sort(tempFilterList, Game.DateComparator);

            } else if (isTimeChecked) {
                Collections.sort(tempFilterList, Game.TimeComparator);

            } else if (isPlayersChecked) {
                Collections.sort(tempFilterList, Game.NumPlayersComparator);

            }
        }
        else if (numChecks == 0) {
            // Fill it back up if no checks are present
            tempFilterList.addAll(_gamesMasterList);
            //_tempFilterList = _gamesMasterList;
        }

        // Assign the display list to the filtered/sorted list and notify the adapter the set changed
        _gamesDisplayList.clear();

        if (numChecks <= 1) {
            _gamesDisplayList.addAll(tempFilterList);
        }
        else if (numChecks > 1) {
            _gamesDisplayList.addAll(multipleFilterList);
        }
        Log.d(TAG, "THIS IS THE SIZE OF THE SORTED LIST: " + Integer.toString(_gamesDisplayList.size()));
        //_gameAdapter.updateGamesList(_gamesDisplayList);
        _gameAdapter.notifyDataSetChanged();

        TextView numGamesTxt = (TextView) findViewById(R.id.numGames);
        int numGames = _gamesDisplayList.size();
        String gameText = "";
        if (numGames == 1) {
            gameText = "Game";
        }
        else {
            gameText = "Games";
        }
        numGamesTxt.setText(Integer.toString(numGames) + " " + gameText);
    }
}
