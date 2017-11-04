package com.pickup.daniel.pick_up;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 10/28/2017.
 */

public class Game {
    String comments;
    String date;
    String time;
    String location;
    List<String> players;

    public void Game() {
        comments = "";
        time = "";
        date = "";
        location = "";
        players = new ArrayList<>();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
