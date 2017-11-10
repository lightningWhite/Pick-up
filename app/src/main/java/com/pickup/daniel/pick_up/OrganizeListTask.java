package com.pickup.daniel.pick_up;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Daniel on 11/4/2017.
 */

public class OrganizeListTask extends AsyncTask<Void, String, Void> {

    Context _currentContext;
    // We need to pass the Array Adapter here so we can use it
    ArrayAdapter<String> _theAdapter;
    List<Game> _games;

    OrganizeListTask(ArrayAdapter<String> theAdapter, Context theContext, List<Game> games) {

        _theAdapter = theAdapter;
        _currentContext = theContext;
        _games = games;
    }

    // Main thread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (Game game: _games) {
            String listItem = game.gameType + " - " +  game.getTime() + ", " +
                    game.getDate() + " - Players: " + game.getNumPlayers();
            publishProgress(listItem);
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
