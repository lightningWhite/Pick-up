package com.pickup.daniel.pick_up;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Daniel on 10/21/2017.
 */

public class PopulateListTask extends AsyncTask<Void, String, Void> {

    // 13 Game types
    String games[] = {"Basketball", "Badminton", "Football", "Golf", "Hockey",
            "Raquetball", "Rugby", "Softball", "Soccer", "Spike Ball", "Ultimate Frisbee",
            "Volley Ball", "Walley Ball"};

    String days[] = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};

    // 16 players
    String players[] = {"Joe", "Sally", "John", "Caleb", "Kevin", "Corey", "Robert", "Ryan",
            "Vanessa", "Connor", "Tucker", "Annie", "Karley", "Kendra", "Samantha", "Kayla"};

    // Four Outdoor locations
    String outdoorLocations[] = {"Upper Playing Fields", "Football field",
            "Porter Park", "Lower Playing Fields"};

    // Two indoor locations
    String indoorLocations[] = {"I-Center Courts", "Hart Gym"};

    Context _currentContext;
    // We need to pass the Array Adapter here so we can use it
    ArrayAdapter<String> _theAdapter;
    List<Game> _games;

    PopulateListTask(ArrayAdapter<String> theAdapter, Context theContext, List<Game> games) {

        _theAdapter = theAdapter;
        _currentContext = theContext;
        _games = games;
    }

    // Main thread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    // Separate thread. We can't update the list from this method because it would access the GUI
    @Override
    protected Void doInBackground(Void... params) {
        // Generate 35 random game configurations
        int numGames = 35;

        String listItem = "";
        String game;

        int hour = 0;
        String day;

        int date = 0;
        int numPlayers = 0;

        // Random object used to randomly pick configurations
        Random rand = new Random();
        int low = 0;
        int high = 0;

        for (int i = 0; i < numGames; i++) {
            listItem = "";
            // Generate a random game
            low = 0;
            high = 13;
            game = games[rand.nextInt(high-low) + low];

            // Generate an hour between 3 and 10
            low = 3;
            high = 10 + 1;
            hour = rand.nextInt(high-low) + low;

            // Generate a random day between Monday and Saturday
            low =0;
            high = 5;
            day = days[rand.nextInt(high-low) + low];

            // Generate a random day of the month
            low = 1;
            high = 31;
            date = rand.nextInt(high-low) + low;

            // Generate a random number of players
            low = 1;
            high = 16;
            numPlayers = rand.nextInt(high-low) + low;

            // Create the string that will be displayed in the list view
            listItem = game + " - " + hour + ":00PM, " + day + " " + date + " - Players: " + numPlayers;
            //listItem.format("%s -- %d:00PM, %s %d -- Players: %d", game, hour, day, date, numPlayers);
            publishProgress(listItem);
            Log.d("PopulateListTask", listItem);

            Game newGame = new Game();
            newGame.setComments("Come for a great game!");
            newGame.setDate(day + " " + date);

            // Generate a location
            if (game == "Football" || game == "Rugby" || game == "Softball" || game == "Soccer"
                    || game == "Spikeball" || game == "Ultimate Frisbee") {
                low = 0;
                high = 4;
                newGame.setLocation(outdoorLocations[rand.nextInt(high - low)]);
            }
            else if (game == "Basketball" || game == "Badminton" || game == "Hockey" || game == "Volley Ball") {
                low = 0;
                high = 2;
                newGame.setLocation(indoorLocations[rand.nextInt(high - low)]);
            }
            else if (game == "Racquetball" || game == "Walley Ball") {
                newGame.setLocation("Racquetball Courts");
            }
            else if (game == "Golf") {
                newGame.setLocation("Golf Course");
            }
            else {
                newGame.setLocation("TBD");
            }

            // Generate a list of players
            for (int j = 0; j < numPlayers; j++) {
                List<String> playerList = new ArrayList<>();

                low = 0;
                high = 16;
                playerList.add(players[rand.nextInt(high - low)]);
                newGame.setPlayers(playerList);
            }

            _games.add(newGame);
        }

        return null;
    }

    // Main thread. This method can modify the GUI
    @Override
    protected void onProgressUpdate(String... values) {
        _theAdapter.add(values[0]);
        super.onProgressUpdate(values);
    }

    // Main thread.
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
