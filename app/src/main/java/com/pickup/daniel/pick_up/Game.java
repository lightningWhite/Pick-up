package com.pickup.daniel.pick_up;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Daniel on 10/28/2017.
 */

public class Game {
    String gameType;
    String comments;
    String date;
    String time;
    String location;
    String listString;
    int numPlayers;

    // This filter data will be used for the predicate in the sorting of the games list
    // Index: 0 = GameType, 1 = Hour.Minute, 2 = Month.DayOfMonth, 3 = NumPlayers
    List<Float> filterData = new ArrayList<>();

    List<String> players;

    public void Game() {
        gameType = "";
        comments = "";
        time = "";
        date = "";
        location = "";
        players = new ArrayList<>();
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
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

    public String getListString() {
        listString = gameType + " - " + time + ", " + date + " - Players: " + numPlayers;
        return listString;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public List<Float> getFilterData() {
        return filterData;
    }

    public void setFilterData(List<Float> filterData) {
        this.filterData = filterData;
    }

    /*Comparator for sorting the list by game type*/
    public static Comparator<Game> GameTypeComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            String gameType1 = g1.getGameType();
            String gameType2 = g2.getGameType();

            /*For ascending order*/
	        return gameType1.compareTo(gameType2);
        }};

    /*Comparator for sorting the list by date*/
    public static Comparator<Game> DateComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            String date1 = g1.getDate();
            String date2 = g2.getDate();

            /*For ascending order*/
            return date1.compareTo(date2);
        }};

    /*Comparator for sorting the list by time*/
    public static Comparator<Game> TimeComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            String time1 = g1.getTime();
            String time2 = g2.getTime();

            /*For ascending order*/
            return time1.compareTo(time2);
        }};

    /*Comparator for sorting the list by number of players*/
    public static Comparator<Game> NumPlayersComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            int num1 = g1.getNumPlayers();
            int num2 = g2.getNumPlayers();

            /*For ascending order*/
            return num1 - num2;
        }};
}
