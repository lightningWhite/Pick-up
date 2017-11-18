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
    String day;
    int dayOfMonth;
    int month;
    String date; // Day Month dayOfMonth
    String time;
    int hour;
    String location;
    String listString;
    int numPlayers;
    String _compareDate;
    int positionInMasterList;

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

    public String get_compareDate() {
        return _compareDate;
    }

    public void set_compareDate(String compareDate) {
        this._compareDate = compareDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getPositionInMasterList() {
        return positionInMasterList;
    }

    public void setPositionInMasterList(int positionInMasterList) {
        this.positionInMasterList = positionInMasterList;
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

            //int month1 = g1.getMonth();
            int dayOfMonth1 = g1.getDayOfMonth();
            //int month2 = g2.getMonth();
            int dayOfMonth2 = g2.getDayOfMonth();

            //?????int monthComp = month1 - month2;
            return  dayOfMonth1 - dayOfMonth2;


//            String date1 = g1.getDate();
//            String date2 = g2.getDate();

            // Place each character of the variable into a string until end of block or end of
            // string
//            int i = 0;
//            int spaceCount = 0;
//            String dayOfMonth1 = "";
//            while (i < date1.length() && spaceCount < 2) {
//                if (date1.charAt(i) == ' ') {
//                    spaceCount++;
//                }
//                // Go to the next letter in the variable
//                i++;
//            }
//            while (i < date1.length()) {
//                dayOfMonth1 += date1.charAt(i);
//            }
//
//            i = 0;
//            spaceCount = 0;
//            String dayOfMonth2 = "";
//            while (i < date2.length() && spaceCount < 2) {
//                if (date2.charAt(i) == ' ') {
//                    spaceCount++;
//                }
//                // Go to the next letter in the variable
//                i++;
//            }
//            while (i < date2.length()) {
//                dayOfMonth2 += date2.charAt(i);
//            }
//
//            int d1 = Integer.parseInt(dayOfMonth1);
//            int d2 = Integer.parseInt(dayOfMonth2);
//
//
//
//            /*For ascending order*/
//            return d1 - d2;
            //return date1.compareTo(date2);
        }};

    /*Comparator for sorting the list by time*/
    public static Comparator<Game> TimeComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            int hour1 = g1.getHour();
            int hour2 = g2.getHour();

            /*For ascending order*/
            return hour1 - hour2;
        }};

    /*Comparator for sorting the list by number of players*/
    public static Comparator<Game> NumPlayersComparator = new Comparator<Game>() {

        public int compare(Game g1, Game g2) {

            int num1 = g1.getNumPlayers();
            int num2 = g2.getNumPlayers();

            /*For ascending order to show most developed games to least developed games*/
            return num2 - num1;
        }};
}
