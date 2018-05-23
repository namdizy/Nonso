package nonso.android.nonso.utils;

import com.google.gson.Gson;

import nonso.android.nonso.models.Journey;

public class JourneyUtils {


    public Journey loadJourneyFromString(String gsonString){

        Gson gson = new Gson();

        return gson.fromJson(gsonString, Journey.class);
    }

    public String loadStringFromJourney(Journey jouney){
        Gson gson = new Gson();
        String temp = gson.toJson(jouney);
        return  gson.toJson(jouney);
    }
}
