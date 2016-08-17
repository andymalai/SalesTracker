package com.webmne.salestracker.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.webmne.salestracker.agent.model.AgentModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xitij on 17-03-2015.
 */
public class PrefUtils {

    private static final String USER_PROFILE = "USER_PROFILE";
    private static final String USER_ID = "USER_ID";
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String DELETE = "DELETE";

    public static void setLoggedIn(Context context, boolean isLoggedIn) {
        Prefs.with(context).save(IS_LOGGED_IN, isLoggedIn);
    }

    public static boolean getLoggedIn(Context context) {
        return Prefs.with(context).getBoolean(IS_LOGGED_IN, false);
    }

    /*public static void setUserProfile(Context context, LoginResponse response) {
        String toJson = ZemullaApplication.getGson().toJson(response);
        setUserID(context, response.getUserID());
        Prefs.with(context).save(USER_PROFILE, toJson);
    }*/

    public static void setUserID(Context context, int userID) {
        Prefs.with(context).save(USER_ID, userID);
    }

    public static int getUserID(Context context) {
        return Prefs.with(context).getInt(USER_ID, 0);
    }

   /* public static LoginResponse getUserProfile(Context context) {
        Gson gson =ZemullaApplication.getGson();
        LoginResponse response = null;

        String jsonString = Prefs.with(context).getString(USER_PROFILE, "");
        try {
            response = gson.fromJson(jsonString, LoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }*/

    public static List<AgentModel> getDeleteAgents(Context context) {
        List<AgentModel> deleteAgents;

        String jsonString = Prefs.with(context).getString(DELETE, "");
        if (jsonString == null) {
            return null;

        } else {
            Gson gson = new Gson();
            AgentModel[] agents = gson.fromJson(jsonString, AgentModel[].class);
            if (agents == null) {
                return null;
            } else {
                deleteAgents = Arrays.asList(agents);
                deleteAgents = new ArrayList<AgentModel>(deleteAgents);
                return deleteAgents;
            }
        }
    }

    public static void setAgent(Context context, AgentModel agentModel) {

        List<AgentModel> agents = getDeleteAgents(context);
        if (agents == null) {
            agents = new ArrayList<>();
        }
        if (agents.contains(agentModel)) {
            agents.remove(agentModel);
        } else {
            agents.add(agentModel);
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(agents);
        Prefs.with(context).save(DELETE, jsonString);
    }

    public static void deselectAll(Context context) {
        List<AgentModel> agents = getDeleteAgents(context);
        if (agents == null) {
            agents = new ArrayList<>();
        } else {
            agents.clear();
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(agents);
        Prefs.with(context).save(DELETE, jsonString);
    }
}
