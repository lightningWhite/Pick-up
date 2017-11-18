package com.pickup.daniel.pick_up;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DisplayDetailsActivity extends AppCompatActivity {

    final String GAMES_FILE = "savedGames";
    final String GAME_KEY = "gameKey";

    private List<Game> _gamesList;
    private int _gamePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);

        Intent intent = getIntent();
        String jsonGamePos = intent.getStringExtra(MainActivity.EXTRA_GAME_POS);
        //String jsonGame = intent.getStringExtra(MainActivity.EXTRA_GAME);

        Gson gson = new Gson();

        // Get the _gamesMasterList
        SharedPreferences gamesPref = this.getSharedPreferences(GAMES_FILE, MODE_PRIVATE);
        _gamesList = gson.fromJson(gamesPref.getString(GAME_KEY, null),
                new TypeToken<ArrayList<Game>>() {
                }.getType());

        //String stringGamePos = gamesPref.getString(MainActivity.EXTRA_GAME_POS, null);
        _gamePos = Integer.parseInt(jsonGamePos);

        Game game;
        game = _gamesList.get(_gamePos);

        // Get the stored master position so we can update the right game
        _gamePos = game.getPositionInMasterList();

        TextView gameType = (TextView) findViewById(R.id.gameType);
        gameType.setText(game.gameType);

        TextView commentBlock = (TextView) findViewById(R.id.commentBlock);
        commentBlock.setText(game.comments);

        TextView whenBlock = (TextView) findViewById(R.id.whenBlock);
        whenBlock.setText(game.getTime() + ", " + game.getDate());

        TextView whereBlock = (TextView) findViewById(R.id.whereBlock);
        whereBlock.setText(game.getLocation());

        TextView playersBlock = (TextView) findViewById(R.id.playersBlock);
        playersBlock.setText("");
        List<String> players = game.getPlayers();
        for(int i = 0; i < players.size(); i++) {
            Log.d("GamePlayerSize", Integer.toString(game.getPlayers().size()));
            if (i < players.size() - 1) {
                playersBlock.append(players.get(i) + ", ");
            }
            else {
                // Don't put a comma after the last one
                playersBlock.append(players.get(i));
            }
        }
    }

    String playerName;

    public void onJoin(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name to join!");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playerName = input.getText().toString();
                onSaveListen();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void onSaveListen() {
        // Add the player name to the list of players that have joined the game
        _gamesList.get(_gamePos).getPlayers().add(playerName);
        // Increment the number of players that have joined the game
        _gamesList.get(_gamePos).setNumPlayers(_gamesList.get(_gamePos).getNumPlayers() + 1);

        TextView playersBlock = (TextView) findViewById(R.id.playersBlock);
        playersBlock.setText("");
        List<String> players = _gamesList.get(_gamePos).getPlayers();
        for(int i = 0; i < players.size(); i++) {
            Log.d("GamePlayerSize", Integer.toString(_gamesList.get(_gamePos).getPlayers().size()));
            if (i < players.size() - 1) {
                playersBlock.append(players.get(i) + ", ");
            }
            else {
                // Don't put a comma after the last one
                playersBlock.append(players.get(i));
            }
        }

        // Update the shared preferences with the edited game
        SharedPreferences gamesPref = this.getSharedPreferences(GAMES_FILE, MODE_PRIVATE);
        Gson gson = new Gson();

        SharedPreferences.Editor prefsEditor = gamesPref.edit();

        // Convert the games list into a json string
        String json = gson.toJson(_gamesList);
        Log.d("MainActivity", json);

        // Update the _gamesMasterList with the modified _game
        prefsEditor.putString(GAME_KEY, json);
        prefsEditor.commit();

    }
}

