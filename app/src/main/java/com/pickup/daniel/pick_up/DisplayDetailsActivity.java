package com.pickup.daniel.pick_up;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

public class DisplayDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);

        Intent intent = getIntent();
        String jsonGame = intent.getStringExtra(MainActivity.EXTRA_GAME);

        Gson gson = new Gson();
        Game game = gson.fromJson(jsonGame, Game.class);

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

    String m_Text;

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
                m_Text = input.getText().toString();
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
}
