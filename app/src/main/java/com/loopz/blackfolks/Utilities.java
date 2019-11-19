package com.loopz.blackfolks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static final String AMOUNT = "amount";
    public static String KEY = "key";
    public static String baseUrl = "https://boundary.com/api";
    public static String ID = "id";
    public static String QUESTION = "question";
    public static String TIME = "time";
    public static String USERNAME = "username";
    public static String READY = "ready";
    public static String SCORE = "score";
    public static String ANSWER = "answer";
    public static String OPTION1 = "option1";
    public static String OPTION2 = "option2";
    public static String MEATCH_ID = "meatch_id";
    public static String MATCH_ID = "match_id";
    public static String CONTEST_ID = "contests_id";
    public static String TEAM_ID = "team_id";
    public static String USER_ID = "user_id";
    public static String BAT = "bat";
    public static String BOWL = "bowl";
    public static String WK = "wk";
    public static String ALL = "all";
    public static String TOTAL = "total";
    public static String TEAM_ONE = "teamOne";
    public static String TEAM_TWO = "teamTwo";
    public static String upcoming = "upcoming";
    public static String live = "live";
    public static String completed = "completed";
    public static String SCHEDULED = "1";
    public static String COMPLETED = "2";
    public static String LIVE = "3";
    public static String CRICKET = "1";
    public static String FOOTBALL = "2";
    public static String KABADDI = "3";
    public static int TEAM_LIST = 1;
    public static int RESULT_TEAM_LIST = 2;
    public static int PICK_TEAM = 3;
    public static int JOINED_SCREEN = 0;
    public static int RESULT_SCREEN = 1;

    public static String addCountryCode(String number) {
        if (number.startsWith("+91")) {
            return number;
        } else {
            return "+91" + number;
        }
    }

    public static boolean checkPatternEmail(String emailAddress) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.find();
    }

    public static boolean checkPatternNumbers(String numbers, int min, int max) {
        Pattern VALID_NUMBER_REGEX =
                Pattern.compile("^[0-9]{" + min + "," + max + "}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_NUMBER_REGEX.matcher(numbers);
        return matcher.find();
    }

    public static boolean checkAlphaNumeric(String numbers, int min, int max) {
        Pattern VALID_NUMBER_REGEX =
                Pattern.compile("^[A-Za-z0-9]{" + min + "," + max + "}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_NUMBER_REGEX.matcher(numbers);
        return matcher.find();
    }


    public static boolean checkNotNull(String text) {
        if (text.equals("") || text == null) {
            return false;
        }
        //if not null
        return true;
    }

    public static boolean checkCharOnly(String text) {
        Pattern SPECIAL_CHAR_REGEX =
                Pattern.compile(String.format("[^A-Za-z /\n]"), Pattern.CASE_INSENSITIVE);

        Matcher matcher = SPECIAL_CHAR_REGEX.matcher(text);
        return !matcher.find();
    }

    public static boolean checkSpecialChar(String text) {
        Pattern SPECIAL_CHAR_REGEX =
                Pattern.compile(String.format("[^A-Za-z0-9 /\n]"), Pattern.CASE_INSENSITIVE);

        Matcher matcher = SPECIAL_CHAR_REGEX.matcher(text);
        return !matcher.find();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static boolean getBoolean(char s) {
        if (s == '0') {
            return false;
        } else {
            return true;
        }
    }
}
