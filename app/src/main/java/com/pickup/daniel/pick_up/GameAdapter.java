package com.pickup.daniel.pick_up;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 11/9/2017.
 */

public class GameAdapter extends ArrayAdapter<Game> {

    List<Game> _items;

    private static class ViewHolder {
        private TextView itemView;
    }

    public GameAdapter(Context context, int textViewResourceId, List<Game> items) {
        super(context, textViewResourceId, items);
        _items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Game game = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_game, parent, false);
        }
        // Lookup view for data population
        TextView gameListString = (TextView) convertView.findViewById(R.id.gameListString);
        gameListString.setText(game.getListString());

//        TextView gameTypeField = (TextView) convertView.findViewById(R.id.gameTypeField);
//        TextView timeField = (TextView) convertView.findViewById(R.id.timeField);
//        TextView dateField = (TextView) convertView.findViewById(R.id.dateField);
//        TextView numPlayersField = (TextView) convertView.findViewById(R.id.numPlayersField);
//
//        // Populate the data into the template view using the data object
//        gameTypeField.setText(game.getGameType());
//        gameTypeField.setText(game.getTime());
//        gameTypeField.setText(game.getDate());
//        gameTypeField.setText(game.getNumPlayers());



        // Return the completed view to render on screen
        return convertView;
    }

    public void updateGamesList(List<Game> newlist) {
        _items.clear();
        _items.addAll(newlist);
        this.notifyDataSetChanged();
    }
}